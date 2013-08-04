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

    public interface Observer<T> {
        void dispose();
    }

    public void raise(T event) {
        for (Listener<T> listener : listeners) {
            listener.handleEvent(event);
        }
    }

    public static <T> Observer<T> observe(EventSource<T> eventSource, Listener<T> listener) {
        eventSource.listeners.add(listener);
        return new ObserverImpl<>(eventSource, listener);
    }

    private static class ObserverImpl<T> implements Observer<T> {

        private EventSource<T> eventSource;
        private Listener<T> listener;

        private ObserverImpl(EventSource<T> eventSource, Listener<T> listener) {
            this.eventSource = eventSource;
            this.listener = listener;
        }

        @Override
        public void dispose() {
            eventSource.listeners.remove(listener);
        }
    }
}
