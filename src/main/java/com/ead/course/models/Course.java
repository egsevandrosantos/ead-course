package com.ead.course.models;

import com.ead.course.enums.CourseLevel;
import com.ead.course.enums.CourseStatus;
import com.ead.course.enums.converters.CourseLevelConverter;
import com.ead.course.enums.converters.CourseStatusConverter;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "courses")
@Data
public class Course implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Column(nullable = false, length = 150)
    private String name;
    @Column(nullable = false, length = 250)
    private String description;
    @Column
    private String imageUrl;
    @Convert(converter = CourseStatusConverter.class)
    @Column(nullable = false)
    private CourseStatus status;
    @Convert(converter = CourseLevelConverter.class)
    @Column(nullable = false)
    private CourseLevel level;
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
    @Column(nullable = false)
    private Instant updatedAt;
    @Column(nullable = false)
    private UUID userInstructorId;
}
