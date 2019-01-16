package com.org.worker.controller;

import com.org.worker.controller.dto.ConvertingRequest;
import com.org.worker.controller.filter.AuthenticationFilter;
import com.org.worker.exception.AppValidationException;
import com.org.worker.exception.LimitExceededException;
import com.org.worker.repository.FileRepository;
import com.org.worker.repository.PlainTextRepository;
import com.org.worker.service.FileConvertingService;
import com.org.worker.service.model.PdfTemplate;
import com.org.worker.util.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.NotAcceptableStatusException;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/document")
public class DocumentController {
    private static final int ALLOWED_STRING_LENGTH = 100;
    private static final int SIZE = 8 * 1024 * 1024 * 50;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private FileConvertingService fileConvertingService;

    @Autowired
    private PlainTextRepository plainTextRepository;

    @PostMapping(path = "/upload",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity uploadExcelFile(@Valid @NotNull MultipartFile excel) {
        if (excel == null) {
            throw new AppValidationException("Excel must not be null");
        }
        String filename = excel.getName();
        if (filename.length() > ALLOWED_STRING_LENGTH) {
            throw new AppValidationException("Length of filename too long");
        }

        if (excel.isEmpty() || excel.getSize() > SIZE) {
            throw new LimitExceededException("File must not be null or exceeds limit", excel.getSize(), SIZE);
        }

        if (!StringUtils.equalsAnyIgnoreCase(excel.getContentType(), FileUtils.XLS_VALUE, FileUtils.XLSX_VALUE)) {
            throw new NotAcceptableStatusException(Arrays.asList(FileUtils.XLSX, FileUtils.XLS));
        }
        LOG.info("About to upload the file '{}'", filename);
        String path = fileRepository.saveFile(excel).toString();
        LOG.info("The file has been uploaded to '{}'", path);

        return ResponseEntity.ok(path);
    }

    @PostMapping(path = "/convert")
    public ResponseEntity<String> convertDocumentToPdf(
            @Valid @RequestBody ConvertingRequest convertingRequest) {
        LOG.info("Got converting request '{}'", convertingRequest);
        String pathToDownloading = fileConvertingService
                .convertToPdf(convertingRequest.getPath(),
                        PdfTemplate.valueOf(convertingRequest.getPdfTemplate()));
        LOG.info("File has been converted");
        fileRepository.removeFile(convertingRequest.getPath());

        return ResponseEntity.ok(pathToDownloading);
    }

    @GetMapping(path = "/download", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> file(@RequestParam("filename") @NotBlank String filename) throws IOException {
        LOG.info("Getting file '{}'", filename);
        MultipartFile multipartFile = fileRepository.fetchFile(filename, FileUtils.PDF_VALUE);
        LOG.info("Got file '{}'", multipartFile.getOriginalFilename());
        return ResponseEntity.ok(multipartFile.getBytes());
    }

    @GetMapping(path = "/templates")
    public ResponseEntity<List<String>> getAllTemplates() {
        return ResponseEntity.ok(Arrays.stream(PdfTemplate.values())
                .map(PdfTemplate::toString)
                .collect(Collectors.toList()));
    }

    @GetMapping(path = "/")
    public String getDocumentPage() {
        return "processing";
    }

    @Bean
    public FilterRegistrationBean<AuthenticationFilter> authenticationFilter() {
        FilterRegistrationBean<AuthenticationFilter> authenticationFilter = new FilterRegistrationBean<>();
        authenticationFilter.setFilter(new AuthenticationFilter(plainTextRepository));
        authenticationFilter.addUrlPatterns("/document/*");

        return authenticationFilter;
    }

}
