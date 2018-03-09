package me.snowdrop.fallback.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class NonStaticErrorHandlerFallbackOperationsInterceptor implements MethodInterceptor {

    private final Method targetFallbackMethod;
    private final Object handlerObject;

    public NonStaticErrorHandlerFallbackOperationsInterceptor(
            Method targetFallbackMethod, Object handlerObject) {
        this.targetFallbackMethod = targetFallbackMethod;
        this.handlerObject = handlerObject;

        if (Modifier.isPrivate(targetFallbackMethod.getModifiers())) {
            targetFallbackMethod.setAccessible(true);
        }
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        return HandlerMethodInvokerUtil.invoke(invocation, targetFallbackMethod, handlerObject);
    }
}
