package com.ead.course.services;

import com.ead.course.repositories.ModuleRepository;
import com.ead.course.services.interfaces.ModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ModuleServiceImpl implements ModuleService {
    @Autowired
    private ModuleRepository repository;
}
