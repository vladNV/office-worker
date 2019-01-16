package com.org.worker.repository;

import com.org.worker.config.FileSystemManager;
import com.org.worker.exception.FileApiError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

@Slf4j
@Component
public class FileRepositoryImpl implements FileRepository {

    @Autowired
    private FileSystemManager properties;

    @Override
    public Path saveFile(@NotNull final MultipartFile file) {
        try {
            Path path = providePathToTmpFolder();
            LOG.info("About to write file to '{}'", path);
            Files.write(path, file.getBytes(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.SYNC);
            LOG.info("File was written successfully");
            return path;
        } catch (IOException e) {
            LOG.error("Error occurred", e);
            throw new FileApiError(e);
        }
    }

    @Override
    public void removeFile(@NotNull final String path) {
        try {
            LOG.info("About to remove file by path '{}'", path);
            Files.deleteIfExists(Paths.get(path));
        } catch (IOException e) {
            LOG.error("Error occurred", e);
            throw new FileApiError(e);
        }
    }

    @Override
    public MultipartFile fetchFile(@NotBlank final Path path, @NotBlank final String contentType) {
        try {
            return new MultipartArrayByte(path, Files.readAllBytes(path), contentType);
        } catch (IOException e) {
            LOG.error("Error occurred", e.getMessage());
            throw new FileApiError(e);
        }
    }

    @Override
    public MultipartFile fetchFile(@NotBlank final String filename, @NotBlank final String contentType) {
        return fetchFile(providePathToPdfFolder(filename), contentType);
    }

    private Path providePathToPdfFolder(String filename) {
        return Paths.get(properties.getPdf() + File.separator + filename);
    }

    private Path providePathToTmpFolder() {
        return Paths.get(properties.getTmp() + File.separator + UUID.randomUUID().toString());
    }
}
