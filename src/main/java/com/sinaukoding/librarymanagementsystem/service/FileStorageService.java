package com.sinaukoding.librarymanagementsystem.service;

import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Path;

public interface FileStorageService {
    String storeFile(MultipartFile file, String subDirectory);
    byte[] readFile(String filePath);
    void deleteFile(String filePath);
}
