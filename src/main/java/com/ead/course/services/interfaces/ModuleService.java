package com.ead.course.services.interfaces;

import com.ead.course.dtos.ModuleDTO;

import java.util.*;

public interface ModuleService {
    UUID create(ModuleDTO moduleDTO);
    void update(ModuleDTO updatedModuleDTO);
    void deleteById(UUID id);
    void merge(ModuleDTO source, ModuleDTO dest);
    void merge(ModuleDTO source, ModuleDTO dest, Class<? extends ModuleDTO.ModuleView> view);
    boolean valid(ModuleDTO updatedModuleDTO);
    boolean valid(ModuleDTO updatedModuleDTO, ModuleDTO internalModuleDTO);
    List<ModuleDTO> findAllIntoCourse(UUID courseId);
    Optional<ModuleDTO> findByIdIntoCourse(UUID id, UUID courseId);
}
