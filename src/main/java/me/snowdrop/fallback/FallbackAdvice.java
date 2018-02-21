package me.snowdrop.fallback;

import org.aopalliance.intercept.MethodInterceptor;

import java.io.Serializable;

public interface FallbackAdvice extends MethodInterceptor, Serializable {
}
