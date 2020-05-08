package com.whut.srms.pojo;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.io.File;

public class MyMultipartFile implements MultipartFile {

    private final byte[] Content;

    public MyMultipartFile(byte[] Content) {
        this.Content = Content;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getOriginalFilename() {
        return null;
    }

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public boolean isEmpty() {
        return Content == null || Content.length == 0;
    }

    @Override
    public long getSize() {
        return Content.length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return Content;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(Content);
    }

    @Override
    public void transferTo(File file) throws IOException, IllegalStateException {
        new FileOutputStream(file).write(Content);
    }
}
