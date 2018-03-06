package me.snowdrop.fallback.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.util.ReflectionUtils;

public class StaticErrorHandlerFallbackOperationsInterceptor implements MethodInterceptor {

    private final Class<?> targetClass;
    private final String methodName;

    public StaticErrorHandlerFallbackOperationsInterceptor(Class<?> targetClass, String methodName) {
        this.targetClass = targetClass;
        this.methodName = methodName;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        return ReflectionUtils.findMethod(targetClass, methodName).invoke(null);
    }
}
