package me.snowdrop.faulttolerance.configuration;

import me.snowdrop.faulttolerance.interceptor.AbstractAnnotationAwareOperationsInterceptor;
import me.snowdrop.faulttolerance.interceptor.retry.RetryOperationsInterceptor;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RetryConfiguration extends AbstractConfiguration<Retry>{

    @Override
    protected Class<Retry> getAnnotationClass() {
        return Retry.class;
    }

    @Override
    protected AbstractAnnotationAwareOperationsInterceptor<Retry> buildAdvice(BeanFactory beanFactory) {
        return new RetryOperationsInterceptor(beanFactory);
    }
}
