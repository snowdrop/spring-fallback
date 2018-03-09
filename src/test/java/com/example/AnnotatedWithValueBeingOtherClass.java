package com.example;

import org.eclipse.microprofile.faulttolerance.Fallback;
import org.springframework.stereotype.Component;

@Component
public class AnnotatedWithValueBeingOtherClass {

    @Fallback(value = SpringErrorHandler.class)
    public String errorHandlerInSpringBeanDefaultMethod() {
        throw new RuntimeException();
    }

    @Fallback(value = SpringErrorHandler.class, fallbackMethod = "nonDefaultFallback")
    public String errorHandlerInSpringBeanNonDefaultMethod() {
        throw new RuntimeException();
    }

    @Fallback(value = SpringErrorHandler.class, fallbackMethod = "nonDefaultFallbackWithParam")
    public String errorHandlerInSpringBeanNonDefaultMethodWithParam() {
        throw new RuntimeException();
    }
}
