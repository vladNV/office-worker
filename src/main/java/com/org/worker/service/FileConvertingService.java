package com.org.worker.service;

import com.org.worker.service.model.PdfTemplate;

public interface FileConvertingService {

    String convertToPdf(String path, PdfTemplate pdfTemplate);

}
