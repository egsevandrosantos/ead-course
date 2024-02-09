package com.ead.course.services.interfaces;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.ead.course.dtos.UserDTO;
import com.ead.course.models.User;

public interface CourseUserService {
    Page<UserDTO> findAll(Specification<User> filtersSpec, Pageable pageable, UUID courseId);
}
