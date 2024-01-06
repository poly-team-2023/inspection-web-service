package com.service.inspection.controller;

import com.service.inspection.InspectionApplication;
import com.service.inspection.dto.equipment.EquipmentDto;
import com.service.inspection.entities.Equipment;
import com.service.inspection.entities.Identifiable;
import com.service.inspection.entities.User;
import com.service.inspection.repositories.UserRepository;
import com.service.inspection.service.EquipmentService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.jdbc.JdbcTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest(classes = InspectionApplication.class)
@AutoConfigureMockMvc
public class EquipmentControllerTests {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EquipmentService equipmentService;

    @AfterEach
    void tearDown() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate,
                "users", "user_roles", "equipment");
    }

    @Test
    void equipmentBaseTest() {
        User user1 = userRepo.save(getUser(1));
        User user2 = userRepo.save(getUser(2));

        int equipmentCount = 4;
        List<Equipment> equipmentListForUser1 = new ArrayList<>();
        List<Equipment> equipmentListForUser2 = new ArrayList<>();
        for (int i = 0; i < equipmentCount; i++) {
            Equipment equipment1 = getEquipment(user1, i + "_" + 1);
            Equipment equipment2 = getEquipment(user1, i + "_" + 2);

            Identifiable equipId1 = equipmentService.addEquipment(user1, equipment1);
            Identifiable equipId2 = equipmentService.addEquipment(user2, equipment2);

            equipment1.setId(equipId1.getId());
            equipment2.setId(equipId2.getId());

            equipmentListForUser1.add(equipment1);
            equipmentListForUser2.add(equipment2);
        }

        assertEquals(equipmentListForUser1, equipmentService.getEquipment(user1.getId()));
        assertEquals(equipmentListForUser2, equipmentService.getEquipment(user2.getId()));

        for (int i = 0; i < equipmentCount; i++) {
            int j = equipmentCount - i - 1;

            Equipment currEquip1 = equipmentListForUser1.get(0);
            Equipment currEquip2 = equipmentListForUser2.get(j);

            equipmentService.deleteEquipment(user1.getId(), currEquip1.getId());
            equipmentService.deleteEquipment(user2.getId(), currEquip2.getId());

            equipmentListForUser1.remove(0);
            equipmentListForUser2.remove(j);

            assertEquals(equipmentListForUser1, equipmentService.getEquipment(user1.getId()));
            assertEquals(equipmentListForUser2, equipmentService.getEquipment(user2.getId()));
        }
    }

    @Test
    void updateEquipmentTest() {
        User user = userRepo.save(getUser(1));
        Equipment equipment1 = getEquipment(user, "123");
        Identifiable equipmentWithId = equipmentService.addEquipment(user, equipment1);

        List<Equipment> equipmentList = equipmentService.getEquipment(user.getId());
        assertEquals(equipmentList.size(), 1);

        String equipmentName = "test_new";
        String serialNumber = "321";
        LocalDate date = LocalDate.now();

        EquipmentDto equipmentDto = new EquipmentDto();
        equipmentDto.setName(equipmentName);
        equipmentDto.setSerialNumber(serialNumber);
        equipmentDto.setVerificationDate(date);

        equipmentService.updateEquipment(user.getId(), equipmentWithId.getId(), equipmentDto);
        equipmentList = equipmentService.getEquipment(user.getId());
        assertEquals(equipmentList.size(), 1);

        Equipment equipment = equipmentList.get(0);
        assertEquals(equipment.getName(), equipmentName);
        assertEquals(equipment.getSerialNumber(), serialNumber);
        assertEquals(equipment.getVerificationDate(), date);
    }

    private User getUser(int prefix) {
        User user = new User();

        user.setFirstName("test" + prefix);
        user.setSecondName("test" + prefix);
        user.setEmail("test" + prefix + "@example.com");
        user.setPassword(passwordEncoder.encode("password"));

        return user;
    }

    private Equipment getEquipment(User user, String serialNumber) {
        Equipment equipment = new Equipment();

        equipment.setUser(user);
        equipment.setSerialNumber(serialNumber);
        equipment.setVerificationDate(LocalDate.now());

        return equipment;
    }
}
