package com.ead.course.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public abstract class BaseDTO {
    @JsonIgnore
    private Map<String, List<String>> errors = new HashMap<>();
}
