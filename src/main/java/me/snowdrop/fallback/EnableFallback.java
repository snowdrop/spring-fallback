package me.snowdrop.fallback;

import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.AutoProxyRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({FallbackConfiguration.class, AutoProxyRegistrar.class})
public @interface EnableFallback {

    /**
     * Indicate whether subclass-based (CGLIB) proxies are to be created as opposed
     * to standard Java interface-based proxies. The default is {@code false}. <strong>
     * Applicable only if {@link #mode()} is set to {@link AdviceMode#PROXY}</strong>.
     * <p>Note that setting this attribute to {@code true} will affect <em>all</em>
     * Spring-managed beans requiring proxying, not just those marked with {@code @Cacheable}.
     * For example, other beans marked with Spring's {@code @Transactional} annotation will
     * be upgraded to subclass proxying at the same time. This approach has no negative
     * impact in practice unless one is explicitly expecting one type of proxy vs another,
     * e.g. in tests.
     */
    boolean proxyTargetClass() default false;

    /**
     * Indicate how caching advice should be applied.
     * <p><b>The default is {@link AdviceMode#PROXY}.</b>
     * Please note that proxy mode allows for interception of calls through the proxy
     * only. Local calls within the same class cannot get intercepted that way;
     * a caching annotation on such a method within a local call will be ignored
     * since Spring's interceptor does not even kick in for such a runtime scenario.
     * For a more advanced mode of interception, consider switching this to
     * {@link AdviceMode#ASPECTJ}.
     */
    AdviceMode mode() default AdviceMode.PROXY;
}
