package com.service.inspection.document.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class CategoryDefectsModel {
    private Set<Long> photoNums;
    private String recommendation;
}
