package com.ead.course.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CourseUserDTO extends BaseDTO {
    public interface CourseUserView {}
    public interface Create extends CourseUserView {}

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID id;

    @JsonView({Create.class})
    @NotNull(groups = {Create.class})
    private UUID userId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private CourseDTO courseDTO;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", timezone = "UTC")
    private Instant createdAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", timezone = "UTC")
    private Instant updatedAt;
}
