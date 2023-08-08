package com.ead.course.services;

import com.ead.course.models.Course;
import com.ead.course.models.Lesson;
import com.ead.course.models.Module;
import com.ead.course.repositories.CourseRepository;
import com.ead.course.repositories.LessonRepository;
import com.ead.course.repositories.ModuleRepository;
import com.ead.course.services.interfaces.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Set;

@Service
public class CourseServiceImpl implements CourseService {
    @Autowired
    private CourseRepository repository;
    @Autowired
    private ModuleRepository moduleRepository;
    @Autowired
    private LessonRepository lessonRepository;

    @Transactional
    @Override
    public void delete(Course course) {
        Set<Module> modules = moduleRepository.findAllModulesIntoCourse(course.getId());
        for (Module module : modules) {
            Set<Lesson> lessons = lessonRepository.findAllLessonsIntoModule(module.getId());
            lessonRepository.deleteAll(lessons);
        }
        moduleRepository.deleteAll(modules);
        repository.delete(course);
    }
}
