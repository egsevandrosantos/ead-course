package com.ead.course.controllers;

import com.ead.course.dtos.UserDTO;
import com.ead.course.services.interfaces.CourseUserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/courses/{courseId}/users")
@Log4j2
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
}
