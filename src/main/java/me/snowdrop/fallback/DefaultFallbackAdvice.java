package me.snowdrop.fallback;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Collection;

public class DefaultFallbackAdvice implements FallbackAdvice {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        try {
            return invocation.proceed();
        } catch (Exception e){
            final Method method = invocation.getMethod();
            final Object object = invocation.getThis();
            final Class<?> targetClass = getTargetClass(object);

            //TODO cleanup by adding error handling and support for calling multiple handlers
            final Collection<Fallback> fallbacks = FallbackResolver.resolve(method, targetClass);
            return invokeFallback(fallbacks.iterator().next(), targetClass, object);
        }
    }

    private Object invokeFallback(Fallback fallback, Class<?> targetClass, Object object) throws Throwable {
        return ReflectionUtils.findMethod(targetClass, fallback.methodName()).invoke(object);
    }

    private Class<?> getTargetClass(Object target) {
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(target);
        if (targetClass == null && target != null) {
            targetClass = target.getClass();
        }
        return targetClass;
    }
}
