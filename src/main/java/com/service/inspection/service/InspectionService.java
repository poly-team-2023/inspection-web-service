package com.service.inspection.service;

import com.service.inspection.entities.Inspection;
import com.service.inspection.entities.User;
import com.service.inspection.mapper.InspectionMapper;
import com.service.inspection.repositories.InspectionRepository;
import com.service.inspection.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class InspectionService {

    private final InspectionRepository inspectionRepository;
    private final UserRepository userRepository;
    @Transactional
    public Long createInspection(String username) {
        User user = userRepository.findByEmail(username).orElseThrow(RuntimeException::new);

        Inspection inspection = new Inspection();
        inspection.setUsers(new HashSet<>(Set.of(user)));
        inspection.setName("Без названия"); // TODO нормально реализовать через проперти
        user.addInspection(inspection);

        inspectionRepository.save(inspection);
        return inspection.getId();
    }

    public Page<Inspection> getUserInspection(String email, Integer pageSize, Integer pageNum) {
        return inspectionRepository.findByUsersEmail(email, PageRequest.of(pageNum, pageSize));
    }

    public void deleteInspection(Integer inspectionId) {
        inspectionRepository.deleteById(inspectionId.longValue());
        // TODO проверка на наличие именно этой инспекци у пользователя + обработка ошибок
    }
}
