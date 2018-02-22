package com.example2;

import me.snowdrop.fallback.Fallback;
import org.springframework.stereotype.Component;

@Component
public class SomeMethodsAnnotatedWithDefaultValue {

    public String normalSayHi() {
        return "hi";
    }

    @Fallback
    public String defaultErrorSayHi() {
        throw new RuntimeException();
    }

    public String error() {
        return "defaultError";
    }

    @Fallback(fallbackMethod = "nonDefaultError")
    public String nonDefaultErrorSayHi() {
        throw new RuntimeException();
    }

    public String nonDefaultError() {
        return "nonDefaultError";
    }
}
