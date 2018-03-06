package com.example;

import me.snowdrop.fallback.ExecutionContext;
import org.springframework.stereotype.Component;

@Component
public class SpringErrorHandler {

    public String springFallback() {
        return "spring error";
    }

    public String springFallbackWithParam(ExecutionContext executionContext) {
        return "spring fallback for " + executionContext.getMethod().getName();
    }
}
