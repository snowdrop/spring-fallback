/*
 * Copyright 2014-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.snowdrop.faulttolerance.interceptor.fallback;

import me.snowdrop.faulttolerance.interceptor.AbstractAnnotationAwareOperationsInterceptor;
import me.snowdrop.faulttolerance.interceptor.HandlerMethodInvokerUtil;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.eclipse.microprofile.faulttolerance.ExecutionContext;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Interceptor that parses the fallback metadata on the method it is invoking and
 * delegates to an appropriate FallbackOperationsInterceptor.
 *
 */
@Order(Ordered.LOWEST_PRECEDENCE - 100)
public class FallbackOperationsInterceptor extends AbstractAnnotationAwareOperationsInterceptor<Fallback> {

    private static final String DEFAULT_FALLBACK_METHOD_NAME = "handle";

    public FallbackOperationsInterceptor(BeanFactory beanFactory) {
        super(beanFactory);
    }

    @Override
    protected Class<Fallback> getAnnotationClass() {
        return Fallback.class;
    }

    @Override
    protected MethodInterceptor createInterceptor(Object target, Fallback fallback) {
        final String methodName = getMethodName(fallback);

        if (fallback.value().equals(Fallback.DEFAULT.class)) { //we need to use the handle method of the target class
            final Method targetFallbackMethod = findTargetMethod(getTargetClass(target), methodName);

            if (targetFallbackMethod == null) {
                throw new IllegalArgumentException(createMethodDoesNotExistsErrorMessage(target, methodName));
            }

            return new ActualInterceptor(targetFallbackMethod, target);
        }
        else {
            final Method targetFallbackMethod = findTargetMethod(fallback.value(), methodName);

            if (targetFallbackMethod == null) {
                throw new IllegalArgumentException(createMethodDoesNotExistsErrorMessage(fallback.value(), methodName));
            }
            try { //we attempt to find a Spring bean using the class supplied
                return new ActualInterceptor(
                        targetFallbackMethod,
                        beanFactory.getBean(fallback.value())
                );
            } catch (BeansException e) {
                throw new UnsupportedOperationException("Unable to retrieve bean of class " + fallback.value(), e);
            }
        }
    }

    private String getMethodName(Fallback fallback) {
        if (fallback.fallbackMethod().equals("")) {
            return DEFAULT_FALLBACK_METHOD_NAME;
        }

        if (StringUtils.startsWithIgnoreCase(fallback.fallbackMethod(), "${")
                && StringUtils.endsWithIgnoreCase(fallback.fallbackMethod(), "}")) {
            return resolveProperty(fallback.fallbackMethod());
        }

        return fallback.fallbackMethod();
    }

    private Method findTargetMethod(Class fallbackClass, String fallbackMethod) {
        final Method noArgsMethod = ReflectionUtils.findMethod(fallbackClass, fallbackMethod);

        if (null != noArgsMethod) {
            return noArgsMethod;
        }

        return ReflectionUtils.findMethod(fallbackClass, fallbackMethod, ExecutionContext.class);
    }

    private String createMethodDoesNotExistsErrorMessage(Object target, String methodName) {
        return target + " does not contain a method named '" + methodName + "'";
    }

    private static final class ActualInterceptor implements MethodInterceptor {

        private final Method targetFallbackMethod;
        private final Object handlerObject;

        ActualInterceptor(Method targetFallbackMethod, Object handlerObject) {
            this.targetFallbackMethod = targetFallbackMethod;
            this.handlerObject = handlerObject;

            if (Modifier.isPrivate(targetFallbackMethod.getModifiers())) {
                targetFallbackMethod.setAccessible(true);
            }
        }

        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            return HandlerMethodInvokerUtil.invoke(
                    invocation,
                    targetFallbackMethod,
                    handlerObject
            );
        }
    }
}
