package com.ead.course.services.interfaces;

import com.ead.course.dtos.ModuleDTO;
import com.ead.course.models.Module;
import com.ead.course.specifications.SpecificationTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;

public interface ModuleService {
    UUID create(ModuleDTO moduleDTO);
    void update(ModuleDTO updatedModuleDTO);
    void deleteById(UUID id) throws IllegalArgumentException;
    void merge(ModuleDTO source, ModuleDTO dest);
    void merge(ModuleDTO source, ModuleDTO dest, Class<? extends ModuleDTO.ModuleView> view);
    boolean valid(ModuleDTO updatedModuleDTO);
    boolean valid(ModuleDTO updatedModuleDTO, ModuleDTO internalModuleDTO);
    Page<ModuleDTO> findAllIntoCourse(UUID courseId, Specification<Module> filtersSpec, Pageable pageable);
    Optional<ModuleDTO> findByIdIntoCourse(UUID id, UUID courseId);
}
