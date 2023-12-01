package com.service.inspection.utils;

import java.util.Collection;
import java.util.Optional;

import com.service.inspection.entities.Company;
import com.service.inspection.entities.Identifiable;
import com.service.inspection.repositories.CompanyRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ServiceUtils {

    private final CompanyRepository companyRepository;

    // TODO реализовать поиск репозитория по классу сущности
    public <T extends Identifiable> T tryToFindByID(JpaRepository<T, Long> repository, Long id) {
        Optional<T> t = repository.findById(id);
        if (t.isEmpty()) {
            throw new EntityNotFoundException(String.format("No such entity with id %s", id));
        }
        return t.get();
    }

    public <T extends Identifiable> T tryToFindByID(Collection<T> collection, Long id) {
        if (collection == null) {
            return null;
        }
        return collection.stream().filter(x -> x.getId().equals(id)).findFirst()
                .orElseThrow(() -> new EntityNotFoundException(String.format("No such entity with id %s", id)));
    }

    public Company getCompanyIfExistForUser(Long companyId, Long userId) {
        return companyRepository.findByUserIdAndId(userId, companyId).orElseThrow(() ->
                new EntityNotFoundException(String.format("No such company with id %s for this user", companyId)));
    }
}
