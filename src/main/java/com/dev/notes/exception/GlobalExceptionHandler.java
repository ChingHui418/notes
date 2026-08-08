package com.dev.notes.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * @RestControllerAdvice：
 * 告訴 Spring 這是全域攔截器，
 * 只要 Controller 發生例外，都會先來這裡處理
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
    * @ExceptionHandler：指定要攔截哪一種錯誤
    * MethodArgumentNotValidException 為 @Valid 驗證失敗時噴出的錯誤
    */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException e) {

        // 準備乾淨的 Map，用來裝 -> 哪個欄位出錯：錯誤訊息
        Map<String, String> errors = new HashMap<>();

        // 迴圈把剛才 Json 裡的 errors 陣列抓出來，只提取需要的資訊
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField(); // 取出錯誤欄位名稱，如 "title"
            String errorMessage = error.getDefaultMessage();   // 取得在 DTO 寫的錯誤訊息，如 "筆記標題不能為空"
            errors.put(fieldName, errorMessage);
        });

        // 重新包裝成 400 Bad Request 回傳給前端
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}
