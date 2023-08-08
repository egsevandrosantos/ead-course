package com.ead.course.services.interfaces;

import com.ead.course.dtos.CourseDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CourseService {
    List<CourseDTO> findAll();
    Optional<CourseDTO> findById(UUID id);
    UUID create(CourseDTO courseDTO);
    void update(CourseDTO updatedCourseDTO);
    void deleteById(UUID id);
    void merge(CourseDTO source, CourseDTO dest);
    void merge(CourseDTO source, CourseDTO dest, Class<? extends CourseDTO.CourseView> view);
    boolean valid(CourseDTO courseDTO);
    boolean valid(CourseDTO courseDTO, CourseDTO internalCourseDTO);
}
