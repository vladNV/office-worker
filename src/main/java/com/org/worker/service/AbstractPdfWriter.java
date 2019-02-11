package com.org.worker.service;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import com.org.worker.config.FileSystemManager;
import com.org.worker.config.PdfProperties;
import com.org.worker.exception.ConvertingException;
import com.org.worker.exception.FileWriterException;
import com.org.worker.service.model.Argument;
import com.org.worker.service.model.ExcelSheet;
import com.org.worker.service.model.ExcelSheetStyle;
import com.org.worker.util.ConverterUtils;
import com.org.worker.util.FileUtils;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

@Getter
abstract class AbstractPdfWriter implements PdfService {

    @Autowired
    private FileSystemManager fileSystemManager;

    @Autowired
    private PdfProperties pdfProperties;

    @Autowired
    private DefaultPdfPageEventHelper pdfPageEventHelper;

    public String generatePath(String filename) {
        return fileSystemManager.getPdfFolder() + File.separator + filename;
    }

    @Override
    public String convertToPdf(List<ExcelSheet> sheetList) {
        String filename = FileUtils.generateName(FileUtils.PDF_VALUE);
        Document document = getA4Document();
        PdfWriter pdfWriter = getWriter(document, generatePath(filename));
        List<String> text = getRowsFromExcelSheet(ExcelSheetStyle.SINGLE_COLUMN, sheetList);

        pdfPageEventHelper.setText(new String[]{text.get(0), text.get(1)});
        pdfWriter.setPageEvent(pdfPageEventHelper);

        document.open();
        try {
            Argument arg = Argument
                    .builder()
                    .text(text)
                    .data(ConverterUtils
                            .toTable(getRowsFromExcelSheet(ExcelSheetStyle.TABLE, sheetList),
                                    FileUtils.SEPARATOR))
                    .build();
            depict(arg, document);
        } catch (DocumentException e) {
            throw new ConvertingException(e);
        }
        document.close();

        return filename;
    }

    PdfWriter getWriter(Document document, String path) {
        try {
            PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(path));
            pdfWriter.setPdfVersion(PdfWriter.PDF_VERSION_1_7);
            return pdfWriter;
        } catch (DocumentException | FileNotFoundException e) {
            throw new FileWriterException("Error occurred while opening document", e);
        }
    }

    public abstract void depict(Argument argument, Document document) throws DocumentException;

}
