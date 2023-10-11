package com.ead.course.services;

import com.ead.course.dtos.CourseDTO;
import com.ead.course.dtos.ModuleDTO;
import com.ead.course.models.Course;
import com.ead.course.models.Module;
import com.ead.course.repositories.CourseRepository;
import com.ead.course.repositories.LessonRepository;
import com.ead.course.repositories.ModuleRepository;
import com.ead.course.services.interfaces.ModuleService;
import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.Hibernate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.lang.reflect.Field;
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
    public ServiceResponse create(UUID courseId, ModuleDTO moduleDTO) {
        if (courseId == null) {
            return ServiceResponse.builder()
                .ok(false)
                .found(false)
                .build();
        }

        Course course = new Course();
        course.setId(courseId);

        Module module = new Module();
        module.setCourse(course);
        merge(moduleDTO, module);
        Map<String, List<String>> errors = valid(module);
        if (!errors.isEmpty()) {
            return ServiceResponse.builder()
                .ok(false)
                .errors(errors)
                .build();
        }

        repository.save(module);
        return ServiceResponse.builder().id(module.getId()).build();
    }

    @Override
    @Transactional // Evitar a consulta SELECT antes do UPDATE
    public ServiceResponse update(UUID id, UUID courseId, ModuleDTO moduleDTO) {
        Optional<Module> moduleOpt;
        if (id == null || courseId == null || (moduleOpt = repository.findByIdIntoCourse(id, courseId)).isEmpty()) {
            return ServiceResponse.builder()
                .ok(false)
                .found(false)
                .build();
        }

        Course course = new Course();
        course.setId(courseId);

        Module module = moduleOpt.get();
        module.setCourse(course);
        merge(moduleDTO, module, ModuleDTO.Update.class);
        Map<String, List<String>> errors = valid(module);
        if (!errors.isEmpty()) {
            return ServiceResponse.builder()
                .ok(false)
                .errors(errors)
                .build();
        }

        repository.save(module);
        return ServiceResponse.builder().build();
    }

    @Transactional
    @Override
    public ServiceResponse deleteById(UUID id) throws IllegalArgumentException {
        if (id == null) {
            return ServiceResponse.builder()
                .ok(false)
                .build();
        }

        if (repository.existsById(id)) {
            lessonRepository.deleteAllByModuleId(id);
            repository.deleteById(id);
        }
        return ServiceResponse.builder().build();
    }

    public void merge(ModuleDTO source, Module dest, Class<? extends ModuleDTO.ModuleView> view) {
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
    }

    private void merge(Module source, ModuleDTO dest) {
        BeanUtils.copyProperties(source, dest);

        if (Hibernate.isInitialized(source.getCourse())) {
            CourseDTO course = new CourseDTO();
            BeanUtils.copyProperties(source.getCourse(), course);
            dest.setCourse(course);
        }
    }

    public Map<String, List<String>> valid(Module updatedModule) {
        Map<String, List<String>> errors = new HashMap<>();

        boolean courseNotExists = updatedModule.getCourse() == null
            || updatedModule.getCourse().getId() == null
            || !courseRepository.existsById(updatedModule.getCourse().getId());
        if (courseNotExists) {
            errors.put("course", List.of("Course not exists."));
        }

        return errors;
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
