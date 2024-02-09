package com.ead.course.services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.ead.course.dtos.UserDTO;
import com.ead.course.models.User;
import com.ead.course.repositories.UserRepository;
import com.ead.course.services.interfaces.CourseUserService;

@Service
public class CourseUserServiceImpl implements CourseUserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public Page<UserDTO> findAll(Specification<User> filtersSpec, Pageable pageable, UUID courseId) {
        filtersSpec = ((Specification<User>) (root, query, criteriaBuilder) -> {
            return criteriaBuilder.and(criteriaBuilder.equal(root.get("courses").get("id"), courseId)); // TODO: WORK?
        }).and(filtersSpec);
        Page<User> usersPage = userRepository.findAll(filtersSpec, pageable);

        List<User> users = usersPage.getContent();
        List<UserDTO> usersDTO = new ArrayList<>();
        for (User user : users) {
            UserDTO userDTO = new UserDTO();
            BeanUtils.copyProperties(user, userDTO);
            usersDTO.add(userDTO);
        }
        return new PageImpl<>(usersDTO, usersPage.getPageable(), usersPage.getTotalElements());
    }
    
}
