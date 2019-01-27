package com.org.worker.service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Component
public class DefaultPdfWriter extends TablePdfWriter {


    public void depict(List<String> text, String[][] data, Document document) throws DocumentException {
        Font font;
        Font font2 = getPdfProperties().getDefaultFont();

        Paragraph spacing = new Paragraph(NON_BREAKING_SPACE);
        spacing.setSpacingAfter(230);
        document.add(spacing);

        font = font(12);
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new int[]{250, 750});
        table.getDefaultCell().setBorderWidth(0);
        table.getDefaultCell().setFixedHeight(20);

        /* item calibrated */
        makeDataRowOnFirstPage(
                "Объект калибровки", "Item calibrated",
                text.get(2), "Наименование эталона / "
                        + "средства измерения / идентификация\n"
                        + "Description of measurement standard / "
                        + "measuring instrument / identification",
                table);

        /* customer */
        makeDataRowOnFirstPage("Заказчик", "Customer",
                text.get(3),
                "Информация о заказчике, адрес\nName of the customer, address",
                table);


        /* calibration method */
        makeDataRowOnFirstPage("Метод калибровки","Method of calibration",
                text.get(4),
                "Наименнование метода / идентификация\nName of the method / identification"
                , table);
        document.add(table);

        PdfBuilderUtils.changeFont(font, 8, BaseColor.BLACK, Font.ITALIC);
        Chunk under1 = PdfBuilderUtils.generateSpaces(155);
        under1.setUnderline(1f, -2.0f);
        document.add(under1);
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

        table = new PdfPTable(new float[]{400f,100f,600f,250f,200f});
        table.setWidthPercentage(100);

        font.setStyle(Font.NORMAL);
        font2.setStyle(Font.NORMAL);

        PdfPCell c1 = new PdfPCell();
        font.setSize(11);
        font.setColor(BaseColor.BLACK);
        c1.addElement(new Phrase("Утверждающая подпись", font));
        font2.setSize(8);
        c1.addElement(new Phrase("Authorising signature", font2));

        PdfPCell c2 = new PdfPCell();
        Paragraph u1 = new Paragraph(PdfBuilderUtils.generateSpaces(14));
        Chunk ch1 = new Chunk();
        ch1.setUnderline(0.2f, -2.0f);
        c2.addElement(u1);

        PdfPCell c3 = new PdfPCell();
        c3.addElement(new Phrase(text.get(5), font));

        Chunk ch2 = PdfBuilderUtils.generateSpaces(41);
        ch2.setUnderline(0.2f, -2);
        c3.addElement(ch2);
        c3.addElement(new Phrase("Ф.И.О и должность / Name and function", font2));

        PdfPCell c4 = new PdfPCell();
        c4.addElement(new Phrase("Дата выдачи", font));
        c4.addElement(new Phrase("Date of issue", font2));

        PdfPCell c5 = new PdfPCell();
        c5.addElement(new Phrase(text.get(6), font));
        Chunk ch3 = PdfBuilderUtils.generateSpaces(21);
        ch3.setUnderline(0.2f, -2);
        c5.addElement(ch3);

        Stream.of(c1, c2, c3, c4, c5).forEach(c -> c.setBorder(0));
        table.addCell(c1);
        table.addCell(c2);
        table.addCell(c3);
        table.addCell(c4);
        table.addCell(c5);

        document.add(table);
        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);
        document.add(under1);

        font.setSize(8);
        font.setColor(BaseColor.BLUE);
        document.add(PdfBuilderUtils.buildParagraph("Адрес НМИ / Address of NMI / "
                + "Телефон, факс, е-почта, web- сайт / Phone, fax, e-mail, website", font));
        document.newPage();

        PdfBuilderUtils.changeFont(font, 12, BaseColor.BLACK);

        spacing = new Paragraph(NON_BREAKING_SPACE);
        spacing.setSpacingAfter(55);
        document.add(spacing);

        PdfPTable t3 = new PdfPTable(new float[]{300f, 700f});
        t3.setWidthPercentage(100);


        PdfPCell c7 = new PdfPCell();
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

        writeTable(document, data, under1);

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
        Chunk ch8 = new Chunk(PdfBuilderUtils.generateSpaces(20).getContent(), font2);
        ch8.setUnderline(0.2f, -2f);
        p15.add(ch8);
        p15.add(PdfBuilderUtils.generateSpaces(22).getContent());
        Chunk ch9 = new Chunk(PdfBuilderUtils.generateSpaces(73).getContent(), font2);
        ch9.setUnderline(0.2f, -2f);
        p15.add(ch9);
        document.add(p15);
    }
}
