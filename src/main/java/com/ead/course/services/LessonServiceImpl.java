package com.ead.course.services;

import com.ead.course.dtos.LessonDTO;
import com.ead.course.dtos.ModuleDTO;
import com.ead.course.models.Lesson;
import com.ead.course.models.Module;
import com.ead.course.repositories.LessonRepository;
import com.ead.course.repositories.ModuleRepository;
import com.ead.course.services.interfaces.LessonService;
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

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.*;

@Service
public class LessonServiceImpl implements LessonService {
    @Autowired
    private LessonRepository repository;
    @Autowired
    private ModuleRepository moduleRepository;

    @Override
    public UUID create(LessonDTO lessonDTO) {
        Lesson lesson = new Lesson();
        merge(lessonDTO, lesson);
        Instant createdAt = Instant.now();
        lesson.setCreatedAt(createdAt);
        lesson.setUpdatedAt(createdAt);
        repository.save(lesson);
        return lesson.getId();
    }

    @Override
    public void update(LessonDTO updatedLessonDTO) {
        Lesson lesson = new Lesson();
        merge(updatedLessonDTO, lesson);
        lesson.setUpdatedAt(Instant.now());
        repository.save(lesson);
    }

    @Override
    public void deleteById(UUID id) throws IllegalArgumentException {
        Optional<Lesson> lessonOptional = null;
        if (id == null || (lessonOptional = repository.findById(id)).isEmpty()) {
            throw new IllegalArgumentException();
        }
        Lesson lesson = lessonOptional.get();
        repository.delete(lesson);
    }

    @Override
    public void merge(LessonDTO source, LessonDTO dest) {
        BeanUtils.copyProperties(source, dest);
    }

    @Override
    public void merge(LessonDTO source, LessonDTO dest, Class<? extends LessonDTO.LessonView> view) {
        String[] fieldsNotInViewToIgnore = Arrays.stream(LessonDTO.class.getDeclaredFields())
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

    private void merge(LessonDTO source, Lesson dest) {
        BeanUtils.copyProperties(source, dest);

        Module module = new Module();
        BeanUtils.copyProperties(source.getModule(), module);
        dest.setModule(module);
    }

    private void merge(Lesson source, LessonDTO dest) {
        BeanUtils.copyProperties(source, dest);

        if (Hibernate.isInitialized(source.getModule())) {
            ModuleDTO moduleDTO = new ModuleDTO();
            BeanUtils.copyProperties(source.getModule(), moduleDTO);
            dest.setModule(moduleDTO);
        }
    }

    @Override
    public boolean valid(LessonDTO updatedLessonDTO) {
        return valid(updatedLessonDTO, null);
    }

    @Override
    public boolean valid(LessonDTO updatedLessonDTO, LessonDTO internalLessonDTO) {
        Optional<Module> module = Optional.empty();
        boolean moduleNotExists = updatedLessonDTO.getModule() == null
            || updatedLessonDTO.getModule().getId() == null
            || (module = moduleRepository.findById(updatedLessonDTO.getModule().getId())).isEmpty();
        if (moduleNotExists) {
            updatedLessonDTO.getErrors().put("module", List.of("Module not exists."));
        } else {
            ModuleDTO moduleDTO = new ModuleDTO();
            BeanUtils.copyProperties(module.get(), moduleDTO);
            updatedLessonDTO.setModule(moduleDTO);
        }

        return updatedLessonDTO.getErrors().isEmpty();
    }

    @Override
    public boolean existsByIdIntoModule(UUID id, UUID moduleId) {
        return repository.existsByIdIntoModule(id, moduleId);
    }

    @Override
    public Optional<LessonDTO> findByIdIntoModule(UUID id, UUID moduleId) {
        Optional<Lesson> lessonOptional = repository.findByIdIntoModule(id, moduleId);
        return lessonOptional
            .map(lesson -> {
                LessonDTO lessonDTO = new LessonDTO();
                merge(lesson, lessonDTO);
                return Optional.of(lessonDTO);
            })
            .orElse(Optional.empty());
    }

    @Override
    public Page<LessonDTO> findAllIntoModule(UUID moduleId, Specification<Lesson> filtersSpec, Pageable pageable) {
        filtersSpec = ((Specification<Lesson>) (root, query, criteriaBuilder) -> {
            // root is Lesson
            return criteriaBuilder.and(criteriaBuilder.equal(root.get("module").get("id"), moduleId));
        }).and(filtersSpec);
        Page<Lesson> lessonPage = repository.findAll(filtersSpec, pageable);

        List<Lesson> lessons = lessonPage.getContent();
        List<LessonDTO> lessonsDTO = new ArrayList<>();
        lessons.forEach(lesson -> {
            LessonDTO lessonDTO = new LessonDTO();
            merge(lesson, lessonDTO);
            lessonsDTO.add(lessonDTO);
        });

        return new PageImpl<>(lessonsDTO, lessonPage.getPageable(), lessonPage.getTotalElements());
    }
}
