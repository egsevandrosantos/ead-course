package com.ead.course.repositories;

import com.ead.course.models.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ModuleRepository extends JpaRepository<Module, UUID>, JpaSpecificationExecutor<Module>/*, QuerydslPredicateExecutor<Module>*/ {
//    @EntityGraph(attributePaths = {"course"}) // Carrega Module e Course junto
//    Module findByTitle(String title);

//    @Query(value = "SELECT * FROM modules WHERE course_id = :courseId", nativeQuery = true)
    @Query(value = "SELECT module FROM Module module WHERE module.course.id = :courseId")
    List<Module> findAllIntoCourse(@Param("courseId") UUID courseId);

    @Query(value = "SELECT module FROM Module module WHERE module.id = :id AND module.course.id = :courseId")
    Optional<Module> findByIdIntoCourse(@Param("id") UUID id, @Param("courseId") UUID courseId);

    @Modifying
    @Query("DELETE FROM Module m WHERE m.course.id = :courseId")
    void deleteAllByCourseId(UUID courseId); // Evitar a consulta SELECT antes do DELETE

    @Modifying
    @Query("DELETE FROM Module m WHERE m.id = :id")
    void deleteById(UUID id); // Evitar a consulta SELECT antes do DELETE
}
