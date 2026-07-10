package com.dev.notes.repository;

import com.dev.notes.entity.NoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteRepository extends JpaRepository<NoteEntity, Long> {
    List<NoteEntity> findByType(String type);
}
