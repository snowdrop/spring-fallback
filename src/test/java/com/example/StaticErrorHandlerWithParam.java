package com.example;

import me.snowdrop.fallback.ExecutionContext;

public final class StaticErrorHandlerWithParam {

    private StaticErrorHandlerWithParam() {}

    public static String error(ExecutionContext executionContext) {
        return "fallback from " + executionContext.getMethod().getName();
    }
}
