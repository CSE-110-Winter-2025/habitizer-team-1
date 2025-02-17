package edu.ucsd.cse110.habitizer.lib.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
public class RoutineRepositoryTest {
    private RoutineRepository repository;

    private Routine morningRoutine;
    private Routine eveningRoutine;
    private Task task1;
    private Task task2;

    @Before
    public void setUp() {
        repository = new RoutineRepository();
    }

    @Test
    public void testGetAllRoutines() {
        List<Routine> routines = repository.getRoutines();
        assertNotNull(routines);
        assertEquals(2, routines.size()); // Morning and Evening routines should exist
        assertEquals("Morning", routines.get(0).getName());
        assertEquals("Evening", routines.get(1).getName());
    }

    @Test
    public void testGetRoutineTasks() {
        repository = new RoutineRepository();
        List<Task> tasks = repository.getRoutineTasks(0); // Morning routine
        assertNotNull(tasks);
        assertEquals(7, tasks.size()); // Morning routine should have 7 tasks
    }

    @Test
    public void testAddTaskToRoutine() {
        Task newTask = new Task(13, "Read a book");
        repository.addTaskToRoutine(0, newTask); // Add to Morning Routine

        List<Task> tasks = repository.getRoutineTasks(0);
        assertTrue(tasks.contains(newTask));
        assertEquals(8, tasks.size()); // Morning routine should now have 8 tasks
    }

    @Test
    public void testAddTaskToRoutineDuplicate() {
        Task newTask = new Task(13, "Read a book");
        repository.addTaskToRoutine(0, newTask);

        // try to add a duplicate task, not allowed
        repository.addTaskToRoutine(0, newTask);

        List<Task> tasks = repository.getRoutineTasks(0);
        assertTrue(tasks.contains(newTask));

        // the task list size should still be 8
        assertEquals(8, tasks.size());
    }

    @Test
    public void testRetrieveUpdatedRoutineTasks() {
        Task newTask = new Task(13, "Stretch");
        repository.addTaskToRoutine(1, newTask); // Add to Evening Routine

        Routine routine = repository.getRoutineById(1);
        assertNotNull(routine);
        assertTrue(routine.getTasks().contains(newTask));
        assertEquals(7, routine.getTasks().size()); // Ensure task count increased
    }

    @Test
    public void testResetRoutineWithTasks() {
        Routine currRoutine = repository.getRoutineById(0); // get morning routine
        assertNotNull(currRoutine);

        for(Task task : currRoutine.getTasks()) {
            task.setComplete(true); // set all tasks as complete
            assertTrue(task.complete());
        }

        repository.resetRoutine(0);

        for(Task task : currRoutine.getTasks()) {
            assertFalse(task.complete());
        }
    }
}