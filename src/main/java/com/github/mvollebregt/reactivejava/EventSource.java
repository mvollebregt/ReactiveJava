package com.github.mvollebregt.reactivejava;

import java.util.ArrayList;

/**
 * @author Michel Vollebregt
 */
public class EventSource<T> {

    private ArrayList<ListenerFunction<T>> listeners = new ArrayList<>();

    public interface Function<A, B> {
        B apply(A param);
    }

    public void raise(T event) {
        for (ListenerFunction<T> listener : listeners) {
            listener.handleEvent(event);
        }
    }

    public <B> EventSource<B> map(Function<T, B> func) {
        EventSource<B> mapped = new EventSource<>();
        observe(this, x -> mapped.raise(func.apply(x)));
        return mapped;
    }

    public static <T> Observer<T> observe(EventSource<T> eventSource, ListenerFunction<T> listener) {
        eventSource.listeners.add(listener);
        return new ObserverImpl<>(eventSource, listener);
    }

    public static <T> EventSource<T> merge(EventSource<? extends T>... ess) {
        EventSource<T> merged = new EventSource<>();
        for (EventSource<? extends T> original : ess) {
            observe(original, merged::raise);
            // TODO: make sure everything gets disposed
        }
        return merged;
    }

    private static class ObserverImpl<T> implements Observer<T> {

        private EventSource<T> eventSource;
        private ListenerFunction<T> listener;

        private ObserverImpl(EventSource<T> eventSource, ListenerFunction<T> listener) {
            this.eventSource = eventSource;
            this.listener = listener;
        }

        @Override
        public void dispose() {
            eventSource.listeners.remove(listener);
        }
    }
}
