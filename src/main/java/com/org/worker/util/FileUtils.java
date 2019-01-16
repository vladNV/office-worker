package com.org.worker.util;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;

import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUtils {
    public static final String XLS_VALUE = "application/vnd.ms-excel";
    public static final String XLSX_VALUE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public static final String PDF_VALUE = "pdf";
    public static final MediaType XLS = MediaType.valueOf("application/vnd.ms-excel");
    public static final MediaType XLSX = MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    public static final String SEPARATOR = ";";

    public static String generateName(String extension) {
        return UUID.randomUUID().toString().concat(".").concat(extension);
    }
}
