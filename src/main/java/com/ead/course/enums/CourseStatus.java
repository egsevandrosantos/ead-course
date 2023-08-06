package com.ead.course.enums;

import java.util.Objects;
import java.util.stream.Stream;

public enum CourseStatus {
    IN_PROGRESS(1),
    CONCLUDED(2);

    private final int value;

    CourseStatus(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }

    public static CourseStatus fromValue(int value) {
        return Stream.of(CourseStatus.values())
            .filter(e -> Objects.equals(e.value(), value))
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
    }
}
