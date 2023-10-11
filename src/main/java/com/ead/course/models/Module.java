package com.ead.course.models;

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
@Table(name = "modules")
@Data
@EntityListeners(AuditingEntityListener.class)
public class Module implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Column(nullable = false, length = 150)
    private String title;
    @Column(nullable = false, length = 250)
    private String description;
    @Column(nullable = false, updatable = false)
    @CreatedDate
    private Instant createdAt;
    @Column(nullable = false)
    @LastModifiedDate
    private Instant updatedAt;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Course course;
    @OneToMany(mappedBy = "module", fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    private List<Lesson> lessons;
}
