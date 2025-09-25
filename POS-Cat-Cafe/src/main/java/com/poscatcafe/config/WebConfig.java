package com.poscatcafe.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * กำหนดค่าการให้บริการไฟล์คงที่ เช่น ไฟล์ที่อัปโหลดไว้ในระบบ
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * เพิ่มตัวจัดการทรัพยากรเพื่อให้เสิร์ฟไฟล์ภายใต้โฟลเดอร์ uploads ผ่าน HTTP
     */
    @Override
    public void addResourceHandlers(@org.springframework.lang.NonNull ResourceHandlerRegistry registry) {
        Path uploadDir = Paths.get("uploads").toAbsolutePath().normalize();
        String uploadPath = uploadDir.toUri().toString();

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadPath);
    }
}
