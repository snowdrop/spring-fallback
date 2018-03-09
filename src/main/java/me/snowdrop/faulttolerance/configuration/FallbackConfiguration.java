package me.snowdrop.faulttolerance.configuration;

import me.snowdrop.faulttolerance.interceptor.fallback.FallbackOperationsInterceptor;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FallbackConfiguration extends AbstractConfiguration<Fallback> {

    @Override
    protected Class<Fallback> getAnnotationClass() {
        return Fallback.class;
    }

    @Override
    protected Pointcut buildPointcut(Class<Fallback> annotationClass) {
        return new ComposablePointcut((Pointcut) new AnnotationClassOrMethodPointcut(annotationClass));
    }

    @Override
    protected FallbackOperationsInterceptor buildAdvice(BeanFactory beanFactory) {
        return new FallbackOperationsInterceptor(beanFactory);
    }

}
