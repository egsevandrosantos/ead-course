package com.ead.course.repositories;

import com.ead.course.models.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LessonRepository extends JpaRepository<Lesson, UUID> {
    @Query(value = "SELECT lesson FROM Lesson lesson WHERE lesson.module.id = :moduleId")
    List<Lesson> findAllIntoModule(@Param("moduleId") UUID moduleId);

    @Query(value = "SELECT CASE WHEN COUNT(lesson) > 0 THEN true ELSE false END FROM Lesson lesson WHERE lesson.id = :id AND lesson.module.id = :moduleId")
    boolean existsByIdIntoModule(@Param("id") UUID id, @Param("moduleId") UUID moduleId);

    @Query(value = "SELECT lesson FROM Lesson lesson WHERE lesson.id = :id AND lesson.module.id = :moduleId")
    Optional<Lesson> findByIdIntoModule(@Param("id") UUID id, @Param("moduleId") UUID moduleId);
}
