package com.ead.course.models;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "lessons")
@Data
@EntityListeners(AuditingEntityListener.class)
public class Lesson implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Column(nullable = false, length = 150)
    private String title;
    @Column(nullable = false, length = 250)
    private String description;
    @Column(nullable = false)
    private String videoUrl;
    @Column(nullable = false, updatable = false)
    @CreatedDate
    private Instant createdAt;
    @Column(nullable = false)
    @LastModifiedDate
    private Instant updatedAt;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Module module;
}
