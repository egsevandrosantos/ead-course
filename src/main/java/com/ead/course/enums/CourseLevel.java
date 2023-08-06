package com.ead.course.enums;

import java.util.Objects;
import java.util.stream.Stream;

public enum CourseLevel {
    BEGINNER(1),
    INTERMEDIARY(2),
    ADVANCED(3);

    private final int value;

    CourseLevel(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public static CourseLevel fromValue(int value) {
        return Stream.of(CourseLevel.values())
            .filter(e -> Objects.equals(e.value(), value))
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
    }
}
