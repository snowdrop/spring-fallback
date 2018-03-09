package me.snowdrop.faulttolerance.interceptor;

import me.snowdrop.faulttolerance.DefaultExecutionContext;
import org.aopalliance.intercept.MethodInvocation;
import org.eclipse.microprofile.faulttolerance.ExecutionContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class HandlerMethodInvokerUtil {

    private HandlerMethodInvokerUtil() {}

    public static Object invoke(MethodInvocation invocation, Method targetMethod, Object targetObject)
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
