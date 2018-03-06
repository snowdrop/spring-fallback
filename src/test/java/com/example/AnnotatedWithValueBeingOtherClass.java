package com.example;

import me.snowdrop.fallback.Fallback;
import org.springframework.stereotype.Component;

@Component
public class AnnotatedWithValueBeingOtherClass {

    @Fallback(StaticErrorHandlerWithoutParam.class)
    public String method1() {
        throw new RuntimeException();
    }

    @Fallback(StaticErrorHandlerWithParam.class)
    public String method2() {
        throw new RuntimeException();
    }
}
