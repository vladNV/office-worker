package com.org.worker.service;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import com.org.worker.exception.ConvertingException;
import com.org.worker.service.model.Argument;
import com.org.worker.service.model.ExcelSheet;
import com.org.worker.service.model.ExcelSheetStyle;
import com.org.worker.service.model.PdfType;
import com.org.worker.util.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class PdfSingleListWriter extends AbstractPdfWriter  {
    @Override
    public String convertToPdf(List<ExcelSheet> sheetList) {
        // resolve here initialization
        // simple initialization
        String filename = FileUtils.generateName(FileUtils.PDF_VALUE);
        Document document = getA4Document();
        PdfWriter pdfWriter = getWriter(document, generatePath(filename));

        // set parameters here
        Argument single = Argument.builder()
                .text(getRowsFromExcelSheet(ExcelSheetStyle.SINGLE_COLUMN, sheetList))
                .build();

        document.open();
        try {
            depict(single, document);
        } catch (DocumentException e) {
            throw new ConvertingException(e);
        }
        document.close();
        return filename;
    }

    @Override
    public void depict(Argument argument, Document document) throws DocumentException {
        // write pdf structure here
        List<String> text = argument.getText();
    }

    @Override
    public PdfType keyOfImplementation() {
        return PdfType.PDF_SINGLE_SHEET;
    }
}
