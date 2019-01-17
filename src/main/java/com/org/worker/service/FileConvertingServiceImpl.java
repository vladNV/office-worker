package com.org.worker.service;

import com.org.worker.config.ExcelProperties;
import com.org.worker.exception.ConvertingException;
import com.org.worker.repository.FileRepository;
import com.org.worker.service.model.PdfType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Service
public class FileConvertingServiceImpl implements FileConvertingService {

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private ExcelProperties excelProperties;

    @Autowired
    private ExcelWorkerService excelWorkerService;

    @Autowired
    private Set<PdfService> pdfServices;

    @Override
    public String convertToPdf(@NotNull final String path, @NotNull final PdfType pdfType) {
        return pdfServices.stream()
                .filter(pdfService -> pdfService.keyOfImplementation() == pdfType)
                .findFirst()
                .orElseThrow(() -> new ConvertingException("There is no such implementation"))
                .convertToPdf(excelWorkerService.excelData(path));
    }
}
