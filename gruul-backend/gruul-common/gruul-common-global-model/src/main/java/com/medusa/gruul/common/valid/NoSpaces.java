package com.medusa.gruul.common.valid;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * 不能包含空格的校验注解
 *
 * @author 张治保
 */
@Documented
@Constraint(validatedBy = {})
@Pattern(regexp = "^\\S+$", message = "不能包含空格")
@Retention(RetentionPolicy.RUNTIME)
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
public @interface NoSpaces {

    /**
     * @return the groups the constraint belongs to
     */
    Class<?>[] groups() default {};

    /**
     * @return the payload associated to the constraint
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * @return the error message template
     */
    String message() default "不能包含空格";
}