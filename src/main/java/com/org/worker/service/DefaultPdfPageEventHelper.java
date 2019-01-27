package com.org.worker.service;

import com.itextpdf.layout.border.Border;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.org.worker.service.PdfBuilderUtils.nextLine;

@Slf4j
@Component
public class DefaultPdfPageEventHelper extends PdfPageEventHelper {

    @Autowired
    private PdfProperties pdfProperties;

    @Value("${img.path.header}")
    private String headerImagePath;

    @Value("${img.path}")
    private String imagePath;

    private PdfTemplate pdfTemplate;

    private Image image;

    @Setter
    private String[] text;

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        try {
            int pageNumber = writer.getPageNumber();
            if (pageNumber == 1) {
                renderTableOnFirstPage(writer, document);
                renderImage(document, headerImagePath);
                return;
            }
            renderHeader(writer, document);
            renderImage(document, imagePath);
        } catch (DocumentException | IOException e) {
            throw new ConvertingException(e);
        }
    }

    private void renderTableOnFirstPage(PdfWriter writer, Document document) throws DocumentException {
        PdfPTable table = new PdfPTable(6);
        table.setWidths(new int[]{2, 7, 7, 4, 1, 2});
        table.setTotalWidth(document.getPageSize().getWidth() - 12);
        table.getDefaultCell().setBorderWidth(0);
        table.getDefaultCell().setFixedHeight(20);

        Font font = pdfProperties.getDefaultFont();
        font.setSize(12);
        table.addCell(StringUtils.EMPTY);
        table.addCell(
                new Phrase(String.format("Номер сертификата %s", text[0]), font)
        );
        table.addCell(
                new Phrase(String.format("Дата калибровки %s", text[1]), font)
        );
        table.addCell(
                new Phrase("Страница  1    из    ", font)
        );
        table.addCell(domainOfTotalPageNumber());
        table.addCell(StringUtils.EMPTY);

        font = pdfProperties.getDefaultFont();
        font.setSize(8);
        table.addCell(StringUtils.EMPTY);
        table.addCell(
                new Phrase("Certificate number", font)
        );
        table.addCell(
                new Phrase("Date when celebrated", font)
        );
        table.addCell(
                new Phrase("Page    of   ", font)
        );
        table.addCell(StringUtils.EMPTY);
        table.addCell(StringUtils.EMPTY);

        PdfContentByte canvas = writer.getDirectContent();
        canvas.beginMarkedContentSequence(PdfName.ARTIFACT);
        table.writeSelectedRows(0, -1, 0, 550, canvas);
        canvas.endMarkedContentSequence();
    }

    private void renderHeader(PdfWriter writer, Document document) {
        PdfPTable pageTable = new PdfPTable(5);
        try {
            pageTable.setTotalWidth(document.getPageSize().getWidth() - 12);
            pageTable.setWidths(new int[]{3, 12, 4, 1, 2});
            pageTable.getDefaultCell().setBorder(Border.SOLID);
            pageTable.getDefaultCell().setBorderColor(BaseColor.BLACK);
            pageTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);

            // first empty row
            nextLine(pageTable, 5);

            String cerfNum = text[0];

            // second row
            pageTable.addCell(StringUtils.EMPTY);

            Font font = pdfProperties.getDefaultFont();
            font.setSize(14);
            font.setStyle(Font.BOLD);
            Phrase cerf = new Phrase("Сертифікат калібрування\n", font);

            font = pdfProperties.getDefaultFont();
            font.setSize(12);
            Phrase cerfEn = new Phrase("Calibration certificate", font);

            Phrase cell = new Phrase();
            cell.add(cerf);
            cell.add(cerfEn);
            pageTable.addCell(cell);

            pageTable.addCell(StringUtils.EMPTY);
            pageTable.addCell(StringUtils.EMPTY);
            pageTable.addCell(StringUtils.EMPTY);

            // third row
            pageTable.addCell(StringUtils.EMPTY);

            font = pdfProperties.getDefaultFont();
            font.setSize(12);
            Phrase num = new Phrase(String.format("Номер сертифікату %s\n", cerfNum), font);

            font = pdfProperties.getDefaultFont();
            font.setSize(8);
            Phrase numEn = new Phrase("Certificate number", font);

            cell = new Phrase();
            cell.add(num);
            cell.add(numEn);
            pageTable.addCell(cell);

            font = pdfProperties.getDefaultFont();
            font.setSize(12);
            Phrase pageOf = new Phrase(String.format("Страница   %d     из \n", writer.getPageNumber()), font);

            font = pdfProperties.getDefaultFont();
            font.setSize(9);
            Phrase pageOfEn = new Phrase("Page    of    ", font);

            cell = new Phrase();
            cell.add(pageOf);
            cell.add(pageOfEn);
            pageTable.addCell(cell);

            pageTable.addCell(domainOfTotalPageNumber());
            pageTable.addCell(StringUtils.EMPTY);

            PdfContentByte canvas = writer.getDirectContent();
            canvas.beginMarkedContentSequence(PdfName.ARTIFACT);
            pageTable.writeSelectedRows(0, -1, 0, document.getPageSize().getHeight(), canvas);

            canvas.endMarkedContentSequence();

        } catch (DocumentException e) {
            throw new ConvertingException(e);
        }
    }

    private PdfPCell domainOfTotalPageNumber() {
        PdfPCell c = new PdfPCell(image);
        c.setBorder(Border.SOLID);
        c.setBorderColor(BaseColor.LIGHT_GRAY);
        c.setHorizontalAlignment(Element.ALIGN_RIGHT);
        return c;
    }

    private void renderImage(Document document, String headerImagePath) throws IOException, DocumentException {
        Image image = Image.getInstance(headerImagePath);
        image.setAbsolutePosition(0, 0);
        image.setAlignment(Image.ALIGN_RIGHT);
        image.scaleToFit(PageSize.A4.getWidth(), PageSize.A4.getHeight());
        image.setBorder(2);
        document.add(image);
    }

    @Override
    public void onOpenDocument(PdfWriter writer, Document document) {
        pdfTemplate = writer.getDirectContent().createTemplate(12, 16);
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
        font.setSize(12);

        int totalLength = String.valueOf(writer.getPageNumber()).length();
        int totalWidth = totalLength * 5;
        ColumnText.showTextAligned(pdfTemplate, Element.ALIGN_LEFT,
                new Phrase(String.valueOf(writer.getPageNumber()), font),
                totalWidth, 1 , 0);
    }

}
