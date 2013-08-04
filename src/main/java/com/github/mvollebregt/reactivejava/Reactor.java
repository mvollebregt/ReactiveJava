package com.github.mvollebregt.reactivejava;

import java.awt.*;
import java.util.function.*;

import static com.github.mvollebregt.reactivejava.EventSource.observe;

/**
 * @author Michel Vollebregt
 */
public class Reactor {

    private boolean repeating = false;
    private Consumer<Reactor> body;
    private Waiter<?> waiter;

    public static Reactor once(Consumer<Reactor> body) {
        Reactor reactor = new Reactor();
        new Thread(() -> body.accept(reactor)).start();
        return reactor;
    }

    public static Reactor loop(Consumer<Reactor> body) {
        Reactor reactor = new Reactor();
        reactor.body = body;
        reactor.repeating = true;
        new Thread(() -> {
            while (reactor.repeating) {
                body.accept(reactor);
            }
        }).start();
        return reactor;
    }

    public <T> T next(EventSource<T> e) {
        // wait for next event from EventSource e
        Waiter<T> waiter = new Waiter<T>();
        this.waiter = waiter;
        observe(e, waiter::eventHappened);
        synchronized(waiter.synchronizer) {
            try {
                waiter.synchronizer.wait();
            } catch (InterruptedException e1) {
            }
        }
//        if (repeating) body.accept(this); // repeat
        return waiter.event;
    }

    public <T> void loopUntil(EventSource<T> e, Consumer<Reactor> body) {
        // start body in new thread
        Reactor loopReactor = Reactor.loop(body);
        // block this thread until the condition happens
        Waiter<T> waiter = new Waiter<T>();
        observe(e, waiter::eventHappened);
        synchronized (waiter.synchronizer) {
            try {
                waiter.synchronizer.wait();
            } catch (InterruptedException e1) {
            }
        }
        System.out.println("loopUntil");
        loopReactor.stop();
        // resume the thread
    }

    public void stop() {
        repeating = false;
//        synchronized (waiter.synchronizer) {
//            waiter.synchronizer.notify();
//        }
    }

    static class Waiter<T> {
        T event;
        Object synchronizer = new Object();
        void eventHappened(T ev) {
            this.event = ev;
            synchronized(synchronizer) {
                synchronizer.notify();
            }
        }
    }
}
