package com.org.worker.service;

import com.org.worker.config.FileSystemManager;
import com.org.worker.config.PdfProperties;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;

@Getter
public abstract class AbstractPdfWriter {

    @Autowired
    private FileSystemManager fileSystemManager;

    @Autowired
    private PdfProperties pdfProperties;

    public String generatePath(String filename) {
        return fileSystemManager.getPdfFolder() + File.separator + filename;
    }

}
