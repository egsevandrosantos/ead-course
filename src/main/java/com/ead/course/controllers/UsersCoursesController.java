package com.ead.course.controllers;

import java.util.Base64;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ead.course.services.ServiceResponse;
import com.ead.course.services.interfaces.CourseUserService;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/users/{userId}/courses")
@Deprecated
public class UsersCoursesController {
    @Value("${ead.api.url.authuser}")
    private String authUserURI;

    @Autowired
    private CourseUserService service;

    @DeleteMapping
    public ResponseEntity<?> delete(
        @PathVariable(value = "userId") UUID userId,
        HttpServletRequest request
    ) {
        String requestedBy = request.getHeader("Requested-By");
        if (requestedBy == null || requestedBy.isBlank() || !new String(Base64.getDecoder().decode(requestedBy)).equals(authUserURI)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        ServiceResponse serviceResponse = service.deleteByUserId(userId);
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
