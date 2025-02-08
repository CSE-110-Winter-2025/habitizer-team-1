package edu.ucsd.cse110.habitizer.lib.domain;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import java.util.List;
import java.util.Optional;

import edu.ucsd.cse110.habitizer.lib.data.InMemoryDataSource;

public class InMemoryDataSourceTest {
    private InMemoryDataSource dataSource;

    @Before
    public void setUp() {
        dataSource = new InMemoryDataSource();
    }

    @Test
    public void testDefaultRoutinesLoaded() {
        List<Routine> routines = dataSource.getRoutines();
        assertEquals(2, routines.size()); // Morning and Evening routines
        assertEquals("Morning", routines.get(0).getName());
        assertEquals("Evening", routines.get(1).getName());
    }

    @Test
    public void testDefaultTasksLoaded() {
        List<Task> tasks = dataSource.getTasks();
        assertEquals(13, tasks.size()); // Should match the number of predefined tasks
    }

    @Test
    public void testGetRoutineById() {
        Routine routine = dataSource.getRoutineById(0);
        assertNotNull(routine);
        assertEquals("Morning", routine.getName());
    }

    @Test
    public void testGetRoutineTasks() {
        List<Task> tasks = dataSource.getRoutineTasks(0); // Morning routine
        assertEquals(7, tasks.size()); // 7 tasks are in the Morning routine
    }

    @Test
    public void testAddNewTask() {
        Task newTask = new Task(13, "Meditate");
        dataSource.addTask(newTask);

        Task retrievedTask = dataSource.getTaskById(13);
        assertNotNull(retrievedTask);
        assertEquals("Meditate", retrievedTask.title());
    }

    @Test
    public void testAddTaskToRoutine() {
        Task newTask = new Task(15, "Exercise");
        dataSource.addTask(newTask);
        dataSource.addTaskToRoutine(1, newTask); // Add to Morning Routine

        List<Task> tasks = dataSource.getRoutineTasks(1);
        assertTrue(tasks.contains(newTask));
    }

    @Test
    public void testGetRoutines() {
        List<Routine> routines = dataSource.getRoutines();
        assertEquals(routines.get(0).getName(), "Morning");
        assertEquals(routines.get(0).id(), (Integer) 0);

        assertEquals(routines.get(1).getName(), "Evening");
        assertEquals(routines.get(1).id(), (Integer) 1);
    }

    
}
