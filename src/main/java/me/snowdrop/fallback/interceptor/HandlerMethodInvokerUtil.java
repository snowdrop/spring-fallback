package me.snowdrop.fallback.interceptor;

import me.snowdrop.fallback.ExecutionContext;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

final class HandlerMethodInvokerUtil {

    private HandlerMethodInvokerUtil() {}

    static Object invoke(MethodInvocation invocation, Method targetMethod, Object targetObject)
            throws InvocationTargetException, IllegalAccessException {

        try {
            return invocation.proceed();
        } catch (Throwable throwable) {
            final Class<?>[] parameterTypes = targetMethod.getParameterTypes();
            if (parameterTypes.length == 0) { //target method takes no parameters
                return targetMethod.invoke(targetObject);
            }

            if ((parameterTypes.length == 1) && (ExecutionContext.class.equals(parameterTypes[0]))) {
                return targetMethod.invoke(targetObject, DefaultExecutionContext.fromMethodInvocation(invocation));
            }

            throw new IllegalArgumentException("The target method " + targetMethod + " is not valid");
        }

    }
}
