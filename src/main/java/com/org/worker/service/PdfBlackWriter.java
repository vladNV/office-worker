package com.org.worker.service;

import com.org.worker.service.model.ExcelSheet;
import com.org.worker.service.model.PdfTemplate;

import java.util.List;

public class PdfBlackWriter implements PdfService {
    @Override
    public String convertToPdf(List<ExcelSheet> sheetList) {
        return null;
    }

    @Override
    public PdfTemplate keyOfImplementation() {
        return PdfTemplate.PDF_BLACK;
    }
}
