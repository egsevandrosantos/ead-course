package com.ead.course.repositories;

import com.ead.course.models.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface CourseRepository extends JpaRepository<Course, UUID>, JpaSpecificationExecutor<Course> {
    @Modifying
    @Query("DELETE FROM Course c WHERE c.id = :id")
    void deleteById(UUID id); // Evitar a consulta SELECT antes do DELETE
}
