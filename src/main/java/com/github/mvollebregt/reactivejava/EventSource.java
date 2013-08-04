package com.github.mvollebregt.reactivejava;

import java.util.ArrayList;

/**
 * @author Michel Vollebregt
 */
public class EventSource<T> {

    private ArrayList<Listener<T>> listeners = new ArrayList<>();

    public interface Listener<T> {
        void handleEvent(T event);
    }

    public void raise(T event) {
        for (Listener<T> listener : listeners) {
            listener.handleEvent(event);
        }
    }

    public static <T> void observe(EventSource<T> eventSource, Listener<T> listener) {
        eventSource.listeners.add(listener);
    }
}
