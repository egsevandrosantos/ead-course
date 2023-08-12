package com.ead.course.services.interfaces;

import com.ead.course.dtos.LessonDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LessonService {
    UUID create(LessonDTO lessonDTO);
    void update(LessonDTO updatedLessonDTO);
    void deleteById(UUID id);
    void merge(LessonDTO source, LessonDTO dest);
    void merge(LessonDTO source, LessonDTO dest, Class<? extends LessonDTO.LessonView> view);
    boolean valid(LessonDTO updatedLessonDTO);
    boolean valid(LessonDTO updatedLessonDTO, LessonDTO internalLessonDTO);
    boolean existsByIdIntoModule(UUID id, UUID moduleId);
    Optional<LessonDTO> findByIdIntoModule(UUID id, UUID moduleId);
    List<LessonDTO> findAllIntoModule(UUID moduleId);
}
