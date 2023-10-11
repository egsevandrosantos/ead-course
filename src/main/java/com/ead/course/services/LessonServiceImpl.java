package com.ead.course.services;

import com.ead.course.dtos.LessonDTO;
import com.ead.course.dtos.ModuleDTO;
import com.ead.course.models.Lesson;
import com.ead.course.models.Module;
import com.ead.course.repositories.LessonRepository;
import com.ead.course.repositories.ModuleRepository;
import com.ead.course.services.interfaces.LessonService;
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
import java.util.*;

import javax.transaction.Transactional;

@Service
public class LessonServiceImpl implements LessonService {
    @Autowired
    private LessonRepository repository;
    @Autowired
    private ModuleRepository moduleRepository;

    @Override
    public ServiceResponse create(UUID moduleId, LessonDTO lessonDTO) {
        Module module = new Module();
        module.setId(moduleId);

        Lesson lesson = new Lesson();
        lesson.setModule(module);
        merge(lessonDTO, lesson);
        Map<String, List<String>> errors = valid(lesson);
        if (!errors.isEmpty()) {
            return ServiceResponse.builder()
                .ok(false)
                .errors(errors)
                .build();
        }

        repository.save(lesson);
        return ServiceResponse.builder().id(lesson.getId()).build();
    }

    @Override
    @Transactional // Evitar a consulta SELECT antes do UPDATE
    public ServiceResponse update(UUID moduleId, UUID id, LessonDTO updatedLessonDTO) {
        Optional<Lesson> lessonOpt = repository.findByIdIntoModule(id, moduleId);
        if (lessonOpt.isEmpty()) {
            return ServiceResponse.builder()
                .ok(false)
                .found(false)
                .build();
        }

        Module module = new Module();
        module.setId(moduleId);

        Lesson lesson = lessonOpt.get();
        lesson.setModule(module);
        merge(updatedLessonDTO, lesson, LessonDTO.Update.class);
        Map<String, List<String>> errors = valid(lesson);
        if (!errors.isEmpty()) {
            return ServiceResponse.builder()
                .ok(false)
                .errors(errors)
                .build();
        }

        repository.save(lesson);
        return ServiceResponse.builder().build();
    }

    @Override
    public ServiceResponse deleteById(UUID id) {
        if (id == null) {
            return ServiceResponse.builder()
                .ok(false)
                .build();
        }

        if (repository.existsById(id)) {
            repository.deleteById(id);
        }
        return ServiceResponse.builder().build();
    }

    public void merge(LessonDTO source, Lesson dest, Class<? extends LessonDTO.LessonView> view) {
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
    }

    private void merge(Lesson source, LessonDTO dest) {
        BeanUtils.copyProperties(source, dest);

        if (Hibernate.isInitialized(source.getModule())) {
            ModuleDTO moduleDTO = new ModuleDTO();
            BeanUtils.copyProperties(source.getModule(), moduleDTO);
            dest.setModule(moduleDTO);
        }
    }

    public Map<String, List<String>> valid(Lesson updatedLesson) {
        Map<String, List<String>> errors = new HashMap<>();

        boolean moduleNotExists = updatedLesson.getModule() == null
            || updatedLesson.getModule().getId() == null
            || !(moduleRepository.existsById(updatedLesson.getModule().getId()));
        if (moduleNotExists) {
            errors.put("module", List.of("Module not exists."));
        }

        return errors;
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
