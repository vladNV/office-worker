package com.org.worker.service;

import com.org.worker.config.FileSystemManager;
import com.org.worker.repository.FileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDateTime;
import java.util.TimerTask;
import java.util.stream.IntStream;

@Slf4j
@Component
public class DocumentGarbageCollector extends TimerTask {

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private FileSystemManager fileSystemManager;

    @Autowired
    private File pdfDirectory;

    @Override
    public void run() {
        LOG.info("Run job at {}", LocalDateTime.now());
        String[] files = pdfDirectory.list();
        if (files != null) {
            if (files.length > fileSystemManager.getThresholdCount()) {
                IntStream.range(0, files.length).forEach(
                        i -> fileRepository.removeFile(pdfDirectory.getPath() + File.separator + files[i]));
                LOG.info("All files removed");
                return;
            }
            LOG.info("Threshold is not exceeded");
            return;
        }
        LOG.info("Nothing to delete");
    }
}
