package com.ead.course.repositories;

import com.ead.course.models.Course;
import com.ead.course.models.CourseUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface CourseUserRepository extends JpaRepository<CourseUser, UUID> {
    boolean existsByCourseAndUserId(Course course, UUID userId);
    List<CourseUser> findByCourse(Course course);
    
    @Modifying
    @Query("DELETE FROM CourseUser cu WHERE cu.course.id = :courseId")
    void deleteAllByCourseId(UUID courseId); // Evitar a consulta SELECT antes do DELETE
}
