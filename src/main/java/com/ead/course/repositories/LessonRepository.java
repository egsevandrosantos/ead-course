package com.ead.course.repositories;

import com.ead.course.models.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;
import java.util.UUID;

public interface LessonRepository extends JpaRepository<Lesson, UUID> {
    @Query(value = "SELECT lesson FROM Lesson lesson WHERE lesson.module.id = :moduleId")
    Set<Lesson> findAllLessonsIntoModule(@Param("moduleId") UUID moduleId);
}
