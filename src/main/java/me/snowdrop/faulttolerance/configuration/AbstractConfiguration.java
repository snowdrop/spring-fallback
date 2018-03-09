package me.snowdrop.faulttolerance.configuration;

import me.snowdrop.faulttolerance.interceptor.AbstractAnnotationAwareOperationsInterceptor;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import javax.annotation.PostConstruct;
import java.lang.annotation.Annotation;

public abstract class AbstractConfiguration<T extends Annotation>
        extends AbstractPointcutAdvisor
        implements BeanFactoryAware {

    private Advice advice;
    private Pointcut pointcut;
    private BeanFactory beanFactory;

    protected abstract Class<T> getAnnotationClass();
    protected abstract AbstractAnnotationAwareOperationsInterceptor<T> buildAdvice(BeanFactory beanFactory);

    protected Pointcut buildPointcut(Class<T> annotationClass) {
        return new ComposablePointcut((Pointcut) new AnnotationClassOrMethodPointcut(annotationClass));
    }

    @PostConstruct
    public void init() {
        this.pointcut = buildPointcut(getAnnotationClass());
        this.advice = buildAdvice(beanFactory);
    }

    @Override
    public final Advice getAdvice() {
        return advice;
    }

    @Override
    public final Pointcut getPointcut() {
        return pointcut;
    }

    @Override
    public final void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

}
