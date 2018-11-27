/*
 * Copyright (C) 2018 Red Hat inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
