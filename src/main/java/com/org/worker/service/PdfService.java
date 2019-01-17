package com.org.worker.service;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.org.worker.service.model.ExcelSheet;
import com.org.worker.service.model.ExcelSheetStyle;
import com.org.worker.service.model.PdfType;

import java.util.List;

public interface PdfService {
    /**
     * Returns path to downloading of pdf
     * @param sheetList excel values
     * @return path
     */
    String convertToPdf(List<ExcelSheet> sheetList);

    PdfType keyOfImplementation();

    default List<String> getRowsFromExcelSheet(ExcelSheetStyle sheetStyle, List<ExcelSheet> sheets) {
        return sheets.stream()
                .filter(s -> s.getExcelSheetStyle() == sheetStyle)
                .findFirst().get().getRows();
    }

    default Document getA4Document() {
        return new Document(PageSize.A4, 36, 36, 90, 36);
    }
}
