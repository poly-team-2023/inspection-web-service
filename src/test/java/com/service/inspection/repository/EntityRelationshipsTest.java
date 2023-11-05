package com.service.inspection.repository;

import com.service.inspection.entities.*;
import com.service.inspection.entities.enums.BuildingType;
import com.service.inspection.entities.enums.Condition;
import com.service.inspection.entities.enums.ProgressingStatus;
import com.service.inspection.repositories.*;
import com.service.inspection.service.AbstractTestContainerStartUp;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
public class EntityRelationshipsTest extends AbstractTestContainerStartUp {

    @Autowired
    AudioRepository audioRepository;

    @Autowired
    BuildingRepository buildingRepository;

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
    void testBuildingRepository() {
        Building buildingToDelete = new Building();
        buildingToDelete.setName("1");
        buildingToDelete.setBuildingType(BuildingType.CULTURE);

        Building buildingNotToDelete = new Building();
        buildingNotToDelete.setName("2");
        buildingNotToDelete.setBuildingType(BuildingType.CULTURE);

        buildingRepository.save(buildingToDelete);
        buildingRepository.save(buildingNotToDelete);

        buildingRepository.deleteById(buildingToDelete.getId());

        assertThat(buildingRepository.findAll())
                .usingRecursiveFieldByFieldElementComparator()
                .containsOnly(buildingNotToDelete);
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
        Company company = new Company();
        company.setName("test");
        company.setLegalAddress("test");
        companyRepository.save(company);

        Equipment equipmentToDelete = new Equipment();
        equipmentToDelete.setSerialNumber("1");
        equipmentToDelete.setVerificationDate(OffsetDateTime.now());
        equipmentToDelete.setVerificationScanUrl("1");
        equipmentToDelete.setCompany(company);

        Equipment equipmentNotToDelete = new Equipment();
        equipmentNotToDelete.setSerialNumber("2");
        equipmentNotToDelete.setVerificationDate(OffsetDateTime.now());
        equipmentNotToDelete.setVerificationScanUrl("2");
        equipmentNotToDelete.setCompany(company);

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
        photoToDelete.setUuid(UUID.randomUUID());
        photoToDelete.setLocation("1");
        photoToDelete.setUrl("1");
        photoToDelete.setCategory(category);

        Photo photoNotToDelete = new Photo();
        photoNotToDelete.setUuid(UUID.randomUUID());
        photoNotToDelete.setLocation("2");
        photoNotToDelete.setUrl("2");
        photoNotToDelete.setCategory(category);

        photoRepository.save(photoToDelete);
        photoRepository.save(photoNotToDelete);

        photoRepository.deleteById(photoToDelete.getUuid());

        assertThat(photoRepository.findAll())
                .usingRecursiveFieldByFieldElementComparator()
                .containsOnly(photoNotToDelete);
    }

    @Test
    void testPlanRepository() {
        Plan planToDelete = new Plan();
        planToDelete.setUrl("1");

        Plan planNotToDelete = new Plan();
        planNotToDelete.setUrl("2");

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
        userToDelete.setUsername("1");
        userToDelete.setEmail("1@1.ru");
        userToDelete.setPassword("1");

        User userNotToDelete = new User();
        userNotToDelete.setUsername("2");
        userNotToDelete.setEmail("2@2.ru");
        userNotToDelete.setPassword("2");

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
        roleToDelete.setName("1");

        Role roleNotToDelete = new Role();
        roleNotToDelete.setName("2");

        roleRepository.save(roleToDelete);
        roleRepository.save(roleNotToDelete);

        roleRepository.deleteById(roleToDelete.getId());

        assertThat(roleRepository.findAll())
                .usingRecursiveFieldByFieldElementComparator()
                .containsOnly(roleNotToDelete);
    }
}
