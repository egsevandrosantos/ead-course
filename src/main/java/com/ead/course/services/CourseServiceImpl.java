package com.ead.course.services;

import com.ead.course.dtos.CourseDTO;
import com.ead.course.models.Course;
import com.ead.course.models.Lesson;
import com.ead.course.models.Module;
import com.ead.course.repositories.CourseRepository;
import com.ead.course.repositories.LessonRepository;
import com.ead.course.repositories.ModuleRepository;
import com.ead.course.services.interfaces.CourseService;
import com.ead.course.specifications.SpecificationTemplate;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.lang.reflect.Field;
import java.time.Instant;
import java.util.*;

@Service
public class CourseServiceImpl implements CourseService {
    @Autowired
    private CourseRepository repository;
    @Autowired
    private ModuleRepository moduleRepository;
    @Autowired
    private LessonRepository lessonRepository;

    @Override
    public Page<CourseDTO> findAll(SpecificationTemplate.CourseSpec filtersSpec, Pageable pageable) {
        Page<Course> coursesPage = repository.findAll(filtersSpec, pageable);

        List<Course> courses = coursesPage.getContent();
        List<CourseDTO> coursesDTO = new ArrayList<>();
        for (Course course : courses) {
            CourseDTO courseDTO = new CourseDTO();
            BeanUtils.copyProperties(course, courseDTO);
            coursesDTO.add(courseDTO);
        }

        return new PageImpl<>(coursesDTO, coursesPage.getPageable(), coursesPage.getTotalElements());
    }

    @Override
    public Optional<CourseDTO> findById(UUID id) {
        if (id == null) {
            return Optional.empty();
        }
        Optional<Course> courseOpt = repository.findById(id);
        return courseOpt
            .map(course -> {
                CourseDTO courseDTO = new CourseDTO();
                BeanUtils.copyProperties(course, courseDTO);
                return Optional.of(courseDTO);
            })
            .orElse(Optional.empty());
    }

    @Override
    public UUID create(CourseDTO courseDTO) {
        Course course = new Course();
        BeanUtils.copyProperties(courseDTO, course);
        course.setCreatedAt(Instant.now());
        course.setUpdatedAt(Instant.now());
        course = repository.save(course);
        return course.getId();
    }

    @Override
    public void update(CourseDTO updatedCourseDTO) {
        Course course = new Course();
        BeanUtils.copyProperties(updatedCourseDTO, course);
        course.setUpdatedAt(Instant.now());
        repository.save(course);
    }

    @Transactional
    @Override
    public void deleteById(UUID id) {
        if (id == null) {
            return;
        }
        List<Module> modules = moduleRepository.findAllIntoCourse(id);
        for (Module module : modules) {
            List<Lesson> lessons = lessonRepository.findAllIntoModule(module.getId());
            lessonRepository.deleteAll(lessons);
        }
        moduleRepository.deleteAll(modules);
        repository.deleteById(id);
    }

    @Override
    public void merge(CourseDTO source, CourseDTO dest) {
        BeanUtils.copyProperties(source, dest);
    }

    @Override
    public void merge(CourseDTO source, CourseDTO dest, Class<? extends CourseDTO.CourseView> view) {
        String[] fieldsNotInViewToIgnore = Arrays.stream(CourseDTO.class.getDeclaredFields())
            .filter(field -> {
                JsonView jsonView = field.getAnnotation(JsonView.class);
                if (jsonView == null) {
                    return true;
                }
                return Arrays.stream(jsonView.value()).noneMatch(value -> value.getName().equals(view.getName()));
            })
            .map(Field::getName)
            .toArray(String[]::new);
        BeanUtils.copyProperties(source, dest, fieldsNotInViewToIgnore);
    }

    @Override
    public boolean valid(CourseDTO courseDTO) {
        return valid(courseDTO, null);
    }

    @Override
    public boolean valid(CourseDTO courseDTO, CourseDTO internalCourseDTO) {
        return true;
    }
}
