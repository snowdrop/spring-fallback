package com.example;

import org.springframework.stereotype.Component;

@Component
public class SomeInterfaceImpl implements SomeInterface {

    @Override
    public String annotatedMethod(String input) {
        throw new RuntimeException();
    }

    @Override
    public String nonAnnotatedMethod(String input) {
        throw new RuntimeException();
    }

    public String handle() {
        return "error";
    }
}
