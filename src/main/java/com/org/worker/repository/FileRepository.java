package com.org.worker.repository;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

public interface FileRepository {

    Path saveFile(MultipartFile file);

    void removeFile(String path);

    MultipartFile fetchFile(Path path, String contentType);

    MultipartFile fetchFile(String filename, String contentType);
}
