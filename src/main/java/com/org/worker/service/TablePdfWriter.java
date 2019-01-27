package com.org.worker.service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

import java.util.Arrays;

public abstract class TablePdfWriter extends AbstractPdfWriter {
    public static final String NON_BREAKING_SPACE = "\u00a0";

    protected void writeTable(Document document, String[][] table, Chunk under1) throws DocumentException {
        PdfPTable pdfTable = allocatePdfTableSize(table[0].length);
        writeHeaders(pdfTable, table[0], getTableFont());

        for (int i = 1; i < table.length; i++) {
            for (int j = 0; j < table[i].length; j++) {
                pdfTable.addCell(new PdfPCell(new Phrase(table[i][j])));
            }
        }

        document.add(pdfTable);
        document.add(under1);
        document.add(Chunk.NEWLINE);
    }

    protected PdfPTable allocatePdfTableSize(int length) {
        float rowWidth = 1200f;
        float[] sizes = new float[length];

        Arrays.fill(sizes, rowWidth / length);

        PdfPTable table = new PdfPTable(sizes);
        table.setWidthPercentage(100);

        return table;
    }

    protected void writeHeaders(PdfPTable pdfPTable, String[] headers, Font font) {
        Arrays.stream(headers).forEach(head -> {
            PdfPCell cell = new PdfPCell();
            cell.addElement(new Phrase(head, font));
            pdfPTable.addCell(cell);
        });
    }

    protected Font getTableFont() {
        Font font = getPdfProperties().getDefaultFont();
        font.setSize(12);
        font.setStyle(Font.BOLD);

        return font;
    }

    protected void makeDataRowOnFirstPage(String main, String enMain,
                                          String value, String description,
                                          PdfPTable table) {
        Font font;

        // 1
        font = font(12);
        table.addCell(new Phrase(main, font));

        // 2
        Chunk item = new Chunk(value, font);
        item.setUnderline(0.2f, -2f);
        table.addCell(new Phrase(item));

        // 3
        font = font(8);
        table.addCell(new Phrase("Item calibrated", font));

        // 4
        font = font(8);
        font.setColor(BaseColor.BLUE);
        Phrase desc = new Phrase(description, font);
        table.addCell(desc);
    }

    protected Font font(int size) {
        Font font = getPdfProperties().getDefaultFont();
        font.setSize(size);
        return font;
    }
}
