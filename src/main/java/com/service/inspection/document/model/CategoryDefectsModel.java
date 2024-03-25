package com.service.inspection.document.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class CategoryDefectsModel {
    private List<Long> photoNums;
    private String recommendation;
}
