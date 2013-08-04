package com.github.mvollebregt.reactivejava;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author Michel Vollebregt
 */
public class EventSource<T> {

    private ArrayList<Consumer<T>> listeners = new ArrayList<>();

    public void raise(T event) {
        for (Consumer<T> listener : listeners) {
            listener.accept(event);
        }
    }

    public <B> EventSource<B> map(Function<T, B> func) {
        EventSource<B> mapped = new EventSource<>();
        observe(this, x -> mapped.raise(func.apply(x)));
        return mapped;
    }

    public EventSource<T> filter(Predicate<T> filterExpr) {
        EventSource<T> filtered = new EventSource<>();
        observe(this, x -> {
            if (filterExpr.test(x)) filtered.raise(x);
        });
        return filtered;
    }

    public static <T> Observer<T> observe(EventSource<T> eventSource, Consumer<T> listener) {
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
        private Consumer<T> listener;

        private ObserverImpl(EventSource<T> eventSource, Consumer<T> listener) {
            this.eventSource = eventSource;
            this.listener = listener;
        }

        @Override
        public void dispose() {
            eventSource.listeners.remove(listener);
        }
    }
}
