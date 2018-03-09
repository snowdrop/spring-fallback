package com.example.fallback;

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
        return "spring faulttolerance";
    }

    public String nonDefaultFallbackWithParam(ExecutionContext executionContext) {
        return "spring faulttolerance for " + executionContext.getMethod().getName();
    }
}
