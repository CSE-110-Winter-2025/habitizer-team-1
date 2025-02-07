package edu.ucsd.cse110.habitizer.lib.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import java.util.List;
import java.util.Optional;

public class RoutineTest {
    private Routine routine;

    @Before
    public void setUp() {
        routine = new Routine(1, "Morning Routine");
    }

    @Test
    public void testRoutineInitialization() {
        assertNotNull(routine);
        assertEquals(Optional.of(1), routine.id());
        assertEquals("Morning Routine", routine.getName());
        assertTrue(routine.getTaskIds().isEmpty()); // Routine should start with no tasks
    }

    @Test
    public void testAddTask() {
        routine.addTask(101); // Adding a task ID

        List<Integer> taskIds = routine.getTaskIds();
        assertEquals(1, taskIds.size());
        assertEquals(Integer.valueOf(101), taskIds.get(0));
    }

    @Test
    public void testMultipleTasks() {
        routine.addTask(101);
        routine.addTask(102);
        routine.addTask(103);

        List<Integer> taskIds = routine.getTaskIds();
        assertEquals(3, taskIds.size());
        assertEquals(Integer.valueOf(101), taskIds.get(0));
        assertEquals(Integer.valueOf(102), taskIds.get(1));
        assertEquals(Integer.valueOf(103), taskIds.get(2));
    }
}

