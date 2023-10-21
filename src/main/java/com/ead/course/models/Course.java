package com.ead.course.models;

import com.ead.course.enums.CourseLevel;
import com.ead.course.enums.CourseStatus;
import com.ead.course.enums.converters.CourseLevelConverter;
import com.ead.course.enums.converters.CourseStatusConverter;
import lombok.Data;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "courses")
@Data
@EntityListeners(AuditingEntityListener.class)
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
    @CreatedDate
    private Instant createdAt;
    @Column(nullable = false)
    @LastModifiedDate
    private Instant updatedAt;
    @Column(nullable = false)
    private UUID userInstructorId;
    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY) //, cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT) // O JOIN ignora o FetchType LAZY e carrega com EAGER, SELECT faz varias queries a mais e SUBSELECT apenas uma query a mais
//    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Module> modules;
}
