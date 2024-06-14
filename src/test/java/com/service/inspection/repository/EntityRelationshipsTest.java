package com.service.inspection.repository;

import com.service.inspection.entities.Audio;
import com.service.inspection.entities.Category;
import com.service.inspection.entities.Company;
import com.service.inspection.entities.Employer;
import com.service.inspection.entities.Equipment;
import com.service.inspection.entities.Inspection;
import com.service.inspection.entities.Photo;
import com.service.inspection.entities.Plan;
import com.service.inspection.entities.Role;
import com.service.inspection.entities.User;
import com.service.inspection.entities.enums.Condition;
import com.service.inspection.entities.enums.ERole;
import com.service.inspection.entities.enums.ProgressingStatus;
import com.service.inspection.repositories.AudioRepository;
import com.service.inspection.repositories.CategoryRepository;
import com.service.inspection.repositories.CompanyRepository;
import com.service.inspection.repositories.EmployerRepository;
import com.service.inspection.repositories.EquipmentRepository;
import com.service.inspection.repositories.InspectionRepository;
import com.service.inspection.repositories.PhotoRepository;
import com.service.inspection.repositories.PlanRepository;
import com.service.inspection.repositories.RoleRepository;
import com.service.inspection.repositories.UserRepository;
import com.service.inspection.service.AbstractTestContainerStartUp;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.*;

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
        Inspection inspection = new Inspection();
        inspection.setName("1");
        inspection.setStatus(ProgressingStatus.READY);

        Category categoryToDelete = new Category();
        categoryToDelete.setName("1");
        categoryToDelete.setCondition(Condition.OPERABLE);
        categoryToDelete.setInspection(inspection);

        Category categoryNotToDelete = new Category();
        categoryNotToDelete.setName("2");
        categoryNotToDelete.setCondition(Condition.OPERABLE);
        categoryNotToDelete.setInspection(inspection);
        inspection.setCategories(new ArrayList<>(List.of(categoryToDelete, categoryNotToDelete)));

        inspectionRepository.save(inspection);

        assertThat(categoryRepository.findAll())
                .usingRecursiveFieldByFieldElementComparator()
                .contains(categoryNotToDelete, categoryToDelete);

        assertThat(inspectionRepository.findAll())
                .usingRecursiveFieldByFieldElementComparator()
                .contains(inspection);

        inspection.getCategories().remove(categoryToDelete);
        categoryRepository.deleteById(categoryToDelete.getId());

        assertThat(categoryRepository.findAll())
                .usingRecursiveFieldByFieldElementComparator()
                .containsOnly(categoryNotToDelete);

        assertThat(inspectionRepository.findAll())
                .usingRecursiveFieldByFieldElementComparator()
                .containsOnly(inspection);

        inspectionRepository.delete(inspection);

        assertThat(inspectionRepository.findAll()).isEmpty();
        assertThat(categoryRepository.findAll()).isEmpty();
    }

    @Test
    void testCompanyRepository() {
        User user = new User();
        user.setEmail("1@1.ru");
        user.setPassword("1");
        user.setFirstName("qwe");
        user.setSecondName("qwe");
        userRepository.save(user);

        Company companyToDelete = new Company();
        companyToDelete.setName("1");
        companyToDelete.setLegalAddress("1");
        companyToDelete.setUser(user);

        Company companyNotToDelete = new Company();
        companyNotToDelete.setName("2");
        companyNotToDelete.setLegalAddress("2");
        companyNotToDelete.setUser(user);

        companyRepository.save(companyToDelete);
        companyRepository.save(companyNotToDelete);

        companyRepository.deleteById(companyToDelete.getId());

        assertThat(companyRepository.findAll())
                .usingRecursiveFieldByFieldElementComparator()
                .containsOnly(companyNotToDelete);

        assertThat(userRepository.findAll())
                .usingRecursiveFieldByFieldElementComparator()
                .containsOnly(user);

        userRepository.deleteById(user.getId());

        assertThat(userRepository.findAll()).isEmpty();
        assertThat(companyRepository.findAll()).isEmpty();
    }

    @Test
    void testEmployerRepository() {
        User user = new User();
        user.setEmail("1@1.ru");
        user.setPassword("1");
        user.setFirstName("qwe");
        user.setSecondName("qwe");

        Company company = new Company();
        company.setName("1");
        company.setLegalAddress("1");
        company.setUser(user);
        user.setCompanies(new HashSet<>(Set.of(company)));

        Employer employerToDelete = new Employer();
        employerToDelete.setName("1");
        employerToDelete.setSignatureUuid(UUID.randomUUID());
        employerToDelete.setCompany(company);

        Employer employerNotToDelete = new Employer();
        employerNotToDelete.setName("2");
        employerNotToDelete.setSignatureUuid(UUID.randomUUID());
        employerNotToDelete.setCompany(company);

        ArrayList<Employer> arrayList = new ArrayList<>();
        arrayList.add(employerToDelete);
        arrayList.add(employerNotToDelete);

        company.setEmployers(arrayList);

        userRepository.save(user);

        company.getEmployers().remove(employerToDelete);
        employerRepository.deleteById(employerToDelete.getId());

        assertThat(employerRepository.findAll())
                .usingRecursiveFieldByFieldElementComparator()
                .containsOnly(employerNotToDelete);

        user.getCompanies().remove(company);
        companyRepository.deleteById(company.getId());

        assertThat(userRepository.findAll()).containsOnly(user);
        assertThat(companyRepository.findAll()).isEmpty();
        assertThat(employerRepository.findAll()).isEmpty();
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
        equipmentToDelete.setVerificationDate(LocalDate.now());
        equipmentToDelete.setUser(user);

        Equipment equipmentNotToDelete = new Equipment();
        equipmentNotToDelete.setSerialNumber("2");
        equipmentNotToDelete.setVerificationDate(LocalDate.now());
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
        category.setName("1");
        category.setCondition(Condition.OPERABLE);

        categoryRepository.save(category);

        Photo photoToDelete = new Photo();
        photoToDelete.setLocation("1");
        photoToDelete.setFileUuid(UUID.randomUUID());
        photoToDelete.setCategory(category);

        Photo photoNotToDelete = new Photo();
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

        Inspection inspection = new Inspection();
        inspection.setName("1");
        inspection.setStatus(ProgressingStatus.READY);

        inspectionRepository.save(inspection);

        Plan planToDelete = new Plan();
        planToDelete.setFileUuid(UUID.randomUUID());
        planToDelete.setInspection(inspection);

        Plan planNotToDelete = new Plan();
        planNotToDelete.setFileUuid(UUID.randomUUID());
        planNotToDelete.setInspection(inspection);

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

    // TODO: testLicenseRepository()
}
