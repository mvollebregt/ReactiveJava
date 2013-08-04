package com.github.mvollebregt.reactivejava;

/**
* @author Michel Vollebregt
*/
public interface ListenerFunction<T> {
    void handleEvent(T event);
}
