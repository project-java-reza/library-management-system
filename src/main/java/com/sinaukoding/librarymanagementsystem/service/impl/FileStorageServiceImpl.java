package com.sinaukoding.librarymanagementsystem.service.impl;

import com.sinaukoding.librarymanagementsystem.service.FileStorageService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${app.upload.dir:./uploads}")
    private String uploadDir;

    @Value("${app.upload.max-size:524288}")
    private long maxFileSize;

    private Path rootLocation;

    @PostConstruct
    public void init() {
        try {
            rootLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(rootLocation);
            log.info("Upload directory initialized at: {}", rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory", e);
        }
    }

    @Override
    public String storeFile(MultipartFile file, String subDirectory) {
        try {
            // Validate file
            if (file.isEmpty()) {
                throw new RuntimeException("File tidak boleh kosong");
            }

            // Validate file size (max 500 KB)
            if (file.getSize() > maxFileSize) {
                long maxSizeInKB = maxFileSize / 1024;
                throw new RuntimeException("Ukuran file terlalu besar. Maksimal " + maxSizeInKB + " KB");
            }

            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || (!contentType.startsWith("image/") && !contentType.equals("application/octet-stream"))) {
                throw new RuntimeException("Hanya file gambar yang diperbolehkan");
            }

            // Create subdirectory if not exists
            Path subDirPath = rootLocation.resolve(subDirectory);
            if (!Files.exists(subDirPath)) {
                Files.createDirectories(subDirPath);
                log.info("Created subdirectory: {}", subDirPath);
            }

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
            Path targetLocation = subDirPath.resolve(uniqueFilename);

            // Store file
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            log.info("File stored successfully: {}", targetLocation);

            // Return relative path from uploads directory
            return "/" + subDirectory + "/" + uniqueFilename;

        } catch (IOException e) {
            throw new RuntimeException("Gagal menyimpan file", e);
        }
    }

    @Override
    public byte[] readFile(String filePath) {
        try {
            Path path = rootLocation.resolve(filePath).normalize();
            if (!Files.exists(path) || !Files.isReadable(path)) {
                throw new RuntimeException("File tidak ditemukan: " + filePath);
            }
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new RuntimeException("Gagal membaca file", e);
        }
    }

    @Override
    public void deleteFile(String filePath) {
        try {
            Path path = rootLocation.resolve(filePath).normalize();
            if (Files.exists(path)) {
                Files.delete(path);
                log.info("File deleted successfully: {}", path);
            }
        } catch (IOException e) {
            throw new RuntimeException("Gagal menghapus file", e);
        }
    }
}
