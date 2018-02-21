package me.snowdrop.fallback;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

@Configuration
public class FallbackConfiguration {

	@Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    BeanFactoryFallbackAdvisor beanFactoryFallbackAdvisor(FallbackAdvice fallbackAdvice) {
	    BeanFactoryFallbackAdvisor advisor = new BeanFactoryFallbackAdvisor();
	    advisor.setAdvice(fallbackAdvice);
	    advisor.setOrder(0);
	    return advisor;
    }

    @Bean
    FallbackAdvice fallbackAdvice() {
	    return new DefaultFallbackAdvice();
    }
}
