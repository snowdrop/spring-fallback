package com.example;

import org.eclipse.microprofile.faulttolerance.ExecutionContext;
import org.eclipse.microprofile.faulttolerance.FallbackHandler;
import org.springframework.stereotype.Component;

@Component
public class SpringErrorHandler implements FallbackHandler<String> {

    @Override
    public String handle(ExecutionContext context) {
        return "default";
    }

    public String nonDefaultFallback() {
        return "spring fallback";
    }

    public String nonDefaultFallbackWithParam(ExecutionContext executionContext) {
        return "spring fallback for " + executionContext.getMethod().getName();
    }
}
