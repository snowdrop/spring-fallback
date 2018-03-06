package me.snowdrop.fallback.interceptor;

import me.snowdrop.fallback.ExecutionContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class HandlerMethodInvokerUtil {

    static Object invoke(Method targetMethod, Object targetObject, ExecutionContext executionContext)
            throws InvocationTargetException, IllegalAccessException {

        final Class<?>[] parameterTypes = targetMethod.getParameterTypes();
        if (parameterTypes.length == 0) { //target method takes no parameters
            return targetMethod.invoke(targetObject);
        }

        if ((parameterTypes.length == 1) && (ExecutionContext.class.equals(parameterTypes[0]))) {
            return targetMethod.invoke(targetObject, executionContext);
        }

        throw new IllegalArgumentException("The target method " + targetMethod + " is not valid");
    }
}
