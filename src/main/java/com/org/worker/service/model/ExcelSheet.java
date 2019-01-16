package com.org.worker.service.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ExcelSheet {
    private List<String> rows;
    private String name;
    private ExcelSheetStyle excelSheetStyle;
}
