package com.org.worker.service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.org.worker.config.FileSystemManager;
import com.org.worker.config.PdfProperties;
import com.org.worker.exception.ConvertingException;
import com.org.worker.exception.FileWriterException;
import com.org.worker.service.model.ExcelSheet;
import com.org.worker.service.model.ExcelSheetStyle;
import com.org.worker.service.model.PdfTemplate;
import com.org.worker.util.ConverterUtils;
import com.org.worker.util.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Component
public class DefaultPdfWriter implements PdfService {
    @Autowired
    private FileSystemManager fileSystemManager;

    @Autowired
    private PdfProperties pdfProperties;

    @Override
    public String convertToPdf(List<ExcelSheet> sheetList) {
        String filename = FileUtils.generateName(FileUtils.PDF_VALUE);
        String path = fileSystemManager.getPdf() + File.separator + filename;
        Document document = new Document(PageSize.A4);
        try {
            PdfWriter pdfWriter = setupWriter(document, path);
            write(
                    getRows(ExcelSheetStyle.SINGLE_COLUMN, sheetList),
                    ConverterUtils.toTable(getRows(ExcelSheetStyle.TABLE, sheetList), FileUtils.SEPARATOR),
                    document, pdfWriter
            );
        } catch (DocumentException e) {
            throw new ConvertingException("Could not convert file");
        }
        document.close();
        return filename;
    }

    private List<String> getRows(ExcelSheetStyle sheetStyle, List<ExcelSheet> sheets) {
        return sheets.stream()
                .filter(s -> s.getExcelSheetStyle() == sheetStyle)
                .findFirst().get().getRows();
    }

    @Override
    public PdfTemplate keyOfImplementation() {
        return PdfTemplate.PDF_MAIN;
    }


    private PdfWriter setupWriter(Document document, String path) {
        LOG.info("About to save data to '{}'", path);
        try {
            PdfWriter pdf = PdfWriter.getInstance(document, new FileOutputStream(path));
            pdf.setPdfVersion(PdfWriter.PDF_VERSION_1_7);
            document.open();
            return pdf;
        } catch (DocumentException | FileNotFoundException e) {
            throw new FileWriterException("Error occurred while opening document", e);
        }
    }

