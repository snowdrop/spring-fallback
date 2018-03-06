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

package me.snowdrop.fallback;

import me.snowdrop.fallback.interceptor.NonStaticErrorHandlerFallbackOperationsInterceptor;
import me.snowdrop.fallback.interceptor.StaticErrorHandlerFallbackOperationsInterceptor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.IntroductionInterceptor;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * Interceptor that parses the fallback metadata on the method it is invoking and
 * delegates to an appropriate FallbackOperationsInterceptor.
 *
 */
public class AnnotationAwareFallbackOperationsInterceptor implements IntroductionInterceptor {

	private final Map<Object, Map<Method, MethodInterceptor>> delegatesCache = new HashMap<>();

	@Override
	public boolean implementsInterface(Class<?> intf) {
		return me.snowdrop.fallback.interceptor.Fallback.class.isAssignableFrom(intf);
	}

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		final MethodInterceptor delegate = getDelegate(invocation.getThis(), invocation.getMethod());
		if (delegate != null) {
			return delegate.invoke(invocation);
		}
		else {
			return invocation.proceed();
		}
	}

	private MethodInterceptor getDelegate(Object target, Method method) {
		if (!this.delegatesCache.containsKey(target) || !this.delegatesCache.get(target).containsKey(method)) {
			synchronized (this.delegatesCache) {
				if (!this.delegatesCache.containsKey(target)) {
					this.delegatesCache.put(target, new HashMap<>());
				}
				Map<Method, MethodInterceptor> delegatesForTarget = this.delegatesCache.get(target);
				if (!delegatesForTarget.containsKey(method)) {
                    Fallback fallback = AnnotationUtils.findAnnotation(method, Fallback.class);
					if (fallback == null) {
						fallback = AnnotationUtils.findAnnotation(method.getDeclaringClass(), Fallback.class);
					}
					if (fallback == null) {
						fallback = findAnnotationOnTarget(target, method);
					}
					if (fallback == null) {
						return delegatesForTarget.put(method, null);
					}
					MethodInterceptor delegate;
                    if (fallback.value().equals(void.class)) { //we need to use the fallback method of the target class
                        final Method targetMethod = findTargetMethod(getTargetClass(target), fallback.fallbackMethod());

                        if (targetMethod == null) {
                            throw new IllegalArgumentException(target + " does not contain a method named '" + fallback.fallbackMethod() + "'");
                        }

                        delegate = new NonStaticErrorHandlerFallbackOperationsInterceptor(targetMethod, target);
                    }
                    else {
                        final Method targetMethod = findTargetMethod(fallback.value(), fallback.fallbackMethod());

                        if (targetMethod == null) {
                            throw new IllegalArgumentException(fallback.value() + " does not contain a static method named '" + fallback.fallbackMethod() + "'");
                        }

                        if (Modifier.isStatic(targetMethod.getModifiers())) {
                            delegate = new StaticErrorHandlerFallbackOperationsInterceptor(targetMethod);
                        }
                        else {
                            throw new UnsupportedOperationException("Using arbitrary object handlers is not currently supported");
                        }
                    }
					delegatesForTarget.put(method, delegate);
				}
			}
		}
		return this.delegatesCache.get(target).get(method);
	}

    private Method findTargetMethod(Class fallbackClass, String fallbackMethod) {
        final Method noArgsMethod = ReflectionUtils.findMethod(fallbackClass, fallbackMethod);

        if (null != noArgsMethod) {
            return noArgsMethod;
        }

        return ReflectionUtils.findMethod(fallbackClass, fallbackMethod, ExecutionContext.class);
    }

    private Fallback findAnnotationOnTarget(Object target, Method method) {
		try {
			final Method targetMethod = target.getClass().getMethod(method.getName(), method.getParameterTypes());
            final Fallback fallback = AnnotationUtils.findAnnotation(targetMethod, Fallback.class);
			if (fallback == null) {
				return AnnotationUtils.findAnnotation(targetMethod.getDeclaringClass(), Fallback.class);
			}

			return null;
		}
		catch (Exception e) {
			return null;
		}
	}

    private Class<?> getTargetClass(Object target) {
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(target);
        if (targetClass == null && target != null) {
            targetClass = target.getClass();
        }
        return targetClass;
    }

}
