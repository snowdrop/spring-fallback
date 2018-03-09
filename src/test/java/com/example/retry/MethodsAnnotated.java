package com.example.retry;

import org.eclipse.microprofile.faulttolerance.Retry;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MethodsAnnotated {

    private int numberOfInvocations = 0;

    @Retry(maxRetries = 3, retryOn = RuntimeException.class)
    public void throwsRetryableExceptionAndRecovers() {
        if (numberOfInvocations++ < 2) {
            throw new DummyException();
        }
    }

    @Retry(maxRetries = 1, retryOn = RuntimeException.class)
    public void throwsRetryableExceptionAndDoesNotRecover() {
        if (numberOfInvocations++ < 1) {
            throw new IllegalArgumentException();
        }

        throw new IllegalStateException();
    }

    public void throwsExceptionAndIsNotAnnotatedWithRetry() {
        throw new IllegalStateException();
    }

    @Retry(maxRetries = 3, retryOn = IOException.class)
    public void throwsNonRetryableException() {
        if (numberOfInvocations++ < 1) {
            throw new RuntimeException();
        }
    }

    public int getNumberOfInvocations() {
        return numberOfInvocations;
    }

    public void reset() {
        numberOfInvocations = 0;
    }

    private static class DummyException extends RuntimeException {}
}
