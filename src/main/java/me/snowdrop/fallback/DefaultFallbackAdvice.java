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

        if (fallback.value().equals(void.class)) {
            return ReflectionUtils.findMethod(targetClass, fallback.fallbackMethod()).invoke(object);
        }

        //TODO implement non-static invocation as well, perhaps on a Spring Bean
        return ReflectionUtils.findMethod(fallback.value(), fallback.fallbackMethod()).invoke(null);
    }

    private Class<?> getTargetClass(Object target) {
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(target);
        if (targetClass == null && target != null) {
            targetClass = target.getClass();
        }
        return targetClass;
    }
}
