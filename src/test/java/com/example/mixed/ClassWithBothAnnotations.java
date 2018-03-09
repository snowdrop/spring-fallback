package com.example.mixed;

import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.springframework.stereotype.Component;

@Component
public class ClassWithBothAnnotations {

    private int numberOfInvocations = 0;

    @Fallback(fallbackMethod = "fallback")
    @Retry(maxRetries = 3)
    public String annotatedMethodThatRecovers() {
        if (numberOfInvocations++ < 2) {
            throw new RuntimeException();
        }

        return "default";
    }

    @Fallback(fallbackMethod = "fallback")
    @Retry(maxRetries = 3)
    public String annotatedMethodThatDoesNotRecover() {
        numberOfInvocations++;
        throw new RuntimeException();
    }

    private String fallback() {
        return "fallback";
    }

    public void reset() {
        numberOfInvocations = 0;
    }

    public int getNumberOfInvocations() {
        return numberOfInvocations;
    }
}
