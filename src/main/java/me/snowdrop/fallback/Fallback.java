package me.snowdrop.fallback;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Fallback {

    /**
     * The default value with result in the class of the method that is being annotated to be used
     */
    Class value() default void.class;

    /**
     * The method to be executed as the fallback method
     * If the name is enclosed in '${}' the it is assumed to be a property
     * and will be looked up in the Spring Environment
     */
    String fallbackMethod() default  "error";
}
