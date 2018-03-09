package me.snowdrop.faulttolerance.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractAnnotationAwareOperationsInterceptor<T extends Annotation> implements MethodInterceptor {

    private final Map<Object, Map<Method, MethodInterceptor>> delegatesCache = new HashMap<>();
    protected final BeanFactory beanFactory;

    public AbstractAnnotationAwareOperationsInterceptor(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    protected abstract Class<T> getAnnotationClass();
    protected abstract MethodInterceptor createInterceptor(Object target, T annotation);

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        final MethodInterceptor delegate = getDelegate(invocation.getThis(), invocation.getMethod());
        if (delegate != null) {
            return delegate.invoke(invocation);
        } else {
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
                    final T annotation = findAnnotation(target, method);
                    if (annotation == null) { //if the target does not contain the annotation, create a cache entry with a null value
                        return delegatesForTarget.put(method, null);
                    }

                    delegatesForTarget.put(method, createInterceptor(target, annotation));
                }
            }
        }
        return this.delegatesCache.get(target).get(method);
    }

    private T findAnnotation(Object target, Method method) {
        final T annotationFromMethod = AnnotationUtils.findAnnotation(method, getAnnotationClass());

        if (annotationFromMethod != null) {
            return annotationFromMethod;
        }

        final T annotationFromClass = AnnotationUtils.findAnnotation(method.getDeclaringClass(), getAnnotationClass());
        if (annotationFromClass != null) {
            return annotationFromClass;
        }


        return findAnnotationOnTarget(target, method);
    }

    private T findAnnotationOnTarget(Object target, Method method) {
        try {
            final Method targetMethod = target.getClass().getMethod(method.getName(), method.getParameterTypes());
            final T annotation = AnnotationUtils.findAnnotation(targetMethod, getAnnotationClass());
            if (annotation == null) {
                return AnnotationUtils.findAnnotation(targetMethod.getDeclaringClass(), getAnnotationClass());
            }

            return null;
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * Resolve the specified value if possible.
     *
     * @see ConfigurableBeanFactory#resolveEmbeddedValue
     */
    protected String resolveProperty(String value) {
        if (this.beanFactory != null && this.beanFactory instanceof ConfigurableBeanFactory) {
            return ((ConfigurableBeanFactory) this.beanFactory).resolveEmbeddedValue(value);
        }
        return value;
    }

    protected Class<?> getTargetClass(Object target) {
        final Class<?> targetClass = AopProxyUtils.ultimateTargetClass(target);
        if (targetClass != null) {
            return targetClass;
        }

        return target.getClass();
    }
}
