package com.service.inspection.repository;

import com.service.inspection.entities.*;
import com.service.inspection.entities.enums.BuildingType;
import com.service.inspection.entities.enums.Condition;
import com.service.inspection.entities.enums.ERole;
import com.service.inspection.entities.enums.ProgressingStatus;
import com.service.inspection.repositories.*;
import com.service.inspection.service.AbstractTestContainerStartUp;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
class EntityRelationshipsTest extends AbstractTestContainerStartUp {

    @Autowired
    AudioRepository audioRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    EmployerRepository employerRepository;

    @Autowired
    EquipmentRepository equipmentRepository;

    @Autowired
    InspectionRepository inspectionRepository;

    @Autowired
    PhotoRepository photoRepository;

    @Autowired
    PlanRepository planRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    void testAudioRepository() {
        Audio audioToDelete = new Audio();
        audioToDelete.setText("1");
        audioToDelete.setStatus(ProgressingStatus.READY);

        Audio audioNotToDelete = new Audio();
        audioNotToDelete.setText("2");
        audioNotToDelete.setStatus(ProgressingStatus.READY);

        audioRepository.save(audioToDelete);
        audioRepository.save(audioNotToDelete);

        audioRepository.deleteById(audioToDelete.getId());

        assertThat(audioRepository.findAll())
                .usingRecursiveFieldByFieldElementComparator()
                .containsOnly(audioNotToDelete);
    }


    @Test
    void testCategoryRepository() {
        Category categoryToDelete = new Category();
        categoryToDelete.setName("1");
        categoryToDelete.setCondition(Condition.OPERABLE);

        Category categoryNotToDelete = new Category();
        categoryNotToDelete.setName("2");
        categoryNotToDelete.setCondition(Condition.OPERABLE);

        categoryRepository.save(categoryToDelete);
        categoryRepository.save(categoryNotToDelete);

        categoryRepository.deleteById(categoryToDelete.getId());

        assertThat(categoryRepository.findAll())
                .usingRecursiveFieldByFieldElementComparator()
                .containsOnly(categoryNotToDelete);
    }

    @Test
    void testCompanyRepository() {
        Company companyToDelete = new Company();
        companyToDelete.setName("1");
        companyToDelete.setLegalAddress("1");

        Company companyNotToDelete = new Company();
        companyNotToDelete.setName("2");
        companyNotToDelete.setLegalAddress("2");

        companyRepository.save(companyToDelete);
        companyRepository.save(companyNotToDelete);

        companyRepository.deleteById(companyToDelete.getId());

        assertThat(companyRepository.findAll())
                .usingRecursiveFieldByFieldElementComparator()
                .containsOnly(companyNotToDelete);
    }

    @Test
    void testEmployerRepository() {
        Employer employerToDelete = new Employer();
        employerToDelete.setName("1");
        employerToDelete.setSignatureUrl("1");

        Employer employerNotToDelete = new Employer();
        employerNotToDelete.setName("2");
        employerNotToDelete.setSignatureUrl("2");

        employerRepository.save(employerToDelete);
        employerRepository.save(employerNotToDelete);

        employerRepository.deleteById(employerToDelete.getId());

        assertThat(employerRepository.findAll())
                .usingRecursiveFieldByFieldElementComparator()
                .containsOnly(employerNotToDelete);
    }

    @Test
    void testEquipmentRepository() {
        User user = new User();
        user.setEmail("1@1.ru");
        user.setPassword("1");
        user.setFirstName("qwe");
        user.setSecondName("qwe");
        userRepository.save(user);

        Equipment equipmentToDelete = new Equipment();
        equipmentToDelete.setSerialNumber("1");
        equipmentToDelete.setVerificationDate(OffsetDateTime.now());
        equipmentToDelete.setVerificationScanUrl("1");
        equipmentToDelete.setUser(user);

        Equipment equipmentNotToDelete = new Equipment();
        equipmentNotToDelete.setSerialNumber("2");
        equipmentNotToDelete.setVerificationDate(OffsetDateTime.now());
        equipmentNotToDelete.setVerificationScanUrl("2");
        equipmentNotToDelete.setUser(user);

        equipmentRepository.save(equipmentToDelete);
        equipmentRepository.save(equipmentNotToDelete);

        equipmentRepository.deleteById(equipmentToDelete.getId());

        assertThat(equipmentRepository.findAll())
                .usingRecursiveFieldByFieldElementComparator()
                .containsOnly(equipmentNotToDelete);
    }

