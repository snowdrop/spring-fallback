package com.example;

import me.snowdrop.fallback.Fallback;

public class SomeSuperClass {

    @Fallback
    public Object annotatedMethod() {
        throw new RuntimeException();
    }

    public Object nonAnnotatedMethod() {
        return new Object();
    }

    public Object error() {
        return null;
    }
}
