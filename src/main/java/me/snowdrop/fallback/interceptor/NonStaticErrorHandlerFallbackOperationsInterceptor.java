package me.snowdrop.fallback.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.util.ReflectionUtils;

public class NonStaticErrorHandlerFallbackOperationsInterceptor implements MethodInterceptor {

    private final Class<?> targetClass;
    private final Object handlerObject;
    private final String methodName;

    public NonStaticErrorHandlerFallbackOperationsInterceptor(
            Class<?> targetClass, Object handlerObject, String methodName) {
        this.targetClass = targetClass;
        this.handlerObject = handlerObject;
        this.methodName = methodName;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        return ReflectionUtils.findMethod(targetClass, methodName).invoke(handlerObject);
    }
}