    private Font getFont() {
        try {
            LOG.info("Using system font '{}'", pdfProperties.getFont());
            BaseFont bf = BaseFont.createFont(pdfProperties.getFont(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            return new Font(bf, 14);
        } catch (DocumentException | IOException e) {
            throw new FileWriterException("Error occurred while creating pdf document", e);
        }
    }

    private void writeTable(Document document, String[][] table, Chunk under1,
                            String sertificate, PdfWriter writer) throws DocumentException {
        PdfPTable headers = allocatePdfTableSize(table[0].length);
        writeHeaders(headers, table[0], getTableFont());
        document.add(headers);

        int currentPage = writer.getCurrentPageNumber();
        for (int i = 1; i < table.length; i++) {
            int pageNumber = writer.getCurrentPageNumber();
            if (currentPage != pageNumber) {
                addPageTable(sertificate, writer.getCurrentPageNumber(), document);
                currentPage = pageNumber;
            }
            PdfPTable pdfPTable = allocatePdfTableSize(table[0].length);
            for (int j = 0; j < table[i].length; j++) {
                pdfPTable.addCell(new PdfPCell(new Phrase(table[i][j])));
            }
            document.add(pdfPTable);
        }
        document.add(under1);
        document.add(Chunk.NEWLINE);
    }

    private PdfPTable allocatePdfTableSize(int length) {
        float rowSize = 1200f;
        float[] sizes = new float[length];

        Arrays.fill(sizes, rowSize / length);

        PdfPTable table = new PdfPTable(sizes);
        table.setWidthPercentage(100);

        return table;
    }

    private void writeHeaders(PdfPTable pdfPTable, String[] headers, Font font) {
        Arrays.stream(headers).forEach(head -> {
            PdfPCell cell = new PdfPCell();
            cell.addElement(new Phrase(head, font));
            pdfPTable.addCell(cell);
        });
    }

    private Font getTableFont() {
        Font font = new Font();
        font.setSize(12);
        font.setStyle(Font.BOLD);

        return font;
    }

    private void addPageTable(String sertificate, int pageNumber, Document document) throws DocumentException {
        Font font = getFont();
        Font font2 = getFont();

        font.setSize(14);
        font2.setSize(10);

        PdfPTable t2 = new PdfPTable(new float[]{750f, 250f});
        t2.setWidthPercentage(100);
        t2.setSpacingAfter(30f);

        PdfPCell c1 = new PdfPCell();
        c1.addElement(new Phrase("Номер сертификата  " + sertificate, font));

        font2.setStyle(Font.NORMAL);
        Phrase ph2 = new Phrase();
        ph2.add(new Phrase("Certificate number ______________________________", font2));
        Chunk ch4 = generateSpaces(35);
        ch4.setUnderline(0.2f,-2);
        ph2.add(ch4);
        c1.addElement(ph2);
        c1.addElement(Chunk.NEWLINE);

        PdfPCell c2 = new PdfPCell();
        Phrase pagePhrase = new Phrase("Страница " + pageNumber + " из {sizeOfPage}", font2);
        c2.addElement(pagePhrase);
        c2.addElement(new Phrase("Page ____ of ____", font2));

        document.add(Chunk.NEWLINE);
        t2.addCell(c1);
        t2.addCell(c2);
        document.add(t2);
    }

    @Deprecated
    private void write(List<String> text, String[][] table, Document document, PdfWriter pdfWriter)
            throws DocumentException {
        Font font = getFont();
        PdfBuilderUtils.changeFont(font, 20, BaseColor.BLUE, Font.BOLD);

        document.add(PdfBuilderUtils.buildParagraph("НАЦИОНАЛЬНЫЙ ИНСТИТУТ МЕТРОЛОГИИ", font, Element.ALIGN_CENTER));

        PdfBuilderUtils.changeFont(font, 16, BaseColor.BLUE, Font.NORMAL);
        document.add(PdfBuilderUtils.buildParagraph("National metrological institute", font, Element.ALIGN_CENTER));
        document.add(Chunk.NEWLINE);

        PdfBuilderUtils.changeFont(font,24, BaseColor.BLACK, Font.BOLD);
        document.add(PdfBuilderUtils.buildParagraph("Сертификат калибровки", font, Element.ALIGN_CENTER));

        PdfBuilderUtils.changeFont(font, 16, BaseColor.BLACK, Font.NORMAL);
        document.add(PdfBuilderUtils.buildParagraph("Calibration certificate", font, Element.ALIGN_CENTER));
        document.add(Chunk.NEWLINE);


        PdfBuilderUtils.changeFont(font, 12);
        document.add(PdfBuilderUtils.buildParagraph(
                "Номер сертификата "
                        + text.get(0)
                        + "  Дата калибровки  "
                        + text.get(1)
                        + " "
                        + "Страница  "
                        + pdfWriter.getCurrentPageNumber()
                        + "   из   {sizeOfPage}", font));

        PdfBuilderUtils.changeFont(font, 10);
        document.add(PdfBuilderUtils.buildParagraph("Certificate number       ____________   "
                + "Date when celebrated"
                + "      ____________   Page      ____ of ____", font));

        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);

        PdfPTable t1 = new PdfPTable(new float[]{250f,750f});
        t1.setWidthPercentage(100);

        PdfPCell c1 = new PdfPCell();
        font.setSize(12);
        c1.addElement(new Phrase("Объект калибровки", font));

        Font font2 = getFont();
        font2.setSize(8);
        c1.addElement(new Phrase("Item calibrated", font2));
        c1.setBorder(0);
        t1.addCell(c1);

        PdfPCell c2 = new PdfPCell();
        Chunk ch1 = new Chunk(text.get(2), font);
        ch1.setUnderline(0.2f, -2f);
        c2.addElement(ch1);
        font.setSize(8);
        font.setColor(BaseColor.BLUE);
        Phrase ph1 = new Phrase("Наименование эталона / средства измерения / идентификация", font);
        ph1.add(Chunk.NEWLINE);
        ph1.add("Description of measurement standard / measuring instrument / identification");
        c2.addElement(ph1);
        c2.setBorder(0);
        t1.addCell(c2);

        PdfPCell c3 = new PdfPCell();
        font.setSize(12);
        font.setColor(BaseColor.BLACK);
        c3.addElement(new Phrase("Заказчик", font));
        font2.setSize(8);
        c3.addElement(new Phrase("Customer", font2));
        c3.setBorder(0);
        t1.addCell(c3);

        PdfPCell c4 = new PdfPCell();
        ch1 = new Chunk(text.get(3), font);
        ch1.setUnderline(0.2f, -2f);
        c4.addElement(ch1);
        font.setSize(8);
        font.setColor(BaseColor.BLACK);
        font2.setColor(BaseColor.BLUE);
        ph1 = new Phrase("Информация о заказчике, адрес", font2);
        ph1.add(Chunk.NEWLINE);
        ph1.add("Name of the customer, address");
        c4.addElement(ph1);
        c4.setBorder(0);
        t1.addCell(c4);

        PdfPCell c5 = new PdfPCell();
        font.setSize(12);

        c5.addElement(new Phrase("Метод калибровки", font));
        font2.setSize(8);
        font2.setColor(BaseColor.BLACK);
        c5.addElement(new Phrase("Method of calibration", font2));
        c5.setBorder(0);
        t1.addCell(c5);

        PdfPCell c6 = new PdfPCell();
        ch1 = new Chunk(text.get(4), font);
        ch1.setUnderline(0.2f, -2f);
        c6.addElement(ch1);
        font.setSize(8);
        font.setColor(BaseColor.BLUE);
        ph1 = new Phrase("Наименнование метода / идентификация", font);
        ph1.add(Chunk.NEWLINE);
        ph1.add("Name of the method / identification");
        c6.addElement(ph1);
        c6.setBorder(0);
        t1.addCell(c6);
        document.add(t1);
        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);


        PdfBuilderUtils.changeFont(font, 8, BaseColor.BLACK, Font.ITALIC);
        Chunk under1 = generateSpaces(155);
        under1.setUnderline(1f, -2.0f);
        document.add(under1);
        document.add(Chunk.NEWLINE);
        
        document.add(Chunk.NEWLINE);
        document.add(PdfBuilderUtils.buildParagraph("Все измерения имеют прослеживаемость к единицам Международной системы SI, "
                + "которые воспроизводятся национальными эталонам НМИ. В"
                + "сертификате приведены результаты калибровки согласующиеся с возможностями, содержащимися в Приложении С соглашения MRA, разработанном"
                + "МКМВ. В рамках MRA все участвующие НМИ взаимно признают действительность своих сертификатов калибровки и измерений в отношении"
                + "измеренных значений, диапазонов и неопределенностей измерений, указанных в Приложении С (подробности см. http://www.bipm.org). Данный"
                + "сертификат может быть воспроизведен только полностью. Любая публикация или частичное воспроизведение содержания сертификата возможны с"
                + "письменного разрешения НМИ, выдавшего сертификат.", font));
        document.add(PdfBuilderUtils.buildParagraph("All measurements are traceable to the SI units which are realized by national "
                + "measurement standards of NMI. This certificate is consistent with the capabilities that"
                + "are included in Appendix C of the MRA drawn up by the CIPM. Under the MRA, all participating NMIs recognize the validity "
                + "of each other's calibration and"
                + "measurement certificates for the quantities, ranges and measurement uncertainties specified in Appendix C "
                + "(for details see http://www.bipm.org). This certificate"
                + "shall not be reproduced, except in full. Any publication extracts from the calibration certificate "
                + "requires written approval of the issuing NMI.", font));
        document.add(Chunk.NEWLINE);
        document.add(under1);
        document.add(Chunk.NEWLINE);

        t1 = new PdfPTable(new float[]{400f,100f,600f,250f,200f});
        t1.setWidthPercentage(100);

        font.setStyle(Font.NORMAL);
        font2.setStyle(Font.NORMAL);

        c1 = new PdfPCell();
        font.setSize(11);
        font.setColor(BaseColor.BLACK);
        c1.addElement(new Phrase("Утверждающая подпись", font));
        font2.setSize(8);
        c1.addElement(new Phrase("Authorising signature", font2));

        c2 = new PdfPCell();
        Paragraph u1 = new Paragraph(generateSpaces(14));
        ch1.setUnderline(0.2f, -2.0f);
        c2.addElement(u1);

        c3 = new PdfPCell();
        c3.addElement(new Phrase(text.get(5), font));

        Chunk ch2 = generateSpaces(41);
        ch2.setUnderline(0.2f, -2);
        c3.addElement(ch2);
        c3.addElement(new Phrase("Ф.И.О и должность / Name and function", font2));

        c4 = new PdfPCell();
        c4.addElement(new Phrase("Дата выдачи", font));
        c4.addElement(new Phrase("Date of issue", font2));

        c5 = new PdfPCell();
        c5.addElement(new Phrase(text.get(6), font));
        Chunk ch3 = generateSpaces(21);
        ch3.setUnderline(0.2f, -2);
        c5.addElement(ch3);

        Stream.of(c1, c2, c3, c4, c5).forEach(c -> c.setBorder(0));

        t1.addCell(c1);
        t1.addCell(c2);
        t1.addCell(c3);
        t1.addCell(c4);
        t1.addCell(c5);

        document.add(t1);
        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);
        document.add(under1);

