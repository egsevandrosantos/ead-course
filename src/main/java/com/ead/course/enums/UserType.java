package com.ead.course.enums;

import java.util.Objects;
import java.util.stream.Stream;

public enum UserType {
    ADMIN(1),
    STUDENT(2),
    INSTRUCTOR(3);

    private final int value;

    UserType(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public static UserType fromValue(int value) {
        return Stream.of(UserType.values())
            .filter(e -> Objects.equals(e.value(), value))
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
    }
}
