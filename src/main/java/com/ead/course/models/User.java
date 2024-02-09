package com.ead.course.models;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Data;

@Entity
@Table(name = "users")
@Data
@EntityListeners(AuditingEntityListener.class)
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id // Related ID from another microservice (non auto-generate)
    private UUID id;
    @Column(nullable = false, unique = true, length = 50)
    private String email;
    @Column(nullable = false, length = 150)
    private String name;
    @Column(nullable = false)
    private Integer status; // Enum(UserStatus)
    @Column(nullable = false)
    private Integer type; // Enum(UserType)
    @Column(length = 11)
    private String cpf;
    @Column
    private String imageUrl;
    @Column(nullable = false, updatable = false)
    @CreatedDate
    private Instant createdAt;
    @Column(nullable = false)
    @LastModifiedDate
    private Instant updatedAt;
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "users")
    private Set<Course> courses; // Set create pk in courses_users
}
