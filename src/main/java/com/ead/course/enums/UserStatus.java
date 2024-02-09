package com.ead.course.enums;

import java.util.Objects;
import java.util.stream.Stream;

public enum UserStatus {
    ACTIVE(1),
    BLOCKED(2);

    private final int value;

    UserStatus(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }

    public static UserStatus fromValue(int value) {
        return Stream.of(UserStatus.values())
            .filter(e -> Objects.equals(e.value(), value))
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
    }
}
