package com.org.worker.service;

import com.itextpdf.layout.border.Border;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import com.org.worker.config.PdfProperties;
import com.org.worker.exception.ConvertingException;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.IntStream;

@Component
public class DefaultPdfPageEventHelper extends PdfPageEventHelper {

    @Autowired
    private PdfProperties pdfProperties;

    private PdfTemplate pdfTemplate;

    private Image image;

    @Setter
    private String[] text;

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        Font font = pdfProperties.getDefaultFont();
        font.setSize(11);

        PdfPTable pageTable = new PdfPTable(4);
        try {
            pageTable.setWidths(new int[]{10, 8, 4, 4});
            pageTable.setTotalWidth(560);
            pageTable.getDefaultCell().setFixedHeight(30);
            pageTable.getDefaultCell().setBorder(Border.SOLID);
            pageTable.getDefaultCell().setBorderColor(BaseColor.LIGHT_GRAY);
            pageTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);

            nextLine(pageTable, 4);

            pageTable.addCell(new Phrase("Номер сертификата " + text[0], font));
            pageTable.addCell(new Phrase("Дата калибровки " + text[1], font));
            pageTable.addCell(new Phrase(String.format("Страница %d  из  ", writer.getPageNumber()), font));

            PdfPCell totalPageCountRu = new PdfPCell(image);
            totalPageCountRu.setBorder(Border.SOLID);
            totalPageCountRu.setBorderColor(BaseColor.LIGHT_GRAY);
            totalPageCountRu.setHorizontalAlignment(Element.ALIGN_LEFT);
            pageTable.addCell(totalPageCountRu);

            pageTable.addCell(new Phrase("Certificate number " + text[0], font));
            pageTable.addCell(new Phrase("Date when celebrated " + text[1], font));
            pageTable.addCell(new Phrase(String.format("Page  %d     of  ", writer.getPageNumber()), font));

            PdfPCell totalPageCountEng = new PdfPCell(image);
            totalPageCountEng.setBorder(Border.SOLID);
            totalPageCountEng.setBorderColor(BaseColor.LIGHT_GRAY);
            totalPageCountRu.setHorizontalAlignment(Element.ALIGN_LEFT);
            pageTable.addCell(totalPageCountEng);

            nextLine(pageTable, 4);

            PdfContentByte canvas = writer.getDirectContent();
            canvas.beginMarkedContentSequence(PdfName.ARTIFACT);
            pageTable.writeSelectedRows(0, -1, 36, document.getPageSize().getHeight(), canvas);
            canvas.endMarkedContentSequence();
        } catch (DocumentException e) {
            throw new ConvertingException(e);
        }
    }

    private void nextLine(PdfPTable pdfPTable, int count) {
        IntStream.range(0, count).forEach(i -> pdfPTable.addCell("\n"));
    }

    @Override
    public void onOpenDocument(PdfWriter writer, Document document) {
        pdfTemplate = writer.getDirectContent().createTemplate(34, 16);
        try {
            image = Image.getInstance(pdfTemplate);
            image.setRole(PdfName.ARTIFACT);
        } catch (BadElementException e) {
            throw new ConvertingException(e);
        }
    }

    @Override
    public void onCloseDocument(PdfWriter writer, Document document) {
        Font font = pdfProperties.getDefaultFont();
        font.setSize(11);

        int totalLength = String.valueOf(writer.getPageNumber()).length();
        int totalWidth = totalLength * 5;
        ColumnText.showTextAligned(pdfTemplate, Element.ALIGN_RIGHT,
                new Phrase(String.valueOf(writer.getPageNumber()), font),
                totalWidth + 3, 3, 0);
    }

}
