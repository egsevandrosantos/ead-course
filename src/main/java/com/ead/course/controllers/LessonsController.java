package com.ead.course.controllers;

import com.ead.course.dtos.LessonDTO;
import com.ead.course.services.ServiceResponse;
import com.ead.course.services.interfaces.LessonService;
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
@RequestMapping("/modules/{moduleId}/lessons")
public class LessonsController {
    @Autowired
    private LessonService service;

    @GetMapping
    public ResponseEntity<Page<LessonDTO>> findAll(
        @PathVariable(value = "moduleId") UUID moduleId,
        SpecificationTemplate.LessonSpec filtersSpec,
        @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(service.findAllIntoModule(moduleId, filtersSpec, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(
            @PathVariable(value = "moduleId") UUID moduleId,
            @PathVariable(value = "id") UUID id
    ) {
        Optional<LessonDTO> lessonDTOOptional = service.findByIdIntoModule(id, moduleId);
        return lessonDTOOptional
            .map(lessonDTO ->
                ResponseEntity
                    .status(HttpStatus.OK)
                    .body(lessonDTO)
            )
            .orElse(
                ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .build()
            );
    }

    @PostMapping
    public ResponseEntity<?> create(
        @PathVariable(value = "moduleId") UUID moduleId,
        @RequestBody @Validated(LessonDTO.Create.class) @JsonView(LessonDTO.Create.class) LessonDTO lessonDTO,
        UriComponentsBuilder uriComponentsBuilder
    ) {
        ServiceResponse serviceResponse = service.create(moduleId, lessonDTO);

        if (serviceResponse.isOk()) {
            UriComponents uriComponents = uriComponentsBuilder.path("/modules/{moduleId}/lessons/{id}")
                .buildAndExpand(moduleId, serviceResponse.getId());
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
        @PathVariable(value = "moduleId") UUID moduleId,
        @PathVariable(value = "id") UUID id,
        @RequestBody @Validated(LessonDTO.Update.class) @JsonView(LessonDTO.Update.class) LessonDTO lessonDTO
    ) {
        ServiceResponse serviceResponse = service.update(moduleId, id, lessonDTO);

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

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(
        @PathVariable(value = "moduleId") UUID moduleId,
        @PathVariable(value = "id") UUID id
    ) {
        try {
            service.deleteById(id);
            return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .build();
        }
    }
}
