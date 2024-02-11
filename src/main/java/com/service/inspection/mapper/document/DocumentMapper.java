package com.service.inspection.mapper.document;

import com.deepoove.poi.data.MergeCellRule;
import com.deepoove.poi.data.Numberings;
import com.deepoove.poi.data.Tables;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.service.inspection.document.DocumentModel;
import com.service.inspection.document.model.CategoryModel;
import com.service.inspection.document.model.CompanyModel;
import com.service.inspection.document.model.DefectModel;
import com.service.inspection.document.model.ImageModelWithDefects;
import com.service.inspection.dto.document.GptReceiverDto;
import com.service.inspection.entities.*;
import com.service.inspection.mapper.SenderMapper;
import com.service.inspection.service.DocumentModelService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.mapstruct.Named;
import org.mapstruct.*;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.RabbitMessageFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Mapper(
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        componentModel = "spring",
        uses = {ImageMapper.class, TableMapper.class}
)
@Slf4j
public abstract class DocumentMapper {

    @Autowired
    private DocumentModelService documentModelService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private AsyncRabbitTemplate template;

    @Autowired
    private SenderMapper senderMapper;

    @Mapping(target = "company", source = "inspection.company")
    @Mapping(source = "inspection.name", target = "projectName")
    @Mapping(source = "inspection.reportName", target = "reportName", defaultValue = "Технический отчет об обследовании")
    @Mapping(source = "inspection.script", target = "script")
    @Mapping(target = "categories", ignore = true)
    @Mapping(source = "inspection.employer", target = "employer")
    public abstract DocumentModel mapToDocumentModel(Inspection inspection, User user, @Context List<CompletableFuture<Void>> futureResult);

    @Mapping(source = "processedPhotos", target = "photos")
    @Mapping(source = "category.id", target = "categoryNum", defaultValue = "0L")
    @Mapping(source = "processedPhotos", target = "defectsWithPhotos")
    @Mapping(source = "processedPhotos", target = "defects", qualifiedByName = "processedPhotos")
    public abstract CategoryModel mapToCategoryModel(Category category, List<ImageModelWithDefects> processedPhotos);

    @Named("processedPhotos")
    public Set<String> processedPhotos(List<ImageModelWithDefects> defectsModel) {
        if (defectsModel == null) return null;

        

        return defectsModel.stream().flatMap(x -> x.getDefects().stream())
                .map(DefectModel::getName).collect(Collectors.toSet());
    }

    public Map<String, Pair<String, List<Long>>> processedPhotosMap(List<ImageModelWithDefects> defectsModel) {
        if (defectsModel == null) return Collections.emptyMap();
        return Collections.emptyMap();
    }


    @AfterMapping
    public void mappingAsyncPhotoFields(@MappingTarget DocumentModel documentModel, Inspection inspection, User user,
                                        @Context List<CompletableFuture<Void>> futureResult) {
        List<Category> categories = inspection.getCategories();
        categories.sort(Comparator.comparingLong(Category::getId));

        List<CompletableFuture<Void>> categoryFutureContext = Collections.synchronizedList(new ArrayList<>());
        Long lastPhotosCount = 1L;

        for (Category category : categories) {
            Hibernate.initialize(category.getPhotos());
            // часть генерации с анализом фотографий
            categoryFutureContext.add(
                    documentModelService.processAllPhotosAsync(category.getPhotos(), lastPhotosCount).thenAccept(x -> {

                        documentModel.addCategory(this.mapToCategoryModel(category, x));
                        log.debug("Add category {} to inspection {}", category.getId(), inspection.getId());
                    })
            );
            lastPhotosCount += category.getPhotos().size();
        }

        // часть генерации с GPT
        CompletableFuture<Void> modelWithUpdatedByGptModel = CompletableFuture
                .allOf(categoryFutureContext.toArray(new CompletableFuture[0])).thenApply(f -> {
                    try {
                        Message m = MessageBuilder.withBody(mapper.writeValueAsBytes(senderMapper.mapToGptSenderDto(documentModel)))
                                .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                                .setCorrelationId(UUID.randomUUID().toString()).build();
                        // реально в этом моменте обработку ошибок, потому что сейчас join максимально тонкое место
                        return template.sendAndReceive("nlm.task", m).handle((x, y) -> {
                         if (y != null) {
                             log.error("Error while waiting answer from gpt: {}", y.getMessage());
                             return null;
                         }
                         return x;
                        }).join();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }).thenAccept(x -> {
                    try {
                        if (x == null) return;
                        log.debug("Receive GPT analyze for inspection with id {}", inspection.getId());
                        GptReceiverDto dto = mapper.readValue(x.getBody(), GptReceiverDto.class);
                        senderMapper.updateDocumentModelWithGpt(documentModel, dto);
                    } catch (Exception e) {
                        return ;
                    }
                });


        futureResult.add(modelWithUpdatedByGptModel);

        UUID uuid = inspection.getMainPhotoUuid();
        if (uuid != null) {
            futureResult.add(documentModelService.processAbstractPhoto(uuid).thenAccept(documentModel::setMainPhoto));
        }

        Optional.ofNullable(inspection.getCompany()).map(Company::getLogoUuid).ifPresent(logoUuid -> {
            futureResult.add(documentModelService.processAbstractPhoto(logoUuid).thenAccept(x -> {
                documentModel.getCompany().setLogo(x);
            }));
        });

        Optional.ofNullable(inspection.getEmployer()).map(Employer::getSignatureUuid).ifPresent(signUuid -> {
            futureResult.add(documentModelService.processAbstractPhoto(signUuid).thenAccept(x -> {
                documentModel.getEmployer().setScript(x);
            }));
        });
    }

    abstract CompanyModel companyModel(Company company, @Context List<CompletableFuture<Void>> futureResult);

    @AfterMapping
    public void mappingCompanyPhotoAsync(@MappingTarget CompanyModel companyModel, Company company,
                                         @Context List<CompletableFuture<Void>> futureResult) {
        UUID uuid = company.getLogoUuid();
        if (uuid != null) {
            futureResult.add(documentModelService.processAbstractPhoto(uuid).thenAccept(companyModel::setLogo));
        }
    }
}
