package com.github.cmput301f21t44.hellohabits.firebase;

/**
 * Interfaces for creating success and fail callbacks
 */
public interface FirebaseTask {
    /**
     * Callback for when a new value is returned
     *
     * @param <T> type of Result to pass
     */
    interface ResultFunction<T> {
        /**
         * Apply callback
         *
         * @param result object passed to the callee
         */
        void apply(T result);
    }

    /**
     * Callback for when no value is returned
     */
    interface ThenFunction {
        /**
         * Apply callback
         */
        void apply();
    }

    /**
     * Callback for when the operation fails
     */
    interface CatchFunction {
        /**
         * Apply callback
         *
         * @param e Exception thrown
         */
        void apply(Exception e);
    }
}
