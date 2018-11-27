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

import java.lang.reflect.Method;

final class DefaultExecutionContext implements ExecutionContext {

    private final Method method;
    private final Object[] parameters;
    private final Throwable throwable;

    DefaultExecutionContext(Method method, Object[] parameters, Throwable throwable) {
        this.method = method;
        this.parameters = parameters;
        this.throwable = throwable;
    }

    static DefaultExecutionContext fromMethodInvocation(MethodInvocation methodInvocation, Throwable throwable) {
        return new DefaultExecutionContext(methodInvocation.getMethod(), methodInvocation.getArguments(), throwable);
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public Object[] getParameters() {
        return parameters;
    }

    @Override
    public Throwable getThrowable() {
        return throwable;
    }
}