    @Test
    void testInspectionRepository() {
        Inspection inspectionToDelete = new Inspection();
        inspectionToDelete.setName("1");
        inspectionToDelete.setStatus(ProgressingStatus.READY);

        Inspection inspectionNotToDelete = new Inspection();
        inspectionNotToDelete.setName("2");
        inspectionNotToDelete.setStatus(ProgressingStatus.READY);

        inspectionRepository.save(inspectionToDelete);
        inspectionRepository.save(inspectionNotToDelete);

        inspectionRepository.deleteById(inspectionToDelete.getId());

        assertThat(inspectionRepository.findAll())
                .usingRecursiveFieldByFieldElementComparator()
                .containsOnly(inspectionNotToDelete);
    }

    @Test
    void testPhotoRepository() {
        Category category = new Category();
        category.setName("test");

        categoryRepository.save(category);

        Photo photoToDelete = new Photo();
        photoToDelete.setId(1L);
        photoToDelete.setLocation("1");
        photoToDelete.setFileUuid(UUID.randomUUID());
        photoToDelete.setCategory(category);

        Photo photoNotToDelete = new Photo();
        photoNotToDelete.setId(2L);
        photoNotToDelete.setLocation("2");
        photoNotToDelete.setFileUuid(UUID.randomUUID());
        photoNotToDelete.setCategory(category);

        photoRepository.save(photoToDelete);
        photoRepository.save(photoNotToDelete);

        photoRepository.deleteById(photoToDelete.getId());

        assertThat(photoRepository.findAll())
                .usingRecursiveFieldByFieldElementComparator()
                .containsOnly(photoNotToDelete);
    }

    @Test
    void testPlanRepository() {
        Plan planToDelete = new Plan();
        planToDelete.setFileUuid(UUID.randomUUID());

        Plan planNotToDelete = new Plan();
        planNotToDelete.setFileUuid(UUID.randomUUID());

        planRepository.save(planToDelete);
        planRepository.save(planNotToDelete);

        planRepository.deleteById(planToDelete.getId());

        assertThat(planRepository.findAll())
                .usingRecursiveFieldByFieldElementComparator()
                .containsOnly(planNotToDelete);
    }

    @Test
    void testUserRepository() {
        User userToDelete = new User();
        userToDelete.setEmail("1@1.ru");
        userToDelete.setPassword("1");
        userToDelete.setFirstName("qwe");
        userToDelete.setSecondName("qwe");

        User userNotToDelete = new User();
        userNotToDelete.setEmail("2@2.ru");
        userNotToDelete.setPassword("2");
        userNotToDelete.setFirstName("qwe");
        userNotToDelete.setSecondName("qwe");

        userRepository.save(userToDelete);
        userRepository.save(userNotToDelete);

        userRepository.deleteById(userToDelete.getId());

        assertThat(userRepository.findAll())
                .usingRecursiveFieldByFieldElementComparator()
                .containsOnly(userNotToDelete);
    }

    @Test
    void testRoleRepository() {
        Role roleToDelete = new Role();
        roleToDelete.setName(ERole.ROLE_VIEWER);

        roleRepository.save(roleToDelete);

        roleRepository.deleteById(roleToDelete.getId());

        assertThat(roleRepository.findAll())
                .usingRecursiveFieldByFieldElementComparator()
                .doesNotContain(roleToDelete);
    }
}
