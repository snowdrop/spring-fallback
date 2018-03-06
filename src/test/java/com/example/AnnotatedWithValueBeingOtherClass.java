package com.example;

import me.snowdrop.fallback.Fallback;
import org.springframework.stereotype.Component;

@Component
public class AnnotatedWithValueBeingOtherClass {

    @Fallback(StaticErrorHandler.class)
    public String perform() {
        throw new RuntimeException();
    }
}
