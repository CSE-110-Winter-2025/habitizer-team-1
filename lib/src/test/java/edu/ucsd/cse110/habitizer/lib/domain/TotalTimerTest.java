package edu.ucsd.cse110.habitizer.lib.domain;

import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TotalTimerTest {
    private Routine mockRoutine;
    private TotalTimer totalTimer;
    private TotalTimer.TimerListener mockListener;

    @Before
    public void setUp() {
        mockRoutine = mock(Routine.class);
        when(mockRoutine.getTasks()).thenReturn(java.util.Collections.emptyList()); // Simulate empty task list

        totalTimer = new TotalTimer(mockRoutine);
        mockListener = mock(TotalTimer.TimerListener.class);
        totalTimer.setListener(mockListener);
    }

    @Test
    public void testTimerStartsCorrectly() {
        totalTimer.start();
        assertTrue(totalTimer.isRunning());
    }

    @Test
    public void testTimerStopsCorrectly() {
        totalTimer.start();
        totalTimer.stop();
        assertFalse(totalTimer.isRunning());
    }

    @Test
    public void testTimerResetsCorrectly() {
        totalTimer.start();
        totalTimer.stop();
        totalTimer.reset();
        assertEquals(0, totalTimer.getSecondsElapsed());
    }

    @Test
    public void testTimerRunsForMultipleSeconds() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(3);

        // Update to match new method signature (2 arguments)
        doAnswer(invocation -> {
            latch.countDown();
            return null;
        }).when(mockListener).onTick(anyInt(), anyString());

        totalTimer.start();
        boolean tickHappened = latch.await(4, TimeUnit.SECONDS);

        assertTrue("Timer should have ticked at least 3 times", tickHappened);
        assertTrue("Timer should still be running", totalTimer.isRunning());
        assertTrue("Timer should have counted at least 3 seconds", totalTimer.getSecondsElapsed() >= 3);

        totalTimer.stop();
    }

    @Test
    public void testTimerDoesNotStopImmediately() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        doAnswer(invocation -> {
            latch.countDown();
            return null;
        }).when(mockListener).onTick(anyInt(), anyString());

        totalTimer.start();
        boolean tickHappened = latch.await(2, TimeUnit.SECONDS);

        assertTrue("Timer should have ticked at least once", tickHappened);
        assertTrue("Timer should still be running", totalTimer.isRunning());
        assertTrue("Timer should have counted at least 1 second", totalTimer.getSecondsElapsed() > 0);

        totalTimer.stop();
    }

    @Test
    public void testTimerStopsWhenAllTasksCompleted() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        Task task1 = mock(Task.class);
        Task task2 = mock(Task.class);
        when(task1.complete()).thenReturn(true);
        when(task2.complete()).thenReturn(true);
        when(mockRoutine.getTasks()).thenReturn(java.util.Arrays.asList(task1, task2));

        doAnswer(invocation -> {
            latch.countDown();
            return null;
        }).when(mockListener).onRoutineCompleted(anyInt(), anyString());

        totalTimer.start();
        boolean timerStopped = latch.await(2, TimeUnit.SECONDS);

        assertTrue("Timer should have stopped", timerStopped);
        assertFalse("Timer should not be running", totalTimer.isRunning());

        verify(mockListener).onRoutineCompleted(anyInt(), anyString());
    }
}
