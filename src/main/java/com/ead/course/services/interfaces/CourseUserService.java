package com.ead.course.services.interfaces;

import com.ead.course.dtos.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CourseUserService {
    Page<UserDTO> findAll(Pageable pageable, UUID courseId);
}
