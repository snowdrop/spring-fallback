package me.snowdrop.fallback;

import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.ClassUtils;

import java.io.Serializable;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;

public class FallbackPointcut extends StaticMethodMatcherPointcut implements Serializable {

    @Override
    public boolean matches(Method method, Class<?> targetClass) {

        // Don't allow no-public methods as required.
        if (!Modifier.isPublic(method.getModifiers())) {
            return false;
        }

        // The method may be on an interface, but we need attributes from the target class.
        // If the target class is null, the method will be unchanged.
        Method specificMethod = ClassUtils.getMostSpecificMethod(method, targetClass);
        // If we are dealing with method with generic parameters, find the original method.
        specificMethod = BridgeMethodResolver.findBridgedMethod(specificMethod);

        //check if the method is annotated with fallback
        return !getFallbacks(specificMethod).isEmpty();

//        //first check if the method of the target class is annotated
//        if (!getFallbacks(specificMethod).isEmpty()) {
//            return true;
//        }
//
//        //if the method is not annotated, check the if the target class is annotated
//        return !getFallbacks(specificMethod.getDeclaringClass()).isEmpty();
    }

    private Collection<Fallback> getFallbacks(AnnotatedElement annotatedElement) {
        return AnnotatedElementUtils.getAllMergedAnnotations(annotatedElement, Fallback.class);
    }


}
