package com.example.retry;

import org.eclipse.microprofile.faulttolerance.Retry;

public interface SomeInterface {

    @Retry(maxRetries = 3)
    String annotatedMethod(String input);

    String nonAnnotatedMethod(String input);

    void reset();
}
