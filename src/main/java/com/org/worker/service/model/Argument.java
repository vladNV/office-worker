package com.org.worker.service.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class Argument {
    private List<String> text;
    private String[][] data;
}
