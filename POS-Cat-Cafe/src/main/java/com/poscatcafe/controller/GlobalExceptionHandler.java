package com.poscatcafe.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * จัดการข้อยกเว้นที่เกิดขึ้นทั่วทั้งแอปพลิเคชันและแปลงเป็นการตอบกลับที่เหมาะสม
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * จัดการข้อผิดพลาดการตรวจสอบข้อมูลที่ส่งมาจากไคลเอนต์
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errs = new HashMap<>();
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errs.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errs);
    }

    /**
     * จัดการข้อผิดพลาดอื่น ๆ ที่ไม่ระบุและส่งสถานะ 500 กลับไป
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleOther(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", ex.getClass().getSimpleName(), "message", ex.getMessage()));
    }
}