        font.setSize(8);
        font.setColor(BaseColor.BLUE);
        document.add(PdfBuilderUtils.buildParagraph("Адрес НМИ / Address of NMI / "
                + "Телефон, факс, е-почта, web- сайт / Phone, fax, e-mail, website", font));
        document.newPage();

        PdfBuilderUtils.changeFont(font, 13, BaseColor.BLACK, Font.BOLD);
        document.add(PdfBuilderUtils.buildParagraph("Сертификат калибровки", font));

        font2.setSize(10);
        document.add(PdfBuilderUtils.buildParagraph("Calibration certificate", font2));

        addPageTable(text.get(0), pdfWriter.getCurrentPageNumber(), document);

        PdfPTable t3 = new PdfPTable(new float[]{300f, 700f});
        t3.setWidthPercentage(100);

        PdfPCell c7 = new PdfPCell();
        font.setSize(12);
        c7.addElement(new Phrase("Калибровка выполнена с помощью", font));
        font2.setSize(8);
        c7.addElement(new Phrase("Calibration is performed by using", font2));
        c7.setBorder(0);
        t3.addCell(c7);

        PdfPCell c8 = new PdfPCell();
        Chunk ch5 = new Chunk(text.get(7), font);
        ch5.setUnderline(0.2f, -2f);
        c8.addElement(ch5);
        font2.setSize(8);
        font2.setColor(BaseColor.BLUE);
        Phrase ph3 = new Phrase("Наименование эталонов и их статус / идентификация "
                + "/ доказательство прослеживаемости", font2);
        ph3.add(Chunk.NEWLINE);
        ph3.add(new Phrase("Description of the reference measurement standards / "
                + "identification / evidence of traceability", font2));
        c8.addElement(ph3);
        c8.setBorder(0);
        t3.addCell(c8);

