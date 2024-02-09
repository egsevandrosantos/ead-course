package com.ead.course.services;

import com.ead.course.dtos.CourseDTO;
import com.ead.course.models.Course;
import com.ead.course.repositories.CourseRepository;
import com.ead.course.repositories.LessonRepository;
import com.ead.course.repositories.ModuleRepository;
import com.ead.course.services.interfaces.CourseService;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.lang.reflect.Field;
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
    public Page<CourseDTO> findAll(Specification<Course> filtersSpec, Pageable pageable, UUID userId) {
        if (userId != null) {
            filtersSpec = ((Specification<Course>) (root, query, criteriaBuilder) -> {
                // root is Course
                return criteriaBuilder.and(criteriaBuilder.equal(root.get("users").get("id"), userId)); // TODO: WORK?
            }).and(filtersSpec);
        }
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
    public ServiceResponse create(CourseDTO courseDTO) {
        Course course = new Course();
        BeanUtils.copyProperties(courseDTO, course);
        Map<String, List<String>> errors = valid(course);
        if (!errors.isEmpty()) {
            return ServiceResponse.builder()
                .ok(false)
                .errors(errors)
                .build();
        }

        course = repository.save(course);
        return ServiceResponse.builder().id(course.getId()).build();
    }

    @Override
    @Transactional // Evitar a consulta SELECT antes do UPDATE
    public ServiceResponse update(UUID id, CourseDTO courseDTO) {
        Optional<Course> courseOpt = repository.findById(id);
        if (courseOpt.isEmpty()) {
            return ServiceResponse.builder()
                .ok(false)
                .found(false)
                .build();
        }

        Course course = courseOpt.get();
        merge(courseDTO, course, CourseDTO.Update.class);
        Map<String, List<String>> errors = valid(course);
        if (!errors.isEmpty()) {
            return ServiceResponse.builder()
                .ok(false)
                .errors(errors)
                .build();
        }

        repository.save(course);
        return ServiceResponse.builder().build();
    }

    @Transactional
    @Override
    public ServiceResponse deleteById(UUID id) throws IllegalArgumentException {
        if (id == null) {
            return ServiceResponse.builder()
                .ok(false)
                .build();
        }

        if (repository.existsById(id)) {
            lessonRepository.deleteAllByModuleCourseId(id);
            moduleRepository.deleteAllByCourseId(id);
            repository.deleteById(id);
        }
        return ServiceResponse.builder().build();
    }

    public void merge(CourseDTO source, CourseDTO dest) {
        BeanUtils.copyProperties(source, dest);
    }

    public void merge(CourseDTO source, Course dest, Class<? extends CourseDTO.CourseView> view) {
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

    public Map<String, List<String>> valid(Course updatedCourse) {
        Map<String, List<String>> errors = new HashMap<>();
        // TODO: Validate user exists and is instructor
        return errors;
    }
}
