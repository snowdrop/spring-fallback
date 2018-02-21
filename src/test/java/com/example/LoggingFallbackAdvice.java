package com.example;

import me.snowdrop.fallback.FallbackAdvice;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class LoggingFallbackAdvice implements FallbackAdvice {

    private final Logger log = LoggerFactory.getLogger(LoggingFallbackAdvice.class);

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        log.info("FallbackAdvice - Before");

        final Object result = invocation.proceed();

        log.info("FallbackAdvice - After");

        return result;
    }
}