        PdfPCell c9 = new PdfPCell();
        font.setSize(12);
        c9.addElement(new Phrase("Условия калибровки", font));
        font2.setSize(8);
        font2.setColor(BaseColor.BLACK);
        c9.addElement(new Phrase("Calibration conditions", font2));
        c9.setBorder(0);
        t3.addCell(c9);

        PdfPCell c10 = new PdfPCell();
        Chunk ch6 = new Chunk(text.get(8), font);
        ch6.setUnderline(0.2f, -2f);
        c10.addElement(ch6);
        font.setSize(8);
        font.setColor(BaseColor.BLUE);
        Phrase ph4 = new Phrase("Условия окружающей среды и другие влияющие факторы", font);
        ph4.add(Chunk.NEWLINE);
        ph4.add("Environmental conditions and other influence parameters");
        c10.addElement(ph4);
        c10.setBorder(0);
        t3.addCell(c10);
        ph4.add(Chunk.NEWLINE);

        document.add(t3);

        PdfBuilderUtils.changeFont(font, 12, BaseColor.BLACK);
        document.add(PdfBuilderUtils.buildParagraph("Результаты калибровки, включая неопределенность",font));

        font.setSize(8);
        document.add(PdfBuilderUtils.buildParagraph("Calibration results including uncertainty", font));
        document.add(Chunk.NEWLINE);

