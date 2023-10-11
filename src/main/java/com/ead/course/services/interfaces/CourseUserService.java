package com.ead.course.services.interfaces;

import com.ead.course.dtos.CourseUserDTO;
import com.ead.course.dtos.UserDTO;
import com.ead.course.services.ServiceResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CourseUserService {
    Page<UserDTO> findAll(Pageable pageable, UUID courseId);
    ServiceResponse create(UUID courseId, CourseUserDTO courseUserDTO);
}
