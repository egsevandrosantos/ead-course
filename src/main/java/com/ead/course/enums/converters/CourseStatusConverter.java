package com.ead.course.enums.converters;

import com.ead.course.enums.CourseStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class CourseStatusConverter implements AttributeConverter<CourseStatus, Integer> {
    @Override
    public Integer convertToDatabaseColumn(CourseStatus courseStatus) {
        if (courseStatus == null) {
            return null;
        }
        return courseStatus.value();
    }

    @Override
    public CourseStatus convertToEntityAttribute(Integer value) {
        if (value == null) {
            return null;
        }
        return CourseStatus.fromValue(value);
    }
}
