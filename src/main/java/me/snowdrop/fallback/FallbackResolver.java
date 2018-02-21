package me.snowdrop.fallback;

import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.ClassUtils;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Collection;

public final class FallbackResolver {

    private FallbackResolver() {}

    public static Collection<Fallback> resolve(Method method, Class<?> targetClass) {
        // The method may be on an interface, but we need attributes from the target class.
        // If the target class is null, the method will be unchanged.
        Method specificMethod = ClassUtils.getMostSpecificMethod(method, targetClass);
        // If we are dealing with method with generic parameters, find the original method.
        specificMethod = BridgeMethodResolver.findBridgedMethod(specificMethod);

        //check if the method is annotated with fallback
        return getFallbacks(specificMethod);
    }

    private static Collection<Fallback> getFallbacks(AnnotatedElement annotatedElement) {
        return AnnotatedElementUtils.getAllMergedAnnotations(annotatedElement, Fallback.class);
    }
}
