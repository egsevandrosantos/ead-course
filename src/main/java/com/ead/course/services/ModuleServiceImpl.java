package com.ead.course.services;

import com.ead.course.dtos.CourseDTO;
import com.ead.course.dtos.ModuleDTO;
import com.ead.course.models.Course;
import com.ead.course.models.Lesson;
import com.ead.course.models.Module;
import com.ead.course.repositories.CourseRepository;
import com.ead.course.repositories.LessonRepository;
import com.ead.course.repositories.ModuleRepository;
import com.ead.course.services.interfaces.ModuleService;
import com.ead.course.specifications.SpecificationTemplate;
import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.Hibernate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.lang.reflect.Field;
import java.time.Instant;
import java.util.*;

@Service
public class ModuleServiceImpl implements ModuleService {
    @Autowired
    private ModuleRepository repository;
    @Autowired
    private LessonRepository lessonRepository;
    @Autowired
    private CourseRepository courseRepository;

    @Override
    public UUID create(ModuleDTO moduleDTO) {
        Module module = new Module();
        merge(moduleDTO, module);
        Instant createdAt = Instant.now();
        module.setCreatedAt(createdAt);
        module.setUpdatedAt(createdAt);
        repository.save(module);
        return module.getId();
    }

    @Override
    public void update(ModuleDTO updatedModuleDTO) {
        Module module = new Module();
        merge(updatedModuleDTO, module);
        module.setUpdatedAt(Instant.now());
        repository.save(module);
    }

    @Transactional
    @Override
    public void deleteById(UUID id) {
        List<Lesson> lessons = lessonRepository.findAllIntoModule(id);
        lessonRepository.deleteAll(lessons);
        repository.deleteById(id);
    }

    @Override
    public void merge(ModuleDTO source, ModuleDTO dest) {
        BeanUtils.copyProperties(source, dest);
    }

    @Override
    public void merge(ModuleDTO source, ModuleDTO dest, Class<? extends ModuleDTO.ModuleView> view) {
        String[] fieldsNotInViewToIgnore = Arrays.stream(ModuleDTO.class.getDeclaredFields())
            .filter(field -> {
                JsonView jsonView = field.getAnnotation(JsonView.class);
                if (jsonView == null) {
                    return true;
                }
                return Arrays.stream(jsonView.value()).noneMatch(value -> value.getName().equals(view.getName()));
            })
            .map(Field::getName)
            .toArray(String[]::new);
        BeanUtils.copyProperties(source, dest, fieldsNotInViewToIgnore);
    }

    private void merge(ModuleDTO source, Module dest) {
        BeanUtils.copyProperties(source, dest);

        Course course = new Course();
        BeanUtils.copyProperties(source.getCourse(), course);
        dest.setCourse(course);
    }

    private void merge(Module source, ModuleDTO dest) {
        BeanUtils.copyProperties(source, dest);

        if (Hibernate.isInitialized(source.getCourse())) {
            CourseDTO course = new CourseDTO();
            BeanUtils.copyProperties(source.getCourse(), course);
            dest.setCourse(course);
        }
    }

    @Override
    public boolean valid(ModuleDTO updatedModuleDTO) {
        return valid(updatedModuleDTO, null);
    }

    @Override
    public boolean valid(ModuleDTO updatedModuleDTO, ModuleDTO internalModuleDTO) {
        Optional<Course> course = Optional.empty();
        boolean courseNotExists = updatedModuleDTO.getCourse() == null
            || updatedModuleDTO.getCourse().getId() == null
            || (course = courseRepository.findById(updatedModuleDTO.getCourse().getId())).isEmpty();
        if (courseNotExists) {
            updatedModuleDTO.getErrors().put("course", List.of("Course not exists."));
        } else {
            CourseDTO courseDTO = new CourseDTO();
            BeanUtils.copyProperties(course.get(), courseDTO);
            updatedModuleDTO.setCourse(courseDTO);
        }

        return updatedModuleDTO.getErrors().isEmpty();
    }

    @Override
    public Page<ModuleDTO> findAllIntoCourse(UUID courseId, Specification<Module> filtersSpec, Pageable pageable) {
//        QModule qModule = QModule.module;
//        BooleanExpression isModuleIntoCourse = qModule.course.id.eq(courseId);
//        repository.findAll(isModuleIntoCourse, pageable);
        filtersSpec = ((Specification<Module>) (root, query, criteriaBuilder) -> {
            // root is Module
            return criteriaBuilder.and(criteriaBuilder.equal(root.get("course").get("id"), courseId));
        }).and(filtersSpec);
        Page<Module> modulesPage = repository.findAll(filtersSpec, pageable);
        List<Module> modules = modulesPage.getContent();

        List<ModuleDTO> modulesDTO = new ArrayList<>();
        modules.forEach(module -> {
            ModuleDTO moduleDTO = new ModuleDTO();
            merge(module, moduleDTO);
            modulesDTO.add(moduleDTO);
        });

        return new PageImpl<>(modulesDTO, modulesPage.getPageable(), modulesPage.getTotalElements());
    }

    @Override
    public Optional<ModuleDTO> findByIdIntoCourse(UUID id, UUID courseId) {
        Optional<Module> moduleOpt = repository.findByIdIntoCourse(id, courseId);
        return moduleOpt
            .map(module -> {
                ModuleDTO moduleDTO = new ModuleDTO();
                merge(module, moduleDTO);
                return Optional.of(moduleDTO);
            })
            .orElse(Optional.empty());
    }
}
