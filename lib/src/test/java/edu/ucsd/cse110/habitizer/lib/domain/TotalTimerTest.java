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
    public void testPauseAndResumeByStopButton() throws InterruptedException {
        totalTimer.start();
        Thread.sleep(2000);

        totalTimer.togglePause(true); // Paused by button_stop
        int pausedTime = totalTimer.getSecondsElapsed();

        Thread.sleep(2000);
        assertEquals("Time should not advance while paused by button_stop", pausedTime, totalTimer.getSecondsElapsed());

        totalTimer.togglePause(true); // Resume
        Thread.sleep(2000);
        assertTrue("Time should continue after resume", totalTimer.getSecondsElapsed() > pausedTime);

        totalTimer.stop();
    }

    @Test
    public void testPauseByTestPauseOnly() throws InterruptedException {
        totalTimer.start();
        Thread.sleep(2000);

        totalTimer.togglePause(false); // Paused by TestPause
        int pausedTime = totalTimer.getSecondsElapsed();

        Thread.sleep(2000);
        assertEquals("Time should not advance while paused by TestPause", pausedTime, totalTimer.getSecondsElapsed());

        // Try resuming with TestPause (should NOT resume)
        totalTimer.togglePause(false);
        Thread.sleep(2000);

        assertEquals("Time should still not advance after pressing TestPause again", pausedTime, totalTimer.getSecondsElapsed());

        // Use Stop Button to verify resuming
        totalTimer.togglePause(true); // Resume using stop button
        Thread.sleep(2000);
        assertTrue("Timer should continue after resuming with Stop button", totalTimer.getSecondsElapsed() > pausedTime);

        totalTimer.stop();
    }



    @Test
    public void testAdvanceTimeBlockedByStopButton() throws InterruptedException {
        totalTimer.start();
        Thread.sleep(2000);

        totalTimer.togglePause(true); // Paused by button_stop
        int pausedTime = totalTimer.getSecondsElapsed();

        totalTimer.advanceTime();
        assertEquals("Time should not advance when paused by button_stop", pausedTime, totalTimer.getSecondsElapsed());

        totalTimer.togglePause(true); // Resume
        Thread.sleep(2000);
        assertTrue("Time should resume normally", totalTimer.getSecondsElapsed() > pausedTime);

        totalTimer.stop();
    }

    @Test
    public void testAdvanceTimeAllowedByTestPause() throws InterruptedException {
        totalTimer.start();
        Thread.sleep(2000);

        totalTimer.togglePause(false); // Paused by TestPause
        int pausedTime = totalTimer.getSecondsElapsed();

        totalTimer.advanceTime();
        assertEquals("Time should advance even when paused by TestPause", pausedTime + 15, totalTimer.getSecondsElapsed());

        // Try resuming with TestPause (should NOT resume)
        totalTimer.togglePause(false);
        Thread.sleep(2000);
        assertEquals("Timer should still not resume after pressing TestPause again", pausedTime + 15, totalTimer.getSecondsElapsed());

        // Use Stop Button to verify resuming
        totalTimer.togglePause(true); // Resume using stop button
        Thread.sleep(2000);
        assertTrue("Timer should resume from the new advanced time when Stop button is used", totalTimer.getSecondsElapsed() > pausedTime + 15);

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
        User Story 17 Tests - Updating Task time to go by 5-second increments when under a minute
     */

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

    @Test
    public void testLapFormatTimeExactMinutes() {
        assertEquals("1 minute", TotalTimer.lapformatTime(60));
        assertEquals("2 minutes", TotalTimer.lapformatTime(120));
        assertEquals("3 minutes", TotalTimer.lapformatTime(180));
    }

}
