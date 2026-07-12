package com.dev.notes.controller;

import com.dev.notes.dto.NoteDTO;
import com.dev.notes.service.NoteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/notes")
@CrossOrigin(origins = "*")
public class NoteController {

    @Autowired
    private NoteService noteService;

    @GetMapping
    public ResponseEntity<List<NoteDTO>> getAllNotes() {
        log.info("收到 API 請求：取的所有筆記");
        List<NoteDTO> notes = noteService.getAllNotes();
        // 回傳 HTTP 狀態碼 200 與資料
        return ResponseEntity.ok(notes);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<NoteDTO>> getNotesByType(@PathVariable String type) {
        log.info("收到 API 請求：取得分類為 [{}] 的筆記", type);
        List<NoteDTO> notes = noteService.getNotesByType(type);
        return ResponseEntity.ok(notes);
    }

    @PostMapping
    public ResponseEntity<NoteDTO> createNote(@RequestBody NoteDTO noteDTO) {
        log.info("收到 API 請求：新增筆記");
        NoteDTO createdNote = noteService.createNote(noteDTO);
        // 回傳 HTTP 狀態碼 201
        return ResponseEntity.status(HttpStatus.CREATED).body(createdNote);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoteDTO> updateNote(@PathVariable Long id, @RequestBody NoteDTO noteDTO) {
        log.info("收到 API 請求：更新 ID [{}] 的筆記", id);
        NoteDTO updatedNote = noteService.updateNote(id, noteDTO);

        if (updatedNote != null) {
            return ResponseEntity.ok(updatedNote);
        } else {
            // 如果找不到該筆記，回傳 HTTP 狀態碼 404
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long id) {
        log.info("收到 API 請求：刪除 ID [{}] 的筆記", id);
        boolean isDeleted = noteService.deleteNote(id);

        if (isDeleted) {
            // 刪除成功，回傳 HTTP 狀態碼 204
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