        writeTable(document, table, under1, text.get(0), pdfWriter);

        font.setStyle(Font.ITALIC);
        font.setSize(8);
        document.add(PdfBuilderUtils.buildParagraph("Расширенная неопределенность получена путем умножения стандартной "
                + "неопределенности на коэффициент охвата k = 2, соответствующего уровню"
                + "доверия приблизительно равному 95 % при допущении нормального распределения."
                + "Оценивание неопределенности проведено в соответствии с"
                + "«Руководством по выражению неопределенности измерений» (GUM)."
                + "The expanded uncertainty is obtained by multiplying the combined standard uncertainty by "
                + "a coverage factor k = 2 corresponding to a confidence interval of"
                + "approximately 95 % assuming a normal distribution. The evaluation of uncertainty is"
                + " conducted according to the “Guide to the expression of uncertainty in"
                + "measurement” (GUM)", font));
        document.add(under1);
        document.add(Chunk.NEWLINE);

        document.newPage();
        addPageTable(text.get(0), pdfWriter.getCurrentPageNumber(), document);

        PdfPTable t5 = new PdfPTable(new float[]{400f, 600f});
        t5.setWidthPercentage(100);

        font.setStyle(Font.NORMAL);
        PdfPCell c16 = new PdfPCell();
        font.setSize(12);
        c16.addElement(new Phrase("Дополнительная информация", font));
        font2.setSize(8);
        font2.setColor(BaseColor.BLACK);
        c16.addElement(new Phrase("Additional information", font2));
        c16.setBorder(0);
        t5.addCell(c16);

        PdfPCell c17 = new PdfPCell();
        Chunk ch7 = new Chunk(text.get(9), font);
        ch7.setUnderline(0.2f, -2f);
        c17.addElement(ch7);
        font.setSize(8);
        font.setColor(BaseColor.BLUE);
        Phrase ph5 = new Phrase("состояние объекта калибровки / регулировка и/или " +
                "ремонт объекта калибровки до его калибровки /\n" +
                "рекомендуемый межкалибровочный интервал по требованию заказчика", font);
        ph5.add(Chunk.NEWLINE);
        ph5.add("condition of the item of calibration / adjustments or repair of " +
                "the item of calibration before calibrated /\n" +
                "recommended recalibration period, if requested by the customer");
        c17.addElement(ph5);
        c17.setBorder(0);
        t5.addCell(c17);
        document.add(t5);

        document.add(Chunk.NEWLINE);

        font.setSize(13);
        font.setColor(BaseColor.BLACK);
        Paragraph p14 = new Paragraph("Подпись лица, выполнившего калибровку              " +
                text.get(10), font);
        document.add(p14);
        document.add(Chunk.NEWLINE);

        font2.setSize(8);
        Paragraph p15 = new Paragraph("Signature of the person " +
                "who has performed calibration         ", font2);
        Chunk ch8 = new Chunk(generateSpaces(20).getContent(), font2);
        ch8.setUnderline(0.2f, -2f);
        p15.add(ch8);
        p15.add(generateSpaces(22).getContent());
        Chunk ch9 = new Chunk(generateSpaces(73).getContent(), font2);
        ch9.setUnderline(0.2f, -2f);
        p15.add(ch9);
        document.add(p15);
    }


    private Chunk generateSpaces(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(StringUtils.SPACE);
        }
        return new Chunk(sb.toString());
    }
}
