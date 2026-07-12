package com.dev.notes.service.implement;

import com.dev.notes.dto.NoteDTO;
import com.dev.notes.entity.NoteEntity;
import com.dev.notes.repository.NoteRepository;
import com.dev.notes.service.NoteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NoteServiceImpl implements NoteService {

    @Autowired
    private NoteRepository noteRepository;

    @Override
    public List<NoteDTO> getAllNotes() {
        log.info("執行查詢：獲取所有筆記");
        // 從 DB 撈出所有 Entity 的 List
        List<NoteEntity> notes = noteRepository.findAll();

        // 使用 Stream API 將 List<Note> 轉換成 List<NoteDTO>
        return notes.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList()
                    );
    }

    @Override
    public List<NoteDTO> getNotesByType(String type) {
        log.info("執行查詢：根據類別 [{}] 篩選筆記", type);

        return noteRepository.findByType(type).stream()
                             .map(this::convertToDTO)
                             .collect(Collectors.toList()
                             );
    }

    @Override
    public NoteDTO createNote(NoteDTO noteDTO) {
        log.info("執行查詢：準備建立標題為 [{}] 的筆記", noteDTO.getTitle());

        // 將前端傳來的 DTO 轉換成 Entity
        NoteEntity note = convertToEntity(noteDTO);
        // 交給 Repository 存入資料庫，並取得帶有自動生成 ID 與時間的 Entity
        NoteEntity savedNote = noteRepository.save(note);
        // 把存好的 Entity 轉回 DTO 交還給前端
        return convertToDTO(savedNote);
    }

    @Override
    public NoteDTO updateNote(Long id, NoteDTO noteDTO) {
        // 使用 Optional 來處理「可能找不到資料」的情況，這是防呆的好習慣
        Optional<NoteEntity> noteOptional = noteRepository.findById(id);

        if (noteOptional.isPresent()) {
            NoteEntity existingNote = noteOptional.get();
            // 只更新允許被修改的欄位 (這裡故意不更新 id 與 createdAt)
            existingNote.setTitle(noteDTO.getTitle());
            existingNote.setType(noteDTO.getType());
            existingNote.setContent(noteDTO.getContent());

            NoteEntity updatedNote = noteRepository.save(existingNote);
            log.info("更新成功：ID [{}]", id);
            return convertToDTO(updatedNote);
        } else {
            log.warn("更新失敗：找不到 ID [{}] 的筆記", id);
            return null;
        }
    }

    @Override
    public boolean deleteNote(Long id) {
        if (noteRepository.existsById(id)) {
            noteRepository.deleteById(id);
            log.info("執行刪除：成功刪除 ID [{}] 的筆記", id);
            return true;
        } else {
            log.warn("刪除失敗：找不到 ID [{}]", id);
            return false;
        }
    }

    // --------------------------------------------------
    // 負責 Entity 與 DTO 之間的互相轉換
    private NoteDTO convertToDTO(NoteEntity note) {
        NoteDTO noteDTO = new NoteDTO();

        noteDTO.setId(note.getId());
        noteDTO.setTitle(note.getTitle());
        noteDTO.setType(note.getType());
        noteDTO.setContent(note.getContent());
        noteDTO.setCreatedAt(note.getCreatedAt());
        noteDTO.setUpdatedAt(note.getUpdatedAt());

        return noteDTO;
    }

    private NoteEntity convertToEntity(NoteDTO noteDTO) {
        NoteEntity note = new NoteEntity();
        // 新增時 ID 通常是 null，由資料庫自動產生
        note.setId(noteDTO.getId());
        note.setTitle(noteDTO.getTitle());
        note.setType(noteDTO.getType());
        note.setContent(noteDTO.getContent());

        return note;
    }
}
