package com.example.retry;

import org.eclipse.microprofile.faulttolerance.Retry;

public class SomeSuperClass {

    private int numberOfInvocations = 0;

    @Retry(maxRetries = 3)
    public Object annotatedMethod() {
        if (numberOfInvocations++ < 2) {
            throw new RuntimeException();
        }

        return new Object();
    }

    public Object nonAnnotatedMethod() {
        if (numberOfInvocations++ < 2) {
            throw new RuntimeException();
        }

        return new Object();
    }

    public void reset() {
        numberOfInvocations = 0;
    }
}
