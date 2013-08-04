package com.github.mvollebregt.reactivejava;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static com.github.mvollebregt.reactivejava.EventSource.*;
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
        Observer ob = observe(eventSource, x -> out.println(x));
        // when we dispose the observer
        ob.dispose();
        // and raise an event
        eventSource.raise(3);
        // then the observer is not called
        assertEquals("", printBuffer.toString("UTF-8"));
    }



    private class Event { public String toString() { return "Event"; }}
    private class MoreSpecificEvent extends Event { public String toString() { return "MoreSpecificEvent"; }}

    @Test
    public void merge_bothEventsComeTrough() throws Exception {
        // given two event sources
        EventSource<Event> es1 = new EventSource<>();
        EventSource<MoreSpecificEvent> es2 = new EventSource<>();
        // and a merged event source
        EventSource<Event> merged = merge(es1, es2);
        observe(merged, x -> out.println(x));
        // when events are raised on both event sources
        es1.raise(new Event());
        es2.raise(new MoreSpecificEvent());
        // then both events are retrieved in the merged source
        assertEquals("Event\nMoreSpecificEvent\n", printBuffer.toString("UTF-8"));
    }

    @Test
    public void map_newEventsComeThrough() throws Exception {
        // given a mapped event source
        EventSource<String> mapped = eventSource.map(x -> "mapped " + x);
        observe(mapped, x -> out.println(x));
        // when raising an event on the original event source
        eventSource.raise(3);
        // then the mapped event source receives a mapped event
        assertEquals("mapped 3\n", printBuffer.toString("UTF-8"));
    }

    @Test
    public void filter_onlyFilteredEventsComeThrough() throws Exception {
        // given a filtered event source
        EventSource<Integer> filtered = eventSource.filter(x -> (x % 2) == 0);
        observe(filtered, x -> out.println(x));
        // when raising two events on the original event source
        eventSource.raise(1);
        eventSource.raise(2);
        // only the filtered event comes through
        assertEquals("2\n", printBuffer.toString("UTF-8"));
    }


}
