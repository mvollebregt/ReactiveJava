package com.github.mvollebregt.reactivejava;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static com.github.mvollebregt.reactivejava.EventSource.observe;
import static org.junit.Assert.assertEquals;

/**
 * @author Michel Vollebregt
 */
public class EventSourceTest {

    private ByteArrayOutputStream printBuffer;
    private PrintStream out;
    private EventSource<Integer> eventSource;

    @Before
    public void setUp() {
        printBuffer = new ByteArrayOutputStream();
        out = new PrintStream(printBuffer);
        eventSource = new EventSource<>();
    }

    @Test
    public void raise_observerIsCalled() throws Exception {
        // given an observer
        observe(eventSource, x -> out.println(x));
        // when we raise an event
        eventSource.raise(2);
        // then the observer is called
        assertEquals("2\n", printBuffer.toString("UTF-8"));
    }

    @Test
    public void dispose_observerIsDisposed() throws Exception {
        // given an observer
        EventSource.Observer ob = observe(eventSource, x -> out.println(x));
        // when we dispose the observer
        ob.dispose();
        // and raise an event
        eventSource.raise(3);
        // then the observer is not called
        assertEquals("", printBuffer.toString("UTF-8"));
    }


}
