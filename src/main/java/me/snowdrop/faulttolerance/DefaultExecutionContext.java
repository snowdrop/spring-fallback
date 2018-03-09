package me.snowdrop.faulttolerance;

import org.aopalliance.intercept.MethodInvocation;
import org.eclipse.microprofile.faulttolerance.ExecutionContext;

import java.lang.reflect.Method;

public final class DefaultExecutionContext implements ExecutionContext {

    private final Method method;
    private final Object[] parameters;

    DefaultExecutionContext(Method method, Object[] parameters) {
        this.method = method;
        this.parameters = parameters;
    }

    public static DefaultExecutionContext fromMethodInvocation(MethodInvocation methodInvocation) {
        return new DefaultExecutionContext(methodInvocation.getMethod(), methodInvocation.getArguments());
    }

    public Method getMethod() {
        return method;
    }

    public Object[] getParameters() {
        return parameters;
    }
}
