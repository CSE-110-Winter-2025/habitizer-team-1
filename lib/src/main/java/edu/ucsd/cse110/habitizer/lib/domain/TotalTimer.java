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
        long currentTime = System.currentTimeMillis();
        long lapDuration = (currentTime - lastLapTime) / 1000; // Convert to seconds
        lastLapTime = currentTime; // Update last lap time

        return lapDuration; // Return lap duration for tracking
    }

    /**
     * Manually advances the elapsed time by 30 seconds.
     * Notifies the listener with the updated time.
     */
    public synchronized void advanceTime() {
        secondsElapsed += 30;

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
    public synchronized void togglePause() {
        if (!running) { // Resume the timer if it was paused
            running = true;
            timer = new Timer();

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
        }

        // Notify the listener about pause state change
        if (listener != null) {
            listener.onPauseToggled(!running);
        }
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


    /**
     * Callback interface for UI or other listeners to receive updates.
     */
    public interface TimerListener {
        void onTick(int secondsElapsed, String formattedTime); // Called every second with updated elapsed time
        void onRoutineCompleted(int totalTime, String formattedTime); // Called when all tasks are completed
        void onPauseToggled(boolean isPaused); // Notify when paused/unpaused
    }

    public static String lapformatTime(int totalSeconds) {
        int minutes = (totalSeconds + 59) / 60;

        return minutes + (minutes == 1 ? " minute" : " minutes");

    }

}
