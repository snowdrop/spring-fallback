package com.example;

import me.snowdrop.fallback.Fallback;
import org.springframework.stereotype.Component;

@Component
public class AnnotationOnSomeMethods {

    @Fallback
    public String annotatedMethod() {
        return "hello";
    }

    @Fallback
    public void annotatedVoidMethod() {}

    public String nonAnnotatedMethod() {
        return "hi";
    }

    public void nonAnnotatedVoidMethod() {}
}
