package edu.ucsd.cse110.habitizer.lib.domain;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TotalTimer {
    private Timer timer; // Timer instance to keep track of elapsed time
    private int secondsElapsed = 0; // Counter for elapsed time in seconds
    private boolean running = false; // Flag to track whether the timer is running
    private final Routine routine; // Routine associated with the timer
    private volatile TimerListener listener; // Listener for UI updates (volatile ensures visibility across threads)

    private int lastLap = 0;
    private boolean pausedByStopButton = false; // Flag to track pause source


    private long lastLapTime = 0; // Keeps track of the last lap timestamp

    /**
     * Constructor that initializes the TotalTimer with a given routine.
     * Throws an exception if the routine is null.
     */
    public TotalTimer(Routine routine) {
        if (routine == null) {
            throw new IllegalArgumentException("Routine cannot be null");
        }
        this.routine = routine;
    }

    /**
     * Starts the timer if it is not already running.
     * The timer increments every second and notifies the listener.
     */
    public synchronized void start() {
        if (running) return;

        running = true;
        secondsElapsed = 0;
        lastLapTime = System.currentTimeMillis(); // Record the start time

        timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                synchronized (TotalTimer.this) {
                    if (!running) return;

                    secondsElapsed++;

                    // Notify listener with elapsed time and formatted MM:SS
                    if (listener != null) {
                        listener.onTick(secondsElapsed, formatTime(secondsElapsed));
                    }

                    routine.checkTasksCompleted();
                }
            }
        }, 1000, 1000); // Start after 1 second, repeat every 1 second
    }

    public synchronized long recordLap() {
        int lapTime = secondsElapsed - lastLap;
        lastLap = secondsElapsed;
        return lapTime;
    }

    /**
     * Manually advances the elapsed time by 15 seconds.
     * Notifies the listener with the updated time.
     */
    public synchronized void advanceTime() {
        if (pausedByStopButton) return; // Block time advancement if paused by button_stop

        secondsElapsed += 15;

        // Notify the listener with updated time
        if (listener != null) {
            listener.onTick(secondsElapsed, formatTime(secondsElapsed));
        }
    }

    /**
     * Stops the timer if it is currently running.
     * Cancels the timer and resets the reference.
     */
    public synchronized void stop() {
        if (timer != null) {
            timer.cancel(); // Cancel the ongoing timer task
            timer = null; // Clear the timer reference to prevent reuse
        }
        running = false;
    }

    /**
     * Toggles the timer between pause and resume.
     */
    public synchronized void togglePause(boolean isStopButton) {
        if (!running) { // Resume the timer if it was paused
            if (!isStopButton) return; // Prevent TestPause from resuming

            // Resume only if button_stop was used
            running = true;
            timer = new Timer();
            pausedByStopButton = false; // Reset flag when resuming

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    synchronized (TotalTimer.this) {
                        if (!running) return;

                        secondsElapsed++;

                        if (listener != null) {
                            listener.onTick(secondsElapsed, formatTime(secondsElapsed));
                        }

                        routine.checkTasksCompleted();
                    }
                }
            }, 1000, 1000); // Resume from the same time

        } else { // Pause the timer
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
            running = false;
            pausedByStopButton = isStopButton; // Track if paused by button_stop
        }

        // Notify the listener about pause state change
        if (listener != null) {
            listener.onPauseToggled(!running);
        }
    }

    // Helper method to check if pause was caused by button_stop
    public boolean isPausedByStopButton() {
        return pausedByStopButton;
    }


    /**
     * Resets the timer by stopping it and setting elapsed time to zero.
     * Also notifies the listener about the reset.
     */
    public synchronized void reset() {
        stop(); // Stop the timer before resetting
        secondsElapsed = 0;

        // Notify the listener about the reset time
        if (listener != null) {
            listener.onTick(secondsElapsed, formatTime(secondsElapsed));

        }
    }

    /** Sets the elapsed time counter (used when resuming a paused routine) */
    public synchronized void setSecondsElapsed(int seconds) {
        this.secondsElapsed = seconds;
    }

    /**
     * Returns the total seconds elapsed since the timer started.
     */
    public int getSecondsElapsed() {
        return secondsElapsed;
    }

    /**
     * Checks if the timer is currently running.
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Sets a listener to receive updates on time progress and completion events.
     */
    public synchronized void setListener(TimerListener listener) {
        this.listener = listener;
    }

    public synchronized TimerListener getListener() {
        return listener;
    }


    /**
     * Converts elapsed time in seconds to MM:SS format.
     */
    public static String formatTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    // returns total time
    public synchronized int getTotalTime() {
        return secondsElapsed;
    }


    /**
     * Callback interface for UI or other listeners to receive updates.
     */
    public interface TimerListener {
        void onTick(int secondsElapsed, String formattedTime); // Called every second with updated elapsed time
        void onRoutineCompleted(int totalTime, String formattedTime); // Called when all tasks are completed
        void onPauseToggled(boolean isPaused); // Notify when paused/unpaused
    }

    public static String lapformatTime(int totalSeconds) {
        int time = totalSeconds;

        // if time is under a minute, then round by 5 second increments
        if (time <= 55) {

            // if divisible by 5, then no rounding is needed
            if ((time % 5) != 0) {
                time += 5 - (time % 5);
            }

            return time + (time == 1 ? " seconds" : " seconds");
        } else {
            // minute rounding
            time = (time + 59) / 60;
        }

        return time + (time == 1 ? " minute" : " minutes");

    }

}
