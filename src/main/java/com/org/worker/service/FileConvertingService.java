package com.org.worker.service;

import com.org.worker.service.model.PdfType;

public interface FileConvertingService {

    String convertToPdf(String path, PdfType pdfType);

}
