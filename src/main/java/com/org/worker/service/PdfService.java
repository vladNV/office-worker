package com.org.worker.service;

import com.org.worker.service.model.ExcelSheet;
import com.org.worker.service.model.PdfTemplate;

import java.util.List;

public interface PdfService {
    /**
     * Returns path to downloading of pdf
     * @param sheetList excel values
     * @return path
     */
    String convertToPdf(List<ExcelSheet> sheetList);

    PdfTemplate keyOfImplementation();

}
