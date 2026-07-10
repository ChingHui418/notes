package com.dev.notes.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NoteDTO {

    private Long id;
    private String title;
    private String type;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
