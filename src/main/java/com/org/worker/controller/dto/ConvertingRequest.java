package com.org.worker.controller.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class ConvertingRequest {
    @NotBlank
    private String path;

    @NotBlank
    private String pdfTemplate;

}
