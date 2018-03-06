package me.snowdrop.fallback.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class NonStaticErrorHandlerFallbackOperationsInterceptor implements MethodInterceptor {

    private final Method targetMethod;
    private final Object handlerObject;

    public NonStaticErrorHandlerFallbackOperationsInterceptor(
            Method targetMethod, Object handlerObject) {
        this.targetMethod = targetMethod;
        this.handlerObject = handlerObject;

        if (Modifier.isPrivate(targetMethod.getModifiers())) {
            targetMethod.setAccessible(true);
        }
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        return HandlerMethodInvokerUtil.invoke(
                targetMethod,
                handlerObject,
                DefaultExecutionContext.fromMethodInvocation(invocation)
        );
    }
}
