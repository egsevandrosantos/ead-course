package com.ead.course.specifications;

import com.ead.course.models.Course;
import net.kaczmarzyk.spring.data.jpa.domain.EqualIgnoreCase;
import net.kaczmarzyk.spring.data.jpa.domain.LikeIgnoreCase;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;

public class SpecificationTemplate {
    @And({
        @Spec(path = "status", spec = EqualIgnoreCase.class),
        @Spec(path = "level", spec = EqualIgnoreCase.class),
        @Spec(path = "name", spec = LikeIgnoreCase.class)
    })
    public interface CourseSpec extends Specification<Course> {}
}
