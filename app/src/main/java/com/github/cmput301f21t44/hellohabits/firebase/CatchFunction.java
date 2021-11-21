package com.github.cmput301f21t44.hellohabits.firebase;

/**
 * Callback for when the operation fails
 */
public interface CatchFunction {
    /**
     * Apply callback
     *
     * @param e Exception thrown
     */
    void apply(Exception e);
}
