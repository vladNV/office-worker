package com.org.worker.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class FileSystemManager {
    @Value("${app.file.limit}")
    private String limit;

    @Value("${app.file.tmp}")
    private String tmp;

    @Value("${app.file.pdf}")
    private String pdf;
}
