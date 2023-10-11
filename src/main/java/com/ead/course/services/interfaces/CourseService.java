package com.ead.course.services.interfaces;

import com.ead.course.dtos.CourseDTO;
import com.ead.course.models.Course;
import com.ead.course.services.ServiceResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;
import java.util.UUID;

public interface CourseService {
    Page<CourseDTO> findAll(Specification<Course> filtersSpec, Pageable pageable, UUID userId);
    Optional<CourseDTO> findById(UUID id);
    ServiceResponse create(CourseDTO courseDTO);
    ServiceResponse update(UUID id, CourseDTO courseDTO);
    ServiceResponse deleteById(UUID id) throws IllegalArgumentException;
}
