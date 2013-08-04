package com.github.mvollebregt.reactivejava;

import static com.github.mvollebregt.reactivejava.EventSource.observe;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * @author Michel Vollebregt
 */
public class EventSourceTest {

    @Test
    public void raise_observerIsCalled() throws Exception {
        // setup
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer);
        // given an event source
        EventSource<Integer> eventSource = new EventSource<>();
        // and an observer
        observe(eventSource, x -> out.println(x));
        // when we raise an event
        eventSource.raise(2);
        // then the observer is called
        assertEquals("2\n", buffer.toString("UTF-8"));
    }

}
