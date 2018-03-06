package com.example;

import me.snowdrop.fallback.Fallback;
import org.springframework.stereotype.Component;

@Component
public class AnnotatedWithValueBeingOtherClass {

    @Fallback(StaticErrorHandlerWithoutParam.class)
    public String errorHandlerInStaticClass() {
        throw new RuntimeException();
    }

    @Fallback(StaticErrorHandlerWithParam.class)
    public String errorHandlerInStaticClassWithParam() {
        throw new RuntimeException();
    }

    @Fallback(value = SpringErrorHandler.class, fallbackMethod = "springFallback")
    public String errorHandlerInSpringBean() {
        throw new RuntimeException();
    }

    @Fallback(value = SpringErrorHandler.class, fallbackMethod = "springFallbackWithParam")
    public String errorHandlerInSpringBeanWithParam() {
        throw new RuntimeException();
    }
}
