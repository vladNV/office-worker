package com.org.worker.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class PdfProperties {
    @Value("${pdf.system.font}")
    private String font;
}
