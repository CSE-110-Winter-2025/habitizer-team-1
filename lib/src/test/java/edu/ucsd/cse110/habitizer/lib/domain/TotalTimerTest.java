package edu.ucsd.cse110.habitizer.lib.domain;

import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;

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

        // Use a real Routine instead of a mock
        Routine routine = new Routine(1, "Test Routine");
        Task task1 = mock(Task.class);
        Task task2 = mock(Task.class);

        when(task1.complete()).thenReturn(true);
        when(task2.complete()).thenReturn(true);

        routine.addTask(task1);
        routine.addTask(task2);

        doAnswer(invocation -> {
            latch.countDown();
            return null;
        }).when(mockListener).onRoutineCompleted(anyInt(), anyString());

        routine.getTotalTimer().setListener(mockListener);
        routine.startRoutine();

        System.out.println("Timer started? " + routine.getTotalTimer().isRunning());

        routine.checkTasksCompleted(); // Ensure this is triggered!

        boolean timerStopped = latch.await(2, TimeUnit.SECONDS);

        System.out.println("Timer stopped? " + !routine.getTotalTimer().isRunning());

        assertTrue("Timer should have stopped", timerStopped);
        assertFalse("Timer should not be running", routine.getTotalTimer().isRunning());

        verify(mockListener).onRoutineCompleted(anyInt(), anyString());
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
    public void testPauseAndResume() throws InterruptedException {
        totalTimer.start();

        // Wait 2 seconds
        Thread.sleep(2000);

        totalTimer.togglePause(); // Pause the timer
        int pausedTime = totalTimer.getSecondsElapsed();

        // Wait another 2 seconds (timer should remain paused)
        Thread.sleep(2000);

        assertEquals("Time should not advance while paused", pausedTime, totalTimer.getSecondsElapsed());

        totalTimer.togglePause(); // Resume the timer

        // Wait 2 more seconds
        Thread.sleep(2000);

        assertTrue("Time should continue after resume", totalTimer.getSecondsElapsed() > pausedTime);

        totalTimer.stop();
    }

    @Test
    public void testAdvanceTimeWhilePaused() throws InterruptedException {
        totalTimer.start();

        // Wait 2 seconds
        Thread.sleep(2000);

        totalTimer.togglePause(); // Pause the timer
        int pausedTime = totalTimer.getSecondsElapsed();

        totalTimer.advanceTime(); // Manually advance by 30 seconds

        assertEquals("Time should advance even when paused", pausedTime + 30, totalTimer.getSecondsElapsed());

        totalTimer.togglePause(); // Resume the timer

        // Wait 2 more seconds
        Thread.sleep(2000);

        assertTrue("Timer should resume from the new advanced time", totalTimer.getSecondsElapsed() > pausedTime + 30);

        totalTimer.stop();
    }


    @Test
    public void testLapTimer() throws InterruptedException {
        totalTimer.start();

        // Wait for 2 seconds
        Thread.sleep(2000);

        long lap1Duration = totalTimer.recordLap();
        assertTrue("Lap duration should be 2 seconds", lap1Duration >= 1 && lap1Duration <= 3);

        // Wait for 3 seconds
        Thread.sleep(3000);
        long lap2Duration = totalTimer.recordLap();
        assertTrue("Lap duration should be 3 seconds", lap2Duration >= 2 && lap2Duration <= 4);

        totalTimer.stop();
    }

    /*
        User Story 17 Tests - Updating Task time to go by 5 second increments when under a minute
     */

    // Under a minute, 5 second increment tests
    @Test
    public void testLapFormatTimeFiveSec() {
        assertEquals("5 seconds", TotalTimer.lapformatTime(1));
        assertEquals("5 seconds", TotalTimer.lapformatTime(2));
        assertEquals("5 seconds", TotalTimer.lapformatTime(3));
        assertEquals("5 seconds", TotalTimer.lapformatTime(4));
        assertEquals("5 seconds", TotalTimer.lapformatTime(5));

        assertEquals("10 seconds", TotalTimer.lapformatTime(6));
        assertEquals("10 seconds", TotalTimer.lapformatTime(7));
        assertEquals("10 seconds", TotalTimer.lapformatTime(9));
        assertEquals("10 seconds", TotalTimer.lapformatTime(10));

        assertEquals("15 seconds", TotalTimer.lapformatTime(11));
        assertEquals("15 seconds", TotalTimer.lapformatTime(14));
        assertEquals("15 seconds", TotalTimer.lapformatTime(15));

        assertEquals("55 seconds", TotalTimer.lapformatTime(54));
        assertEquals("55 seconds", TotalTimer.lapformatTime(55));
    }

    // Over a minute, rounds up nearest minute
    @Test
    public void testLapFormatTimeMinuteRound() {
        assertEquals("1 minute", TotalTimer.lapformatTime(56));
        assertEquals("1 minute", TotalTimer.lapformatTime(59));
        assertEquals("1 minute", TotalTimer.lapformatTime(60));
        assertEquals("2 minutes", TotalTimer.lapformatTime(61));
        assertEquals("2 minutes", TotalTimer.lapformatTime(119));
        assertEquals("2 minutes", TotalTimer.lapformatTime(120));
        assertEquals("3 minutes", TotalTimer.lapformatTime(121));
    }

    // Exact minute, no rounding
    @Test
    public void testLapFormatTimeExactMinutes() {
        assertEquals("1 minute", TotalTimer.lapformatTime(60));
        assertEquals("2 minutes", TotalTimer.lapformatTime(120));
        assertEquals("3 minutes", TotalTimer.lapformatTime(180));
    }

}
