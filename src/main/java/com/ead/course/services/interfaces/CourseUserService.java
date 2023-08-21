package com.ead.course.services.interfaces;

import com.ead.course.dtos.CourseUserDTO;
import com.ead.course.dtos.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.net.UnknownHostException;
import java.util.UUID;

public interface CourseUserService {
    Page<UserDTO> findAll(Pageable pageable, UUID courseId);
    UUID create(CourseUserDTO courseUserDTO);
    boolean valid(CourseUserDTO courseUserDTO);
    void merge(CourseUserDTO source, CourseUserDTO dest);
    void merge(CourseUserDTO source, CourseUserDTO dest, Class<? extends CourseUserDTO.CourseUserView> view);
}
