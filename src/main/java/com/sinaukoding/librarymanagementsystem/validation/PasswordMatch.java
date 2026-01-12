package com.sinaukoding.librarymanagementsystem.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordMatchValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordMatch {
    String message() default "Password baru dan konfirmasi password tidak sesuai";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String field();

    String matchField();
}
