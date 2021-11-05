package com.github.cmput301f21t44.hellohabits.firebase;

public interface FirebaseTask {
    interface ResultFunction<T> {
        void apply(T result);
    }

    interface ThenFunction {
        void apply();
    }

    interface CatchFunction {
        void apply(Exception e);
    }
}
