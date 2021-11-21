package com.github.cmput301f21t44.hellohabits.firebase;

/**
 * Callback for when a new value is returned
 *
 * @param <T> type of Result to pass
 */
public interface ResultFunction<T> {
    /**
     * Apply callback
     *
     * @param result object passed to the callee
     */
    void apply(T result);
}
