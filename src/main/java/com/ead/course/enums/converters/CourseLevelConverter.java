package com.ead.course.enums.converters;

import com.ead.course.enums.CourseLevel;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class CourseLevelConverter implements AttributeConverter<CourseLevel, Integer> {
    @Override
    public Integer convertToDatabaseColumn(CourseLevel courseLevel) {
        if (courseLevel == null) {
            return null;
        }
        return courseLevel.value();
    }

    @Override
    public CourseLevel convertToEntityAttribute(Integer value) {
        if (value == null) {
            return null;
        }
        return CourseLevel.fromValue(value);
    }
}
