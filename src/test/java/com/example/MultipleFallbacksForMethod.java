package com.example;

import me.snowdrop.fallback.Fallback;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;

@Component
public class MultipleFallbacksForMethod {

    @Fallback(fallbackMethod = "genericHandler", order = Integer.MAX_VALUE)
    @Fallback(fallbackMethod = "ioHandler", throwable = IOException.class)
    @Fallback(fallbackMethod = "fileHandler", throwable = FileNotFoundException.class, order = Integer.MIN_VALUE)
    @Fallback(fallbackMethod = "runtimeHandler", throwable = RuntimeException.class)
    public String dummyThrower(Exception typeToThrow) throws Exception {
        if(typeToThrow != null) {
            throw typeToThrow;
        }

        return "noException";
    }

    private String genericHandler() {
        return "generic";
    }

    private String ioHandler() {
        return "io";
    }

    private String fileHandler() {
        return "file";
    }

    private String runtimeHandler() {
        return "runtime";
    }
}
