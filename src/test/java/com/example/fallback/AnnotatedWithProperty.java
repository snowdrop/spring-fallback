package com.example.fallback;

import org.eclipse.microprofile.faulttolerance.Fallback;
import org.springframework.stereotype.Component;

@Component
public class AnnotatedWithProperty {

    @Fallback(fallbackMethod = "${faulttolerance.name}")
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
