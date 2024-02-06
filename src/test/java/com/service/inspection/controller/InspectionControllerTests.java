package com.service.inspection.controller;

import com.service.inspection.InspectionApplication;
import com.service.inspection.dto.inspection.InspectionDto;
import com.service.inspection.entities.Category;
import com.service.inspection.entities.Identifiable;
import com.service.inspection.entities.Inspection;
import com.service.inspection.entities.User;
import com.service.inspection.repositories.UserRepository;
import com.service.inspection.service.InspectionService;
import com.service.inspection.service.StorageService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = InspectionApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations="classpath:application-test.properties")
public class InspectionControllerTests {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private InspectionService inspectionService;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @AfterEach
    void tearDown() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate,
                "users", "user_roles", "inspection", "category", "photo");
    }

    @Test
    void inspectionBaseTest() {
        User user = userRepo.save(getUser(1));
        Identifiable inspection1 = inspectionService.createInspection(user.getId());
        Identifiable inspection2 = inspectionService.createInspection(user.getId());

        List<Inspection> inspectionsList = inspectionService
                .getUserInspections(user.getId(), 10, 0)
                .get()
                .toList();

        assertEquals(inspectionsList.size(), 2);
        assertEquals(inspectionsList.get(0).getId(), inspection1.getId());
        assertEquals(inspectionsList.get(1).getId(), inspection2.getId());

        inspectionService.deleteInspection(inspection2.getId(), user.getId());
        try {
            inspectionService.getUserInspection(user.getId(), inspection2.getId());
        } catch (EntityNotFoundException e) {
            // it's ok.
        }
        assertEquals(inspectionService.getUserInspection(user.getId(), inspection1.getId()).getId(), inspection1.getId());

        inspectionService.deleteInspection(inspection1.getId(), user.getId());
        assertTrue(inspectionService.getUserInspections(user.getId(), 10, 0).get().toList().isEmpty());
    }

    @Test
    void updateInspectionTest() {
        User user = userRepo.save(getUser(1));
        Identifiable inspection = inspectionService.createInspection(user.getId());

        String name = "name";
        String address = "name";
        String script = "script";

        InspectionDto inspectionDto = new InspectionDto();
        inspectionDto.setName(name);
        inspectionDto.setAddress(address);
        inspectionDto.setScript(script);

        inspectionService.updateInspection(inspection.getId(), user.getId(), inspectionDto);

        Inspection receivedInspection = inspectionService.getUserInspection(user.getId(), inspection.getId());
        assertEquals(receivedInspection.getName(), name);
        assertEquals(receivedInspection.getAddress(), address);
        assertEquals(receivedInspection.getScript(), script);
    }

    @Test
    void photoUploadTest() {
        User user = userRepo.save(getUser(1));
        Identifiable inspection = inspectionService.createInspection(user.getId());

        String content = "content";
        MockMultipartFile file = getFile(content);
        inspectionService.uploadMainInspectionPhoto(user.getId(), inspection.getId(), file);

        StorageService.BytesWithContentType photo = inspectionService.getMainInspectionPhoto(inspection.getId(), user.getId());
        assertEquals(new String(photo.getBytes()), content);
    }

    @Test
    @Transactional
    void categoryBaseTest() {
        User user = userRepo.save(getUser(1));
        Identifiable inspection = inspectionService.createInspection(user.getId());

        List<String> categories = List.of(
                "category_name_1",
                "category_name_2",
                "category_name_3"
        );

        categories.forEach(it -> inspectionService.createNewCategory(user.getId(), inspection.getId(), it));

        List<Long> categoriesIds = inspectionService
                .getAllCategories(inspection.getId(), user.getId())
                .stream().map(Category::getId).toList();

        assertEquals(categories.size(), categoriesIds.size());

        for (int i = categories.size() - 1; i >= 0; i--) {
            inspectionService.deleteCategory(user.getId(), inspection.getId(), categoriesIds.get(i));

            List<String> receivedCategories = inspectionService
                    .getAllCategories(inspection.getId(), user.getId())
                    .stream().map(Category::getName).toList();

            assertEquals(categories.subList(0, i), receivedCategories);
        }
    }

    @Test
    @Transactional
    void categoryUpdateTest() {
        User user = userRepo.save(getUser(1));
        Identifiable inspection = inspectionService.createInspection(user.getId());
        Identifiable category = inspectionService.createNewCategory(
                user.getId(),
                inspection.getId(),
                "category_name"
        );

        inspectionService.updateCategory(
                user.getId(),
                category.getId(),
                inspection.getId(),
                "new_category_name"
        );

        List<Category> allCategories = inspectionService.getAllCategories(inspection.getId(), user.getId()).stream().toList();

        assertEquals(allCategories.size(), 1);
        assertEquals(allCategories.get(0).getName(), "new_category_name");
    }

    @Test
    @Transactional
    void categoryWithPhotosTest() throws IOException {
        User user = userRepo.save(getUser(1));
        Identifiable inspection = inspectionService.createInspection(user.getId());
        Identifiable category = inspectionService.createNewCategory(
                user.getId(),
                inspection.getId(),
                "category_name"
        );

        List<MultipartFile> photos = List.of(
                getFile("photo_1"),
                getFile("photo_2"),
                getFile("photo_3")
        );

        List<Identifiable> identifiablePhotos = new ArrayList<>();
        photos.forEach(it -> {
                    Identifiable photo = inspectionService.addPhotoToCategory(user.getId(), inspection.getId(), category.getId(), it);
                    identifiablePhotos.add(photo);
                }
        );

        for (int i = photos.size() - 1; i >= 0; i--) {
            inspectionService.deletePhoto(
                    user.getId(),
                    inspection.getId(),
                    category.getId(),
                    identifiablePhotos.get(i).getId()
            );

            for (int j = 0; j < photos.size(); j++) {
                try {
                    byte[] receivedPhoto = inspectionService.getCategoryPhoto(
                            user.getId(),
                            inspection.getId(),
                            category.getId(),
                            identifiablePhotos.get(j).getId()
                    ).getBytes();

                    assertEquals(
                            new String(photos.get(j).getBytes()),
                            new String(receivedPhoto)
                    );
                } catch (EntityNotFoundException e) {
                    if (j < i) { // Ненайденная фотография находится в диапазоне существующих.
                        throw new EntityNotFoundException(e.getMessage());
                    }
                }
            }
        }
    }

    private User getUser(int prefix) {
        User user = new User();

        user.setFirstName("test" + prefix);
        user.setSecondName("test" + prefix);
        user.setEmail("test" + prefix + "@example.com");
        user.setPassword(passwordEncoder.encode("password"));

        return user;
    }

    private MockMultipartFile getFile(String content) {
        return new MockMultipartFile(
                "file",
                "sample.txt",
                "text/plain",
                content.getBytes());
    }
}
