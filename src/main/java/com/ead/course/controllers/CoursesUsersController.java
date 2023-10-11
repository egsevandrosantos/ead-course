package com.ead.course.controllers;

import com.ead.course.dtos.CourseUserDTO;
import com.ead.course.dtos.UserDTO;
import com.ead.course.services.ServiceResponse;
import com.ead.course.services.interfaces.CourseUserService;
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

import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/courses/{courseId}/users")
public class CoursesUsersController {
    @Autowired
    private CourseUserService service;

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
        ServiceResponse serviceResponse = service.create(courseId, courseUserDTO);
        if (!serviceResponse.isFound()) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .build();
        }

        if (serviceResponse.isOk()) {
            UriComponents uriComponents = uriComponentsBuilder
                .path("/courses/{courseId}/users/{userId}")
                .buildAndExpand(courseId, courseUserDTO.getUserId());
            return ResponseEntity
                .created(uriComponents.toUri())
                .build();
        } else {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(serviceResponse.getErrors());
        }
    }
}
