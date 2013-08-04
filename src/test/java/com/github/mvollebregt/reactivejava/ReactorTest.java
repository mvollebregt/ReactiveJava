package com.github.mvollebregt.reactivejava;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * @author Michel Vollebregt
 */
public class ReactorTest {

    private static class Path {

        List<Integer> positions;

        Path(Integer position) {
            positions = new ArrayList<Integer>();
            positions.add(position);
        }

        void lineTo(Integer position) {
            positions.add(position);
            System.out.println("draw " + positions);
        }

        void draw(List<Integer> output) {
            System.out.println("draw " + positions);
            output.addAll(positions);
        }
    }

    private static class MouseEvent {
        MouseEvent(Integer position) { this.position = position; };
        Integer position;
        @Override public String toString() { return String.valueOf(position); }
    }

    @Test
    public void once_react() {
        // given an EventSource
        EventSource<MouseEvent> mouseDown = new EventSource<>();
        EventSource<MouseEvent> mouseUp = new EventSource<>();
        EventSource<MouseEvent> mouseMove = new EventSource<>();
        // and an output
        List<Integer> output = new ArrayList<Integer>();
        // when a reactor is defined
        Reactor.once(self -> {
            // step 1
            MouseEvent me = self.next(mouseDown);
            Path path = new Path(me.position);
            // step 2
            self.loopUntil(mouseUp, x -> {
                System.out.println("accept");
                MouseEvent m = self.next(mouseMove);
                path.lineTo(m.position);
            });
            // step 3
            path.draw(output);
        });
        // and events are emitted
        System.out.println("raise events");
        waitAWhile();
        mouseDown.raise(new MouseEvent(3));
        waitAWhile();
        mouseMove.raise(new MouseEvent(4));
        waitAWhile();
        mouseMove.raise(new MouseEvent(5));
        waitAWhile();
        mouseUp.raise(new MouseEvent(6));
        // then all events are recorded in the reactor
        waitAWhile();
        assertEquals(Arrays.asList(new Integer[] {3, 4, 5}), output);
    }

    private void waitAWhile() {
        try {
            Thread.sleep(1000);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}
