package com.ead.course.repositories;

import com.ead.course.models.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;
import java.util.UUID;

public interface ModuleRepository extends JpaRepository<Module, UUID> {
//    @EntityGraph(attributePaths = {"course"}) // Carrega Module e Course junto
//    Module findByTitle(String title);

//    @Query(value = "SELECT * FROM modules WHERE course_id = :courseId", nativeQuery = true)
    @Query(value = "SELECT * FROM Module WHERE course.id = :courseId")
    Set<Module> findAllModulesIntoCourse(@Param("courseId") UUID courseId);
}
