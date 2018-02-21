package me.snowdrop.fallback;

import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;

public class BeanFactoryFallbackAdvisor extends AbstractBeanFactoryPointcutAdvisor {

    private final Pointcut pointcut = new FallbackPointcut();

    @Override
    public Pointcut getPointcut() {
        return pointcut;
    }
}
