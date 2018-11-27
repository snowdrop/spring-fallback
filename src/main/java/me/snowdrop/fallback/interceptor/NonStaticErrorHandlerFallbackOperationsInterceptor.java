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
