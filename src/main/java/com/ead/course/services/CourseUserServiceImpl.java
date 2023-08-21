package com.ead.course.services;

import com.ead.course.clients.AuthUserClient;
import com.ead.course.dtos.CourseDTO;
import com.ead.course.dtos.CourseUserDTO;
import com.ead.course.dtos.UserDTO;
import com.ead.course.exceptions.UserBlockedException;
import com.ead.course.models.Course;
import com.ead.course.models.CourseUser;
import com.ead.course.repositories.CourseUserRepository;
import com.ead.course.services.interfaces.CourseUserService;
import com.fasterxml.jackson.annotation.JsonView;

import org.hibernate.Hibernate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;

import java.lang.reflect.Field;
import java.net.UnknownHostException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class CourseUserServiceImpl implements CourseUserService {
    @Autowired
    private CourseUserRepository repository;
    @Autowired
    private AuthUserClient authUserClient;

    @Override
    public Page<UserDTO> findAll(Pageable pageable, UUID courseId) {
        return authUserClient.findAllUsers(pageable, courseId);
    }

    @Transactional
    @Override
    public UUID create(CourseUserDTO courseUserDTO) {
        CourseUser courseUser = new CourseUser();
        merge(courseUserDTO, courseUser);
        Instant createdAt = Instant.now();
        courseUser.setCreatedAt(createdAt);
        courseUser.setUpdatedAt(createdAt);
        courseUser = repository.save(courseUser);

        authUserClient.createUserCourseRelationship(courseUser.getCourse().getId(), courseUser.getUserId());

        return courseUser.getId();
    }

    @Override
    public boolean valid(CourseUserDTO courseUserDTO) {
        if (courseUserDTO.getCourseDTO() == null) {
            courseUserDTO.getErrors().put("course", List.of("Course not exists"));
        } else {
            Course course = new Course();
            course.setId(courseUserDTO.getCourseDTO().getId());
            if (repository.existsByCourseAndUserId(course, courseUserDTO.getUserId())) {
                courseUserDTO.getErrors().put("courseUser", List.of("Relationship already exists"));
            }
        }
        
        try {
            if (authUserClient.findUserById(courseUserDTO.getUserId()) == null) {
                courseUserDTO.getErrors().put("user", List.of("User not exists"));
            }
        } catch (UserBlockedException ex) {
            courseUserDTO.getErrors().put("user", List.of("User blocked"));
        } catch (HttpStatusCodeException ex) {
            courseUserDTO.getErrors().put("user", List.of("Error in get user"));
        }

        return courseUserDTO.getErrors().isEmpty();
    }

    @Override
    public void merge(CourseUserDTO source, CourseUserDTO dest) {
        BeanUtils.copyProperties(source, dest);
    }

    @Override
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

        Course course = new Course();
        BeanUtils.copyProperties(source.getCourseDTO(), course);
        dest.setCourse(course);
    }

    private void merge(CourseUser source, CourseUserDTO dest) {
        BeanUtils.copyProperties(source, dest);

        if (Hibernate.isInitialized(source.getCourse())) {
            CourseDTO courseDTO = new CourseDTO();
            BeanUtils.copyProperties(source.getCourse(), courseDTO);
            dest.setCourseDTO(courseDTO);
        }
    }
}
