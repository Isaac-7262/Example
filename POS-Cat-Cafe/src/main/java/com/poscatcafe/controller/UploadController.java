package com.poscatcafe.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.poscatcafe.service.FileStorageService;

import java.util.Map;

/**
 * รับคำขออัปโหลดไฟล์ต่าง ๆ สำหรับระบบ เช่น รูปภาพสินค้า
 */
@RestController
@RequestMapping("/api/uploads")
public class UploadController {

    private final FileStorageService storageService;

    /**
     * สร้างคอนโทรลเลอร์พร้อมบริการจัดเก็บไฟล์
     */
    public UploadController(FileStorageService storageService) {
        this.storageService = storageService;
    }

    /**
     * อัปโหลดรูปภาพสินค้าจากคำขอแล้วคืนค่า URL ที่สามารถเข้าถึงได้
     */
    @PostMapping("/products")
    public ResponseEntity<Map<String, Object>> uploadProductImage(
            @RequestParam("file") MultipartFile file) {
        String url = storageService.storeProductImage(file);
        return ResponseEntity.ok(Map.of("url", url));
    }
}
