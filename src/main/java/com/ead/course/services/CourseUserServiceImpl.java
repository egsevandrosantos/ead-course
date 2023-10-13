package com.ead.course.services;

import com.ead.course.clients.AuthUserClient;
import com.ead.course.dtos.CourseUserDTO;
import com.ead.course.dtos.UserDTO;
import com.ead.course.exceptions.UserBlockedException;
import com.ead.course.models.Course;
import com.ead.course.models.CourseUser;
import com.ead.course.repositories.CourseRepository;
import com.ead.course.repositories.CourseUserRepository;
import com.ead.course.services.interfaces.CourseUserService;
import com.fasterxml.jackson.annotation.JsonView;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class CourseUserServiceImpl implements CourseUserService {
    @Autowired
    private CourseUserRepository repository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private AuthUserClient authUserClient;

    @Override
    public Page<UserDTO> findAll(Pageable pageable, UUID courseId) {
        return authUserClient.findAllUsers(pageable, courseId);
    }

    @Transactional
    @Override
    public ServiceResponse create(UUID courseId, CourseUserDTO courseUserDTO) {
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        if (courseOpt.isEmpty()) {
            return ServiceResponse.builder()
                .ok(false)
                .found(false)
                .build();
        }

        CourseUser courseUser = new CourseUser();
        courseUser.setCourse(courseOpt.get());
        merge(courseUserDTO, courseUser);
        Map<String, List<String>> errors = valid(courseUser);
        if (!errors.isEmpty()) {
            return ServiceResponse.builder()
                .ok(false)
                .found(true)
                .errors(errors)
                .build();
        }

        courseUser = repository.save(courseUser);
        authUserClient.createUserCourseRelationship(courseUser.getCourse().getId(), courseUser.getUserId());
        return ServiceResponse.builder().id(courseUser.getId()).build();
    }

    @Override
    @Transactional
    public ServiceResponse deleteByUserId(UUID userId) {
        if (userId == null) {
            return ServiceResponse.builder()
                .ok(false)
                .found(false)
                .build();
        }

        if (repository.existsByUserId(userId)) {
            repository.deleteAllByUserId(userId);
        }
        return ServiceResponse.builder().build();
    }

    public Map<String, List<String>> valid(CourseUser courseUser) {
        Map<String, List<String>> errors = new HashMap<>();
        if (courseUser.getCourse() == null) {
            errors.put("course", List.of("Course not exists"));
        } else {
            Course course = new Course();
            course.setId(courseUser.getCourse().getId());
            if (repository.existsByCourseAndUserId(course, courseUser.getUserId())) {
                errors.put("courseUser", List.of("Relationship already exists"));
            }
        }
        
        try {
            if (authUserClient.findUserById(courseUser.getUserId()) == null) {
                errors.put("user", List.of("User not exists"));
            }
        } catch (UserBlockedException ex) {
            errors.put("user", List.of("User blocked"));
        } catch (HttpStatusCodeException ex) {
            errors.put("user", List.of("Error in get user"));
        }

        return errors;
    }

    public void merge(CourseUserDTO source, CourseUserDTO dest) {
        BeanUtils.copyProperties(source, dest);
    }

    public void merge(CourseUserDTO source, CourseUserDTO dest, Class<? extends CourseUserDTO.CourseUserView> view) {
        String[] fieldsNotInViewToIgnore = Arrays.stream(CourseUserDTO.class.getDeclaredFields())
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

    private void merge(CourseUserDTO source, CourseUser dest) {
        BeanUtils.copyProperties(source, dest);
    }
}
