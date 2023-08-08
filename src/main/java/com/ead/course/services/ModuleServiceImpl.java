package com.ead.course.services;

import com.ead.course.models.Lesson;
import com.ead.course.models.Module;
import com.ead.course.repositories.LessonRepository;
import com.ead.course.repositories.ModuleRepository;
import com.ead.course.services.interfaces.ModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Set;

@Service
public class ModuleServiceImpl implements ModuleService {
    @Autowired
    private ModuleRepository repository;
    @Autowired
    private LessonRepository lessonRepository;

    @Transactional
    @Override
    public void delete(Module module) {
        Set<Lesson> lessons = lessonRepository.findAllLessonsIntoModule(module.getId());
        lessonRepository.deleteAll(lessons);
        repository.delete(module);
    }
}
