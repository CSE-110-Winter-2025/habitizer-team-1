package edu.ucsd.cse110.habitizer.app;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import edu.ucsd.cse110.habitizer.app.ui.task.TaskFragment;
import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.Task;
import edu.ucsd.cse110.habitizer.lib.domain.TotalTimer;

import java.util.Arrays;

public class LapTimerTest {
    /*
}
    private TaskFragment taskFragment;
    private Routine routine;
    private TotalTimer totalTimer;

    @Before
    public void setUp() {
        // Create a mock routine with tasks
        routine = new Routine(1, "Test Routine");
        routine.addTask(new Task(1, "Task 1"));
        routine.addTask(new Task(2, "Task 2"));

        // Initialize the TaskFragment with the mock routine
        taskFragment = new TaskFragment(routine);
        totalTimer = new TotalTimer(routine);
    }

    @Test
    public void testLapTimerTracksElapsedTime() throws InterruptedException {
        totalTimer.start();
        Thread.sleep(3000); // Simulate 3 seconds passing
        totalTimer.stop();

        int elapsed = totalTimer.getSecondsElapsed();
        assertTrue("Elapsed time should be at least 3 seconds", elapsed >= 3);
    }

    @Test
    public void testMarkTaskCompleteTracksLapTime() {
        Task task = routine.getTasks().get(0);
        totalTimer.start();

        // Simulate time passage
        taskFragment.markTaskComplete(task);
        int lapTime = task.getLapTime();

        assertTrue("Lap time should be greater than 0", lapTime > 0);
    }

    @Test
    public void testAllTasksCompleteStopsTimer() {
        totalTimer.start();

        // Mark all tasks as complete
        for (Task task : routine.getTasks()) {
            taskFragment.markTaskComplete(task);
        }

        assertFalse("Total timer should stop when all tasks are completed", totalTimer.isRunning());
    }
    */
}


