package com.ead.course.controllers;

import com.ead.course.dtos.CourseDTO;
import com.ead.course.dtos.ModuleDTO;
import com.ead.course.services.interfaces.ModuleService;
import com.ead.course.specifications.SpecificationTemplate;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/courses/{courseId}/modules")
public class ModulesController {
    @Autowired
    private ModuleService service;

    @GetMapping
    public ResponseEntity<Page<ModuleDTO>> findAll(
        @PathVariable(value = "courseId") UUID courseId,
        SpecificationTemplate.ModuleSpec filtersSpec,
        @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(service.findAllIntoCourse(courseId, filtersSpec, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(
        @PathVariable(value = "courseId") UUID courseId,
        @PathVariable(value = "id") UUID id
    ) {
        Optional<ModuleDTO> moduleDTOOpt = service.findByIdIntoCourse(id, courseId);
        return moduleDTOOpt
            .map(moduleDTO ->
                ResponseEntity
                    .status(HttpStatus.OK)
                    .body(moduleDTO)
            )
            .orElse(
                ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .build()
            );
    }

    @PostMapping
    public ResponseEntity<?> create(
        @PathVariable(value = "courseId") UUID courseId,
        @RequestBody @Validated(ModuleDTO.Create.class) @JsonView(ModuleDTO.Create.class) ModuleDTO moduleDTO,
        UriComponentsBuilder uriComponentsBuilder
    ) {
        CourseDTO courseDTO = new CourseDTO();
        courseDTO.setId(courseId);
        moduleDTO.setCourse(courseDTO);
        if (service.valid(moduleDTO)) {
            UUID id = service.create(moduleDTO);
            UriComponents uriComponents = uriComponentsBuilder.path("/courses/{courseId}/modules/{id}")
                .buildAndExpand(courseId, id);
            return ResponseEntity
                .created(uriComponents.toUri())
                .build();
        } else {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(moduleDTO.getErrors());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
        @PathVariable(value = "courseId") UUID courseId,
        @PathVariable(value = "id") UUID id,
        @RequestBody @Validated(ModuleDTO.Update.class) @JsonView(ModuleDTO.Update.class) ModuleDTO moduleDTO
    ) {
        Optional<ModuleDTO> updatedModuleDTOOpt = service.findByIdIntoCourse(id, courseId);
        if (updatedModuleDTOOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ModuleDTO updatedModuleDTO = updatedModuleDTOOpt.get();
        CourseDTO courseDTO = new CourseDTO();
        courseDTO.setId(courseId);
        updatedModuleDTO.setCourse(courseDTO);
        service.merge(moduleDTO, updatedModuleDTO, ModuleDTO.Update.class);
        if (service.valid(updatedModuleDTO)) {
            service.update(updatedModuleDTO);
            return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
        } else {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(updatedModuleDTO.getErrors());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
        @PathVariable(value = "courseId") UUID courseId,
        @PathVariable(value = "id") UUID id
    ) {
        if (service.findByIdIntoCourse(id, courseId).isEmpty()) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .build();
        }

        service.deleteById(id);
        return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .build();
    }
}
