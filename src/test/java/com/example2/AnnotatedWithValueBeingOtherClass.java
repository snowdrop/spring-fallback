package com.example2;

import me.snowdrop.fallback.Fallback;
import org.springframework.stereotype.Component;

@Component
public class AnnotatedWithValueBeingOtherClass {

    @Fallback(ErrorHandler.class)
    public String perform() {
        throw new RuntimeException();
    }
}
