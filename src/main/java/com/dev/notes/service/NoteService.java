package com.dev.notes.service;

import com.dev.notes.dto.NoteDTO;

import java.util.List;

public interface NoteService {

    List<NoteDTO> getAllNotes();

    // 根據環境（UT, UAT, PROD）篩選筆記
    List<NoteDTO> getAllNotesType(String type);

    // 傳入前端給的 DTO，回傳新增成功後的 DTO
    NoteDTO createNote(NoteDTO noteDTO);

    // 傳入要修改的 ID 與新的資料，回傳修改後的 DTO
    NoteDTO updateNote(Long id, NoteDTO noteDTO);

    // true 刪除成功，false 找不到該筆記
    boolean deleteNote(Long id);

}
