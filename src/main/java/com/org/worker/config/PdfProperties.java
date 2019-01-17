package com.org.worker.config;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.BaseFont;
import com.org.worker.exception.FileWriterException;
import com.org.worker.service.DefaultPdfPageEventHelper;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.io.IOException;

@Getter
@Configuration
public class PdfProperties {
    @Value("${pdf.system.font}")
    private String font;

    public Font getDefaultFont() {
        try {
            BaseFont bf = BaseFont.createFont(font, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            return new Font(bf, 14);
        } catch (DocumentException | IOException e) {
            throw new FileWriterException("Error occurred while creating pdf document", e);
        }
    }

    @Bean
    @Scope("prototype")
    public DefaultPdfPageEventHelper pdfPageEventHelper() {
        return new DefaultPdfPageEventHelper();
    }
}
