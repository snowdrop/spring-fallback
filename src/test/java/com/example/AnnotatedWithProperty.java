package com.example;

import me.snowdrop.fallback.Fallback;
import org.springframework.stereotype.Component;

@Component
public class AnnotatedWithProperty {

    @Fallback(fallbackMethod = "${fallback.name}")
    public String invoke() {
        throw new RuntimeException();
    }

    private String fallback1() {
        return "fallback1";
    }

    private String fallback2() {
        return "fallback2";
    }
}