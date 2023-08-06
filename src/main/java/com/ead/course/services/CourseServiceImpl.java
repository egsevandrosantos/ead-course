package com.ead.course.services;

import com.ead.course.repositories.CourseRepository;
import com.ead.course.services.interfaces.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CourseServiceImpl implements CourseService {
    @Autowired
    private CourseRepository repository;
}
