package com.service.inspection.utils;

import com.service.inspection.entities.Identifiable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ServiceUtils {

    private final EntityManager entityManager;


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
}
