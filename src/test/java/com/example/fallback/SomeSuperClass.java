package com.example.fallback;

import org.eclipse.microprofile.faulttolerance.Fallback;

public class SomeSuperClass {

    @Fallback
    public Object annotatedMethod() {
        throw new RuntimeException();
    }

    public Object nonAnnotatedMethod() {
        return new Object();
    }

    public Object handle() {
        return null;
    }
}
