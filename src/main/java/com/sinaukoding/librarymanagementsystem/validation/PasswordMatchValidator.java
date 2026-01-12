package com.sinaukoding.librarymanagementsystem.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, Object> {

    private String field;
    private String matchField;

    @Override
    public void initialize(PasswordMatch constraintAnnotation) {
        this.field = constraintAnnotation.field();
        this.matchField = constraintAnnotation.matchField();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        try {
            Object fieldValue = getFieldValue(value, field);
            Object matchFieldValue = getFieldValue(value, matchField);

            if (fieldValue == null || matchFieldValue == null) {
                return true;
            }

            return fieldValue.equals(matchFieldValue);
        } catch (Exception e) {
            return false;
        }
    }

    private Object getFieldValue(Object object, String fieldName) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(object);
    }
}
