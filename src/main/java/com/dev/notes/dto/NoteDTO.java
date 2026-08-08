package com.dev.notes.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NoteDTO {

    private Long id;

    // @NotBlank: 確保字串不能是 null，也不能是""或空白
    @NotBlank(message = "筆記標題不能為空")
    private String title;

    @NotBlank(message = "筆記分類不能為空")
    private String type;

    @NotBlank(message = "筆記內容不能為空")
    private String content;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
