package me.snowdrop.fallback.interceptor;

import me.snowdrop.fallback.ExecutionContext;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

class DefaultExecutionContext implements ExecutionContext {

    private final Method method;
    private final Object[] parameters;

    DefaultExecutionContext(Method method, Object[] parameters) {
        this.method = method;
        this.parameters = parameters;
    }

    static DefaultExecutionContext fromMethodInvocation(MethodInvocation methodInvocation) {
        return new DefaultExecutionContext(methodInvocation.getMethod(), methodInvocation.getArguments());
    }

    public Method getMethod() {
        return method;
    }

    public Object[] getParameters() {
        return parameters;
    }
}
