package me.snowdrop.faulttolerance.interceptor.retry;

import me.snowdrop.faulttolerance.interceptor.AbstractAnnotationAwareOperationsInterceptor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.time.Duration;
import java.util.Arrays;

@Order(Ordered.HIGHEST_PRECEDENCE + 100)
public class RetryOperationsInterceptor extends AbstractAnnotationAwareOperationsInterceptor<Retry> {

    public RetryOperationsInterceptor(BeanFactory beanFactory) {
        super(beanFactory);
    }

    @Override
    protected Class<Retry> getAnnotationClass() {
        return Retry.class;
    }

    @Override
    protected MethodInterceptor createInterceptor(Object target, Retry annotation) {
        return new ActualImplementation(
                new RetryProperties(
                        annotation.maxRetries(),
                        Duration.of(annotation.delay(), annotation.delayUnit()),
                        annotation.retryOn()
                )
        );
    }

    private static class ActualImplementation implements MethodInterceptor {

        private final RetryProperties retryProperties;

        private ActualImplementation(RetryProperties retryProperties) {
            this.retryProperties = retryProperties;
        }

        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            return doInvoke(invocation, 0);
        }

        private Object doInvoke(MethodInvocation invocation, int currentFailureCount)
                throws Throwable{

            try {
                return invocation.proceed();
            } catch (Throwable t) {
                //in this case the exception we caught was not one of the configured retryable exceptions
                if (!shouldRetryForCaughtThrowable(t)) {
                    throw t;
                }

                //in this case we have already reached the configured max retries
                if (currentFailureCount == retryProperties.getMaxRetries()) {
                    throw t;
                }

                sleepForConfiguredDelay();

                //retry the method
                return doInvoke(invocation, currentFailureCount + 1);
            }
        }

        private boolean shouldRetryForCaughtThrowable(Throwable caught) {
            return Arrays.stream(retryProperties.retryOn).anyMatch(t -> t.isAssignableFrom(caught.getClass()));
        }

        private void sleepForConfiguredDelay() {
            final long sleepMillis = retryProperties.getDelayDuration().toMillis();
            try {
                Thread.sleep(sleepMillis);
            } catch (InterruptedException ignored) {}
        }
    }

    private static class RetryProperties {
        private final int maxRetries;
        private final Duration delayDuration;
        private final Class<? extends Throwable>[] retryOn;

        public RetryProperties(int maxRetries, Duration delayDuration, Class<? extends Throwable>[] retryOn) {
            this.maxRetries = maxRetries;
            this.delayDuration = delayDuration;
            this.retryOn = retryOn;
        }

        public int getMaxRetries() {
            return maxRetries;
        }

        public Duration getDelayDuration() {
            return delayDuration;
        }
    }
}
