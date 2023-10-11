package com.ead.course.services.interfaces;

import com.ead.course.dtos.ModuleDTO;
import com.ead.course.models.Module;
import com.ead.course.services.ServiceResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;

public interface ModuleService {
    ServiceResponse create(UUID courseId, ModuleDTO moduleDTO);
    ServiceResponse update(UUID id, UUID courseId, ModuleDTO moduleDTO);
    ServiceResponse deleteById(UUID id) throws IllegalArgumentException;
    Page<ModuleDTO> findAllIntoCourse(UUID courseId, Specification<Module> filtersSpec, Pageable pageable);
    Optional<ModuleDTO> findByIdIntoCourse(UUID id, UUID courseId);
}
