package edu.ucsd.cse110.habitizer.lib.domain;

import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TaskTimerTest {
    private TotalTimer totalTimer;
    private TotalTimer.TimerListener mockListener;

    @Before
    public void setUp() {
        totalTimer = new TotalTimer(mock(Routine.class));
        mockListener = mock(TotalTimer.TimerListener.class);
        totalTimer.setListener(mockListener);
    }

    @Test
    public void testTimerStartsCorrectly() {
        totalTimer.start();
        assertTrue("Timer should be running", totalTimer.isRunning());
    }

    @Test
    public void testTimerStopsCorrectly() {
        totalTimer.start();
        totalTimer.stop();
        assertFalse("Timer should not be running", totalTimer.isRunning());
    }

    @Test
    public void testTimerResetsCorrectly() {
        totalTimer.start();
        totalTimer.stop();
        totalTimer.reset();
        assertEquals("Timer should be reset to 0 seconds", 0, totalTimer.getSecondsElapsed());
    }

    @Test
    public void testTimerRunsForMultipleSeconds() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(3); // Expecting 3 ticks

        // Mock the listener to confirm ticks
        doAnswer(invocation -> {
            latch.countDown();
            return null;
        }).when(mockListener).onTick(anyInt(), anyString());

        totalTimer.start();

        // Wait until latch counts down (i.e., 3 ticks)
        boolean tickHappened = latch.await(4, TimeUnit.SECONDS);

        assertTrue("Timer should have ticked at least 3 times", tickHappened);
        assertTrue("Timer should be running", totalTimer.isRunning());
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
    public void testTimerAccuracyWithSleep() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(3); // Expecting 3 ticks

        doAnswer(invocation -> {
            latch.countDown(); // Decrease count each time onTick() is called
            return null;
        }).when(mockListener).onTick(anyInt(), anyString());

        totalTimer.start();
        boolean completed = latch.await(4, TimeUnit.SECONDS); // Wait up to 4 seconds

        assertTrue("Timer should have ticked at least 3 times", completed);
        assertTrue("Timer should have counted at least 3 seconds", totalTimer.getSecondsElapsed() >= 3);

        totalTimer.stop();
    }

    @Test
    public void testLapTimer() throws InterruptedException {
        totalTimer.start();

        // Wait for 2 seconds
        Thread.sleep(2000);

        long lap1Duration = totalTimer.recordLap();
        assertTrue("Lap duration should be approximately 2 seconds", lap1Duration >= 1 && lap1Duration <= 3);

        // Wait for another 3 seconds
        Thread.sleep(3000);
        long lap2Duration = totalTimer.recordLap();
        assertTrue("Lap duration should be approximately 3 seconds", lap2Duration >= 2 && lap2Duration <= 4);

        totalTimer.stop();
    }

    @Test
    public void testLapFormatTime() {
        // Testing time format for lap times
        assertEquals("5 seconds", TotalTimer.lapformatTime(1));
        assertEquals("5 seconds", TotalTimer.lapformatTime(4));
        assertEquals("10 seconds", TotalTimer.lapformatTime(6));
        assertEquals("1 minute", TotalTimer.lapformatTime(60));
        assertEquals("2 minutes", TotalTimer.lapformatTime(120));
    }
}
