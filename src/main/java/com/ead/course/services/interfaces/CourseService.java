package com.ead.course.services.interfaces;

import com.ead.course.dtos.CourseDTO;
import com.ead.course.models.Course;
import com.ead.course.specifications.SpecificationTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CourseService {
    Page<CourseDTO> findAll(Specification<Course> filtersSpec, Pageable pageable, UUID userId);
    Optional<CourseDTO> findById(UUID id);
    UUID create(CourseDTO courseDTO);
    void update(CourseDTO updatedCourseDTO);
    void deleteById(UUID id) throws IllegalArgumentException;
    void merge(CourseDTO source, CourseDTO dest);
    void merge(CourseDTO source, CourseDTO dest, Class<? extends CourseDTO.CourseView> view);
    boolean valid(CourseDTO courseDTO);
    boolean valid(CourseDTO courseDTO, CourseDTO internalCourseDTO);
}
