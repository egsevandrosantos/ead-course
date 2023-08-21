package com.ead.course.controllers;

import com.ead.course.dtos.CourseDTO;
import com.ead.course.dtos.CourseUserDTO;
import com.ead.course.dtos.UserDTO;
import com.ead.course.services.interfaces.CourseService;
import com.ead.course.services.interfaces.CourseUserService;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.extern.log4j.Log4j2;
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
@RequestMapping("/courses/{courseId}/users")
@Log4j2
public class CoursesUsersController {
    @Autowired
    private CourseUserService service;
    @Autowired
    private CourseService courseService;

    @GetMapping
    public ResponseEntity<Page<UserDTO>> findAll(
        @PathVariable(value = "courseId") UUID courseId,
        @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(service.findAll(pageable, courseId));
    }

    @PostMapping
    public ResponseEntity<?> create(
        @PathVariable(value = "courseId") UUID courseId,
        @RequestBody @Validated(CourseUserDTO.Create.class) @JsonView(CourseUserDTO.Create.class) CourseUserDTO courseUserDTO,
        UriComponentsBuilder uriComponentsBuilder
    ) {
        Optional<CourseDTO> courseDTOOptional = courseService.findById(courseId);
        if (courseDTOOptional.isEmpty()) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .build();
        }

        CourseDTO courseDTO = courseDTOOptional.get();
        courseUserDTO.setCourseDTO(courseDTO);
        if (service.valid(courseUserDTO)) {
            service.create(courseUserDTO);
            UriComponents uriComponents = uriComponentsBuilder
                .path("/courses/{courseId}/users/{userId}")
                .buildAndExpand(courseId, courseUserDTO.getUserId());
            return ResponseEntity
                .created(uriComponents.toUri())
                .build();
        } else {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(courseUserDTO.getErrors());
        }
    }
}
