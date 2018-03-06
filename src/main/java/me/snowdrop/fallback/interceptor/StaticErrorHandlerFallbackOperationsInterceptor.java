package me.snowdrop.fallback.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

public class StaticErrorHandlerFallbackOperationsInterceptor implements MethodInterceptor {

    private final Method targetMethod;

    public StaticErrorHandlerFallbackOperationsInterceptor(Method targetMethod) {
        this.targetMethod = targetMethod;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        return HandlerMethodInvokerUtil.invoke(
                targetMethod,
                null,
                DefaultExecutionContext.fromMethodInvocation(invocation)
        );
    }
}
