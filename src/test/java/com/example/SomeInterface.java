package com.example;

import me.snowdrop.fallback.Fallback;

public interface SomeInterface {

    @Fallback
    String annotatedMethod(String input);

    String nonAnnotatedMethod(String input);
}
