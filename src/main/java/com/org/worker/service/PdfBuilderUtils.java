package com.org.worker.service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import org.apache.commons.lang3.StringUtils;

public class PdfBuilderUtils {

    static Chunk generateSpaces(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(StringUtils.SPACE);
        }
        return new Chunk(sb.toString());
    }

    static Font buildFont(int size, int style) {
        Font font = new Font();
        font.setSize(size);
        font.setStyle(style);

        return font;
    }

    static void changeFont(Font font, int size) {
        changeFont(font, size, BaseColor.BLACK, Font.NORMAL);
    }

    static void changeFont(Font font, int size, BaseColor color) {
        changeFont(font, size, color, Font.NORMAL);
    }

    static void changeFont(Font font, int size, BaseColor color, int style) {
        font.setSize(size);
        font.setColor(color);
        font.setStyle(style);
    }

    static Paragraph buildParagraph(String text) {
        return new Paragraph(text);
    }

    static Paragraph buildParagraph(String text, Font font) {
        return new Paragraph(text, font);
    }

    static Paragraph buildParagraph(String text, Font font, int alignment) {
        Paragraph paragraph = new Paragraph(text, font);
        paragraph.setAlignment(alignment);

        return paragraph;
    }

}
