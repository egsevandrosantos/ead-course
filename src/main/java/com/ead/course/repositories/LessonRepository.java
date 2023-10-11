package com.ead.course.repositories;

import com.ead.course.models.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LessonRepository extends JpaRepository<Lesson, UUID>, JpaSpecificationExecutor<Lesson> {
    @Query(value = "SELECT lesson FROM Lesson lesson WHERE lesson.module.id = :moduleId")
    List<Lesson> findAllIntoModule(@Param("moduleId") UUID moduleId);

    @Query(value = "SELECT CASE WHEN COUNT(lesson) > 0 THEN true ELSE false END FROM Lesson lesson WHERE lesson.id = :id AND lesson.module.id = :moduleId")
    boolean existsByIdIntoModule(@Param("id") UUID id, @Param("moduleId") UUID moduleId);

    @Query(value = "SELECT lesson FROM Lesson lesson WHERE lesson.id = :id AND lesson.module.id = :moduleId")
    Optional<Lesson> findByIdIntoModule(@Param("id") UUID id, @Param("moduleId") UUID moduleId);

    @Modifying
    @Query("DELETE FROM Lesson l WHERE l.module.id IN (SELECT m.id FROM Module m WHERE m.course.id = :courseId)")
    void deleteAllByModuleCourseId(UUID courseId); // Evitar a consulta SELECT antes do DELETE

    @Modifying
    @Query("DELETE FROM Lesson l WHERE l.module.id = :moduleId")
    void deleteAllByModuleId(UUID moduleId); // Evitar a consulta SELECT antes do DELETE

    @Modifying
    @Query("DELETE FROM Lesson l WHERE l.id = :id")
    void deleteById(UUID id); // Evitar a consulta SELECT antes do DELETE
}
