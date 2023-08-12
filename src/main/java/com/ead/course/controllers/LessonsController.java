package com.ead.course.controllers;

import com.ead.course.dtos.LessonDTO;
import com.ead.course.dtos.ModuleDTO;
import com.ead.course.services.interfaces.LessonService;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/modules/{moduleId}/lessons")
public class LessonsController {
    @Autowired
    private LessonService service;

    @GetMapping
    public ResponseEntity<List<LessonDTO>> findAll(@PathVariable(value = "moduleId") UUID moduleId) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(service.findAllIntoModule(moduleId));
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
        ModuleDTO moduleDTO = new ModuleDTO();
        moduleDTO.setId(moduleId);
        lessonDTO.setModule(moduleDTO);
        if (service.valid(lessonDTO)) {
            UUID id = service.create(lessonDTO);
            UriComponents uriComponents = uriComponentsBuilder.path("/modules/{moduleId}/lessons/{id}")
                .buildAndExpand(moduleId, id);
            return ResponseEntity
                .created(uriComponents.toUri())
                .build();
        } else {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(lessonDTO.getErrors());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
        @PathVariable(value = "moduleId") UUID moduleId,
        @PathVariable(value = "id") UUID id,
        @RequestBody @Validated(LessonDTO.Update.class) @JsonView(LessonDTO.Update.class) LessonDTO lessonDTO
    ) {
        Optional<LessonDTO> updatedLessonDTOOptional = service.findByIdIntoModule(id, moduleId);
        if (updatedLessonDTOOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        LessonDTO updatedLessonDTO = updatedLessonDTOOptional.get();
        ModuleDTO moduleDTO = new ModuleDTO();
        moduleDTO.setId(moduleId);
        updatedLessonDTO.setModule(moduleDTO);
        service.merge(lessonDTO, updatedLessonDTO, LessonDTO.Update.class);
        if (service.valid(updatedLessonDTO)) {
            service.update(updatedLessonDTO);
            return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
        } else {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(updatedLessonDTO.getErrors());
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(
        @PathVariable(value = "moduleId") UUID moduleId,
        @PathVariable(value = "id") UUID id
    ) {
        if (service.findByIdIntoModule(id, moduleId).isEmpty()) {
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
