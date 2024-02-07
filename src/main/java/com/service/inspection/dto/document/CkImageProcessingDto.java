package com.service.inspection.dto.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class CkImageProcessingDto {

    @JsonProperty("nn_mode")
    @ToString.Include
    private String nnMode;

    @JsonProperty("img")
    @ToString.Exclude
    private String imgBASE64;
}
