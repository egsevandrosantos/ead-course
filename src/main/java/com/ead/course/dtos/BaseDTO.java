package com.ead.course.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

@EqualsAndHashCode(callSuper = true)
@Data
public abstract class BaseDTO extends RepresentationModel<BaseDTO> {
}
