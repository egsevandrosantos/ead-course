package com.ead.course.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ead.course.dtos.UserDTO;
import com.ead.course.services.interfaces.CourseUserService;
import com.ead.course.specifications.SpecificationTemplate;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;


@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/courses/{courseId}/users")
public class CoursesUsersController {
    @Autowired
    private CourseUserService service;

    @GetMapping
    public ResponseEntity<Page<UserDTO>> findAll(
        SpecificationTemplate.UserSpec filtersSpec,
        @PathVariable(value = "courseId") UUID courseId,
        @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(service.findAll(filtersSpec, pageable, courseId));
    }
}
