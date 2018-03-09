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

import me.snowdrop.fallback.interceptor.DefaultErrorHandlerFallbackOperationsInterceptor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.eclipse.microprofile.faulttolerance.ExecutionContext;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Interceptor that parses the fallback metadata on the method it is invoking and
 * delegates to an appropriate FallbackOperationsInterceptor.
 *
 */
public class AnnotationAwareFallbackOperationsInterceptor implements MethodInterceptor {

    private static final String DEFAULT_FALLBACK_METHOD_NAME = "handle";
    private final Map<Object, Map<Method, MethodInterceptor>> delegatesCache = new HashMap<>();

	private final BeanFactory beanFactory;

    public AnnotationAwareFallbackOperationsInterceptor(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
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
				final Map<Method, MethodInterceptor> delegatesForTarget = this.delegatesCache.get(target);
				if (!delegatesForTarget.containsKey(method)) {
                    final Fallback fallback = findAnnotation(target, method);
					if (fallback == null) { //if the target does not contain the fallback method, create a cache entry with a null value
						return delegatesForTarget.put(method, null);
					}

                    delegatesForTarget.put(method, getDelegate(target, fallback));
				}
			}
		}
		return this.delegatesCache.get(target).get(method);
	}

    private Fallback findAnnotation(Object target, Method method) {
        final Fallback fallbackFromMethod = AnnotationUtils.findAnnotation(method, Fallback.class);

        if (fallbackFromMethod != null) {
            return fallbackFromMethod;
        }

        final Fallback fallbackFromClass = AnnotationUtils.findAnnotation(method.getDeclaringClass(), Fallback.class);
        if (fallbackFromClass != null) {
            return fallbackFromClass;
        }


        return findAnnotationOnTarget(target, method);
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

    private Method findTargetMethod(Class fallbackClass, String fallbackMethod) {
        final Method noArgsMethod = ReflectionUtils.findMethod(fallbackClass, fallbackMethod);

        if (null != noArgsMethod) {
            return noArgsMethod;
        }

        return ReflectionUtils.findMethod(fallbackClass, fallbackMethod, ExecutionContext.class);
    }

    private MethodInterceptor getDelegate(Object target, Fallback fallback) {
        final String methodName = getMethodName(fallback);

        if (fallback.value().equals(Fallback.DEFAULT.class)) { //we need to use the fallback method of the target class
            final Method targetMethod = findTargetMethod(getTargetClass(target), methodName);

            if (targetMethod == null) {
                throw new IllegalArgumentException(createMethodDoesNotExistsErrorMessage(target, methodName));
            }

            return new DefaultErrorHandlerFallbackOperationsInterceptor(targetMethod, target);
        }
        else {
            final Method targetFallbackMethod = findTargetMethod(fallback.value(), methodName);

            if (targetFallbackMethod == null) {
                throw new IllegalArgumentException(fallback.value() + " does not contain a static method named '" + methodName + "'");
            }
            try { //we attempt to find a Spring bean using the class supplied
                return new DefaultErrorHandlerFallbackOperationsInterceptor(
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

    private String createMethodDoesNotExistsErrorMessage(Object target, String methodName) {
        return target + " does not contain a method named '" + methodName + "'";
    }

    /**
     * Resolve the specified value if possible.
     *
     * @see ConfigurableBeanFactory#resolveEmbeddedValue
     */
    private String resolveProperty(String value) {
        if (this.beanFactory != null && this.beanFactory instanceof ConfigurableBeanFactory) {
            return ((ConfigurableBeanFactory) this.beanFactory).resolveEmbeddedValue(value);
        }
        return value;
    }

    private Class<?> getTargetClass(Object target) {
        final Class<?> targetClass = AopProxyUtils.ultimateTargetClass(target);
        if (targetClass != null) {
            return targetClass;
        }

        return target.getClass();
    }

}
