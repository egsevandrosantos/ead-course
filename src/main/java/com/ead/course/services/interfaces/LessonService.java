package com.ead.course.services.interfaces;

import com.ead.course.dtos.LessonDTO;
import com.ead.course.models.Lesson;
import com.ead.course.services.ServiceResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;
import java.util.UUID;

public interface LessonService {
    ServiceResponse create(UUID moduleId, LessonDTO lessonDTO);
    ServiceResponse update(UUID moduleId, UUID id, LessonDTO updatedLessonDTO);
    ServiceResponse deleteById(UUID id) throws IllegalArgumentException;
    boolean existsByIdIntoModule(UUID id, UUID moduleId);
    Optional<LessonDTO> findByIdIntoModule(UUID id, UUID moduleId);
    Page<LessonDTO> findAllIntoModule(UUID moduleId, Specification<Lesson> filtersSpec, Pageable pageable);
}
