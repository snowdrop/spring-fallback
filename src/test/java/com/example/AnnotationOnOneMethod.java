package com.example;

import me.snowdrop.fallback.Fallback;
import org.springframework.stereotype.Component;

@Component
public class AnnotationOnOneMethod {

    @Fallback
    public String annotatedMethod() {
        return "hello";
    }

    public String nonAnnotatedMethod() {
        return "hi";
    }
}
