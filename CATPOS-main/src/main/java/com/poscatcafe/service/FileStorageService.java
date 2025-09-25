package com.poscatcafe.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

/**
 * จัดการการจัดเก็บไฟล์ที่อัปโหลด เช่น รูปภาพสินค้า
 */
@Service
public class FileStorageService {

    private final Path uploadRoot;
    private final Path productImageDir;

    /**
     * เตรียมโครงสร้างโฟลเดอร์สำหรับเก็บไฟล์เมื่อบริการถูกสร้างขึ้น
     */
    public FileStorageService() {
        try {
            this.uploadRoot = Paths.get("uploads").toAbsolutePath().normalize();
            this.productImageDir = uploadRoot.resolve("products");
            Files.createDirectories(productImageDir);
        } catch (IOException e) {
            throw new RuntimeException("ไม่สามารถเตรียมโฟลเดอร์สำหรับเก็บไฟล์ได้", e);
        }
    }

    /**
     * บันทึกรูปภาพสินค้าและคืน URL สำหรับเข้าถึงไฟล์
     */
    public String storeProductImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("ไฟล์ว่าง หรือไม่ได้เลือกไฟล์");
        }

        String originalName = file.getOriginalFilename();
        String ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf('.'));
        }

        String filename = UUID.randomUUID().toString().replaceAll("-", "") + ext;
        Path targetPath = productImageDir.resolve(filename);

        try {
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("ไม่สามารถอัปโหลดไฟล์ได้", e);
        }

        return "/uploads/products/" + filename;
    }
}
