package com.service.inspection.service;

import com.service.inspection.repositories.InspectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataService {

    private final InspectionRepository inspectionRepository;



}
