package me.snowdrop.fallback;

import org.springframework.aop.support.StaticMethodMatcherPointcut;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class FallbackPointcut extends StaticMethodMatcherPointcut implements Serializable {

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        // Don't allow no-public methods as required.
        if (!Modifier.isPublic(method.getModifiers())) {
            return false;
        }

        return !FallbackResolver.resolve(method, targetClass).isEmpty();
    }

}
