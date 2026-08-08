package com.dev.notes.controller;

import com.dev.notes.dto.NoteDTO;
import com.dev.notes.service.NoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@Tag(name = "筆記管理 API", description = "提供筆記的增刪修改功能") // 幫整個 Controller 命名
public class NoteController {

    @Autowired
    private NoteService noteService;

    @Operation(summary = "取得所有筆記", description = "回傳資料庫內所有筆記的陣列")
    @GetMapping
    public ResponseEntity<List<NoteDTO>> getAllNotes() {
        log.info("收到 API 請求：取的所有筆記");
        List<NoteDTO> notes = noteService.getAllNotes();
        // 回傳 HTTP 狀態碼 200 與資料
        return ResponseEntity.ok(notes);
    }

    @Operation(summary = "根據分類取得筆記", description = "傳入自訂分類，回傳符合該分類筆記的陣列")
    @GetMapping("/type/{type}")
    public ResponseEntity<List<NoteDTO>> getNotesByType(@PathVariable String type) {
        log.info("收到 API 請求：取得分類為 [{}] 的筆記", type);
        List<NoteDTO> notes = noteService.getNotesByType(type);
        return ResponseEntity.ok(notes);
    }

    // 新增筆記
    @Operation(summary = "新增筆記", description = "傳入帶有標題與內容的 JSON，成功後回傳帶有 ID 的筆記資料")
    @PostMapping
    public ResponseEntity<NoteDTO> createNote(@Valid @RequestBody NoteDTO noteDTO) {
        log.info("收到 API 請求：新增筆記");
        NoteDTO createdNote = noteService.createNote(noteDTO);
        // 回傳 HTTP 狀態碼 201
        return ResponseEntity.status(HttpStatus.CREATED).body(createdNote);
    }

    // 更新筆記
    @Operation(summary = "更新筆記", description = "傳入欲修改的筆記 ID 與新的 JSON 資料，覆寫原有筆記")
    @PutMapping("/{id}")
    public ResponseEntity<NoteDTO> updateNote(@PathVariable Long id,@Valid @RequestBody NoteDTO noteDTO) {
        log.info("收到 API 請求：更新 ID [{}] 的筆記", id);
        NoteDTO updatedNote = noteService.updateNote(id, noteDTO);

        if (updatedNote != null) {
            return ResponseEntity.ok(updatedNote);
        } else {
            // 如果找不到該筆記，回傳 HTTP 狀態碼 404
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "刪除筆記", description = "傳入欲刪除的筆記 ID，成功刪除不會回傳任何內容 (HTTP 204)")
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
