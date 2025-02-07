package edu.ucsd.cse110.habitizer.lib.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import java.util.List;


public class RoutineTest {
    private Routine routine;
    private Task task1, task2, task3;

    @Before
    public void setUp() {
        routine = new Routine(1, "Morning Routine");
        task1 = new Task(101, "Wake up");
        task2 = new Task(102, "Brush Teeth");
        task3 = new Task(103, "Eat Breakfast");
    }

    @Test
    public void testRoutineInitialization() {
        assertNotNull(routine);
        assertEquals(0, routine.getTasks().size());
        assertEquals("Morning Routine", routine.getName());
        assertTrue(routine.getTasks().isEmpty()); // Routine should start with no tasks
    }

    @Test
    public void testAddTask() {
        routine.addTask(task1); // Adding a Task object

        List<Task> tasks = routine.getTasks();
        assertEquals(1, tasks.size());
        assertEquals(task1, tasks.get(0)); // Check if the correct Task is stored
    }

    @Test
    public void testAddMultipleTasks() {
        routine.addTask(task1);
        routine.addTask(task2);
        routine.addTask(task3);

        List<Task> tasks = routine.getTasks();
        assertEquals(3, tasks.size());
        assertEquals(task1, tasks.get(0));
        assertEquals(task2, tasks.get(1));
        assertEquals(task3, tasks.get(2));
    }



}

