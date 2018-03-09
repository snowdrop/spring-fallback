package com.example.fallback;

import org.eclipse.microprofile.faulttolerance.ExecutionContext;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.springframework.stereotype.Component;

@Component
public class SomeMethodsAnnotated {

    public String isAnnotatedAndDoesNotThrowException() {
        return "default";
    }

    @Fallback
    public String isAnnotatedButDoesNotThrowException() {
        return "default";
    }

    @Fallback
    public String defaultErrorSayHi() {
        throw new RuntimeException();
    }

    private String handle() {
        return "defaultError";
    }

    @Fallback(fallbackMethod = "nonDefaultError")
    public String nonDefaultErrorSayHi() {
        throw new RuntimeException();
    }

    public String nonDefaultError(ExecutionContext executionContext) {
        return "faulttolerance from" + executionContext.getMethod().getName();
    }
}
