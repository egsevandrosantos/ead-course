package com.ead.course.services;

import com.ead.course.clients.CourseUserClient;
import com.ead.course.dtos.UserDTO;
import com.ead.course.repositories.CourseUserRepository;
import com.ead.course.services.interfaces.CourseUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CourseUserServiceImpl implements CourseUserService {
    @Autowired
    private CourseUserRepository repository;
    @Autowired
    private CourseUserClient client;

    @Override
    public Page<UserDTO> findAll(Pageable pageable, UUID courseId) {
        return client.findAll(pageable, courseId);
    }
}
