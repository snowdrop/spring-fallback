package com.example.retry;

import org.springframework.stereotype.Component;

@Component
public class SomeInterfaceImpl implements SomeInterface {

    private int numberOfInvocations = 0;

    @Override
    public String annotatedMethod(String input) {
        if (numberOfInvocations++ < 2) {
            throw new RuntimeException();
        }

        return "out";
    }

    @Override
    public String nonAnnotatedMethod(String input) {
        if (numberOfInvocations++ < 2) {
            throw new RuntimeException();
        }

        return "out";
    }

    @Override
    public void reset() {
        numberOfInvocations = 0;
    }

}
