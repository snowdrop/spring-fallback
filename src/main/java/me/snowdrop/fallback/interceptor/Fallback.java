package me.snowdrop.fallback.interceptor;

/**
 * Marker interface for proxies that are providing fallback behaviour. Can be added by
 * proxy creators that use the {@link StaticErrorHandlerFallbackOperationsInterceptor} and
 * {@link NonStaticErrorHandlerFallbackOperationsInterceptor}.
 *
 */
public interface Fallback {
}
