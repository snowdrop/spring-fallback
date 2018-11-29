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
package me.snowdrop.fallback;

import me.snowdrop.fallback.interceptor.FallbackInterceptor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Interceptor that parses the fallback metadata on the method it is invoking and
 * delegates to an appropriate FallbackOperationsInterceptor.
 *
 */
public class AnnotationAwareFallbackOperationsInterceptor implements MethodInterceptor {

	private final Map<Object, Map<Method, MethodInterceptor>> methodInterceptorCache = new HashMap<>();

	private final BeanFactory beanFactory;

    public AnnotationAwareFallbackOperationsInterceptor(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		final MethodInterceptor methodInterceptor = getMethodInterceptor(invocation.getThis(), invocation.getMethod());
		if (methodInterceptor != null) {
			return methodInterceptor.invoke(invocation);
		}
		else {
			return invocation.proceed();
		}
	}

	private MethodInterceptor getMethodInterceptor(Object target, Method method) {
		if (!this.methodInterceptorCache.containsKey(target) || !this.methodInterceptorCache.get(target).containsKey(method)) {
			synchronized (this.methodInterceptorCache) {
				if (!this.methodInterceptorCache.containsKey(target)) {
					this.methodInterceptorCache.put(target, new HashMap<>());
				}
				final Map<Method, MethodInterceptor> delegatesForTarget = this.methodInterceptorCache.get(target);
				if (!delegatesForTarget.containsKey(method)) {
                    final Set<Fallback> fallbacks = findAnnotations(method);
					if (fallbacks.isEmpty()) { //if the target does not contain the fallback method, create a cache entry with a null value
						return delegatesForTarget.put(method, null);
					}

                    delegatesForTarget.put(method, doGetMethodInterceptor(target, fallbacks));
				}
			}
		}
		return this.methodInterceptorCache.get(target).get(method);
	}

    private Set<Fallback> findAnnotations(Method method) {
        final Set<Fallback> fallbacks = AnnotationUtils.getRepeatableAnnotations(method, Fallback.class);

        // look up the annotation on the class if none is present on the method
        if(fallbacks.isEmpty()) {
            return AnnotationUtils.getRepeatableAnnotations(method.getDeclaringClass(), Fallback.class);
        }

        return fallbacks;
    }


    private Method findTargetMethod(Class fallbackClass, String fallbackMethod) {
        final Method noArgsMethod = ReflectionUtils.findMethod(fallbackClass, fallbackMethod);

        if (null != noArgsMethod) {
            return noArgsMethod;
        }

        return ReflectionUtils.findMethod(fallbackClass, fallbackMethod, ExecutionContext.class);
    }

    private MethodInterceptor doGetMethodInterceptor(Object target, Set<Fallback> fallbacks) {


        final List<FallbackInterceptor.Configuration> configurationList =
                fallbacks
                        .stream()
                        .map(f -> {
                            final String methodName = getMethodName(f);
                            if (f.value().equals(void.class)) { //we need to use the fallback method of the target class
                                final Method targetFallbackMethod = findTargetMethod(getTargetClass(target), methodName);

                                if (targetFallbackMethod == null) {
                                    throw new IllegalArgumentException(target + " does not contain a method named '" + methodName + "'");
                                }

                                return new FallbackInterceptor.Configuration(targetFallbackMethod, target, f.exception(), f.order());
                            }
                            else {
                                final Method targetFallbackMethod = findTargetMethod(f.value(), methodName);

                                if (targetFallbackMethod == null) {
                                    throw new IllegalArgumentException(
                                            "Class: '" + f.value() + "' does not contain a static method named '"
                                                    + methodName + "'");
                                }

                                if (Modifier.isStatic(targetFallbackMethod.getModifiers())) {  //in this a static method is used
                                    return new FallbackInterceptor.Configuration(targetFallbackMethod, null, f.exception(), f.order());
                                }
                                else { //finally we attempt to find a Spring bean using the class supplied
                                    final Object bean = beanFactory.getBean(f.value());

                                    try {
                                        return new FallbackInterceptor.Configuration(
                                                targetFallbackMethod, bean, f.exception(), f.order());
                                    } catch (BeansException e) {
                                        throw new UnsupportedOperationException("Unable to retrieve bean of Class '"
                                                + f.value() + "' from the ApplicationContext", e);
                                    }


                                }
                            }
                        })
                        .collect(Collectors.toList());

        return new FallbackInterceptor(configurationList);
    }

    private String getMethodName(Fallback fallback) {
        if (StringUtils.startsWithIgnoreCase(fallback.fallbackMethod(), "${")
                && StringUtils.endsWithIgnoreCase(fallback.fallbackMethod(), "}")) {
            return resolveProperty(fallback.fallbackMethod());
        }

        return fallback.fallbackMethod();
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
