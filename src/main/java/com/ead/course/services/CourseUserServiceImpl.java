package com.ead.course.services;

import com.ead.course.repositories.CourseUserRepository;
import com.ead.course.services.interfaces.CourseUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CourseUserServiceImpl implements CourseUserService {
    @Autowired
    private CourseUserRepository repository;
}
