package com.ead.course.controllers;

import com.ead.course.dtos.ModuleDTO;
import com.ead.course.services.ServiceResponse;
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
        ServiceResponse serviceResponse = service.create(courseId, moduleDTO);

        if (serviceResponse.isOk()) {
            UriComponents uriComponents = uriComponentsBuilder.path("/courses/{courseId}/modules/{id}")
                .buildAndExpand(courseId, serviceResponse.getId());
            return ResponseEntity
                .created(uriComponents.toUri())
                .build();
        } else {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(serviceResponse.getErrors());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
        @PathVariable(value = "courseId") UUID courseId,
        @PathVariable(value = "id") UUID id,
        @RequestBody @Validated(ModuleDTO.Update.class) @JsonView(ModuleDTO.Update.class) ModuleDTO moduleDTO
    ) {
        ServiceResponse serviceResponse = service.update(id, courseId, moduleDTO);

        if (!serviceResponse.isFound()) {
            return ResponseEntity.notFound().build();
        }

        if (serviceResponse.isOk()) {
            return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
        } else {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(serviceResponse.getErrors());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
        @PathVariable(value = "courseId") UUID courseId,
        @PathVariable(value = "id") UUID id
    ) {
        ServiceResponse serviceResponse = service.deleteById(id);

        if (serviceResponse.isOk()) {
            return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
        } else {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .build();
        }
    }
}
