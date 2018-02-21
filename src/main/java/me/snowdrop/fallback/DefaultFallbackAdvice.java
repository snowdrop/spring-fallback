package me.snowdrop.fallback;

import org.aopalliance.intercept.MethodInvocation;

public class DefaultFallbackAdvice implements FallbackAdvice {

    //TODO need to be passed the Fallback annotation somehow

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        //TODO needs to be implemented
        return invocation.proceed();
    }
}
