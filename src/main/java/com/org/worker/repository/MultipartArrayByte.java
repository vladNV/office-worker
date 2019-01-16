package com.org.worker.repository;

import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public class MultipartArrayByte implements MultipartFile {
    private Path path;
    private byte[] bytes;
    private String contentType;

    public MultipartArrayByte(Path path, byte[] bytes, String  contentType) {
        Assert.notNull(path, "path must not be null");
        Assert.notNull(bytes, "bytes must not be null");
        this.path = path;
        this.bytes = bytes;
        this.contentType = contentType;
    }

    @Override
    public String getName() {
        return path.toString();
    }

    @Override
    public String getOriginalFilename() {
        return path.toAbsolutePath().toString();
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public boolean isEmpty() {
        return bytes.length == 0;
    }

    @Override
    public long getSize() {
        return bytes.length;
    }

    @Override
    public byte[] getBytes() {
        return bytes;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(bytes);
    }

    @Override
    public void transferTo(File file) throws IllegalStateException {
        throw new UnsupportedOperationException();
    }
}
