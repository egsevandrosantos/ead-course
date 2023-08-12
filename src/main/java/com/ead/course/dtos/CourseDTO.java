package com.ead.course.dtos;

import com.ead.course.enums.CourseLevel;
import com.ead.course.enums.CourseStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CourseDTO extends BaseDTO {
    public interface CourseView {}
    public interface Create extends CourseView {}
    public interface Update extends CourseView {}

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID id;

    @JsonView({Create.class, Update.class})
    @NotBlank(groups = {Create.class, Update.class})
    private String name;

    @JsonView({Create.class, Update.class})
    @NotBlank(groups = {Create.class, Update.class})
    private String description;

    @JsonView({Create.class, Update.class})
    private String imageUrl;

    @JsonView({Create.class, Update.class})
    @NotNull(groups = {Create.class, Update.class})
    private CourseStatus status;

    @JsonView({Create.class, Update.class})
    @NotNull(groups = {Create.class, Update.class})
    private CourseLevel level;

    @JsonView({Create.class, Update.class})
    @NotNull(groups = {Create.class, Update.class})
    private UUID userInstructorId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", timezone = "UTC")
    private Instant createdAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", timezone = "UTC")
    private Instant updatedAt;
}
