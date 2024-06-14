package com.service.inspection.controller;

import static com.service.inspection.controller.InspectionControllerTest.asJsonString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.service.inspection.configs.security.jwt.AuthTokenFilter;
import com.service.inspection.dto.IdentifiableDto;
import com.service.inspection.dto.equipment.EquipmentDto;
import com.service.inspection.dto.equipment.GetEquipmentDto;
import com.service.inspection.entities.Equipment;
import com.service.inspection.entities.Identifiable;
import com.service.inspection.entities.User;
import com.service.inspection.mapper.CommonMapper;
import com.service.inspection.mapper.EquipmentMapper;
import com.service.inspection.service.EquipmentService;
import com.service.inspection.service.StorageService;
import com.service.inspection.utils.ControllerUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@WebMvcTest(controllers = EquipmentController.class,
        excludeFilters = @ComponentScan.Filter(classes = AuthTokenFilter.class, type = FilterType.ASSIGNABLE_TYPE))
@WithMockUser
class EquipmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EquipmentService equipmentService;
    @MockBean
    private EquipmentMapper equipmentMapper;
    @MockBean
    private ControllerUtils controllerUtils;
    @MockBean
    private CommonMapper commonMapper;

    @BeforeEach
    public void setUp() {
        when(controllerUtils.getUserId(any())).thenReturn(1L);

        User user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        when(controllerUtils.getUser(any())).thenReturn(user);
    }

    @Test
    void testGetEquipment() throws Exception {
        List<GetEquipmentDto> equipmentDtoList = List.of(new GetEquipmentDto());

        when(equipmentService.getEquipment(anyLong())).thenReturn(List.of(new Equipment()));
        when(equipmentMapper.mapToDto(any(Equipment.class))).thenReturn(new GetEquipmentDto());

        mockMvc.perform(get("/api/v1/equipment")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    void testAddEquipment() throws Exception {
        EquipmentDto equipmentDto = new EquipmentDto();
        equipmentDto.setSerialNumber("testSerialNumber");
        equipmentDto.setVerificationDate(LocalDate.now());

        IdentifiableDto identifiableDto = new IdentifiableDto(1L);

        when(equipmentMapper.mapToEquipment(any(EquipmentDto.class))).thenReturn(new Equipment());
        when(equipmentService.addEquipment(any(User.class), any(Equipment.class))).thenReturn(new Equipment());
        when(commonMapper.mapToIdentifiableDto(any(Equipment.class))).thenReturn(identifiableDto);

        mockMvc.perform(post("/api/v1/equipment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(equipmentDto))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(identifiableDto.getId()));
    }

    @Test
    void testUpdateEquipment() throws Exception {
        long id = 1L;
        EquipmentDto equipmentDto = new EquipmentDto();
        equipmentDto.setSerialNumber("testSerialNumber");
        equipmentDto.setVerificationDate(LocalDate.now());

        doNothing().when(equipmentService).updateEquipment(anyLong(), eq(id), any(EquipmentDto.class));

        mockMvc.perform(put("/api/v1/equipment/{equip_id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(equipmentDto))
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteEquipment() throws Exception {
        long id = 1L;

        doNothing().when(equipmentService).deleteEquipment(anyLong(), eq(id));

        mockMvc.perform(delete("/api/v1/equipment/{equip_id}", id)
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void testAddFile() throws Exception {
        long id = 1L;
        int scanNumber = 1;
        MockMultipartFile file = new MockMultipartFile(
                "file", "test_scan.pdf", MediaType.APPLICATION_PDF_VALUE, "scan content".getBytes());
        IdentifiableDto identifiableDto = new IdentifiableDto(1L);

        when(equipmentService.addScan(anyLong(), eq(id), eq(scanNumber), any(MultipartFile.class))).thenReturn(new Identifiable());
        when(commonMapper.mapToIdentifiableDto(any(Identifiable.class))).thenReturn(identifiableDto);

        mockMvc.perform(multipart("/api/v1/equipment/{equip_id}/file", id)
                        .file(file)
                        .param("scanNumber", String.valueOf(scanNumber))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(identifiableDto.getId()));
    }

    @Test
    void testUpdateFile() throws Exception {
        long equipId = 1L;
        long fileId = 2L;
        int scanNumber = 2;

        doNothing().when(equipmentService).updateScan(anyLong(), eq(equipId), eq(fileId), eq(scanNumber));

        mockMvc.perform(put("/api/v1/equipment/{equip_id}/file/{file_id}", equipId, fileId)
                        .param("scanNumber", String.valueOf(scanNumber))
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteFile() throws Exception {
        long equipId = 1L;
        long fileId = 2L;

        doNothing().when(equipmentService).deleteScan(anyLong(), eq(equipId), eq(fileId));

        mockMvc.perform(delete("/api/v1/equipment/{equip_id}/file/{file_id}", equipId, fileId)
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteAllFiles() throws Exception {
        long equipId = 1L;

        doNothing().when(equipmentService).deleteAllScan(anyLong(), eq(equipId));

        mockMvc.perform(delete("/api/v1/equipment/{equip_id}/file", equipId)
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void testGetFile() throws Exception {
        long equipId = 1L;
        long fileId = 2L;
        StorageService.BytesWithContentType file = new StorageService.BytesWithContentType(
                "file content".getBytes(), MediaType.APPLICATION_PDF_VALUE);

        when(equipmentService.getScan(eq(equipId), anyLong(), eq(fileId))).thenReturn(file);
        when(controllerUtils.getResponseEntityFromFile(anyString(), any(StorageService.BytesWithContentType.class)))
                .thenCallRealMethod();

        mockMvc.perform(get("/api/v1/equipment/{equip_id}/file/{file_id}", equipId, fileId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF_VALUE));
    }
}
