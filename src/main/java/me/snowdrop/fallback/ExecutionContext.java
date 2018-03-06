package me.snowdrop.fallback;

import java.lang.reflect.Method;

public interface ExecutionContext {

    /**
     * Returns the method being executed
     *
     */
    Method getMethod();

    /**
     * Returns the parameter values being passed to the method
     *
     */
    Object[] getParameters();
}
