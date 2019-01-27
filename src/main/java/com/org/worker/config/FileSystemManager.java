package com.org.worker.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.nio.file.Paths;

@Getter
@Configuration
public class FileSystemManager {
    @Value("${app.file.limit}")
    private String limit;

    @Value("${app.file.tmp}")
    private String tmp;

    @Value("${app.file.pdf}")
    private String pdfFolder;

    @Value("${app.file.threshold : 5}")
    private int thresholdCount;

    @Bean
    public File pdfDirectory() {
        return Paths.get(pdfFolder).toFile();
    }
}
