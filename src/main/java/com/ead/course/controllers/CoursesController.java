package com.ead.course.controllers;

import com.ead.course.dtos.CourseDTO;
import com.ead.course.services.ServiceResponse;
import com.ead.course.services.interfaces.CourseService;
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
@RequestMapping("/courses")
public class CoursesController {
    @Autowired
    private CourseService service;

    @GetMapping
    public ResponseEntity<Page<CourseDTO>> findAll(
        SpecificationTemplate.CourseSpec filtersSpec,
        @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable,
        @RequestParam(required = false) UUID userId
    ) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(service.findAll(filtersSpec, pageable, userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable(value = "id") UUID id) {
        Optional<CourseDTO> courseOpt = service.findById(id);
        return courseOpt
            .map(course ->
                ResponseEntity
                    .status(HttpStatus.OK)
                    .body(course)
            ).orElseGet(() ->
                ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .build()
            );
    }

    @PostMapping
    public ResponseEntity<?> create(
        @RequestBody @Validated(CourseDTO.Create.class) @JsonView(CourseDTO.Create.class) CourseDTO courseDTO,
        UriComponentsBuilder uriComponentsBuilder
    ) {
        ServiceResponse serviceResponse = service.create(courseDTO);
        if (serviceResponse.isOk()) {
            UriComponents uriComponents = uriComponentsBuilder.path("/courses/{id}").buildAndExpand(serviceResponse.getId());
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
        @PathVariable(value = "id") UUID id,
        @RequestBody @Validated(CourseDTO.Update.class) @JsonView(CourseDTO.Update.class) CourseDTO courseDTO
    ) {
        ServiceResponse serviceResponse = service.update(id, courseDTO);
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
    public ResponseEntity<Void> delete(@PathVariable(value = "id") UUID id) {
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
