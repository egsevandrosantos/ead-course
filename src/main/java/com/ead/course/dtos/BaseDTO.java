package com.ead.course.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public abstract class BaseDTO extends RepresentationModel<BaseDTO> {
    @JsonIgnore
    private Map<String, List<String>> errors = new HashMap<>();
}
