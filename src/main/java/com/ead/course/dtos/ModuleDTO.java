package com.ead.course.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ModuleDTO extends BaseDTO {
    public interface ModuleView {}
    public interface Create extends ModuleView {}
    public interface Update extends ModuleView {}

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID id;

    @JsonView({Create.class, Update.class})
    @NotBlank(groups = {Create.class, Update.class})
    private String title;

    @JsonView({Create.class, Update.class})
    @NotBlank(groups = {Create.class, Update.class})
    private String description;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", timezone = "UTC")
    private Instant createdAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", timezone = "UTC")
    private Instant updatedAt;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private CourseDTO course;
}
