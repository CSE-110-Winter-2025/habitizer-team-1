package edu.ucsd.cse110.habitizer.lib.domain;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import edu.ucsd.cse110.habitizer.lib.data.InMemoryDataSource;

public class InMemoryDataSourceTest {
    private InMemoryDataSource dataSource;

    @Before
    public void setUp() {
        dataSource = new InMemoryDataSource();
    }

    @Test
    public void testDefaultRoutinesLoaded() {
        List<Routine> routines = dataSource.getAllRoutines();
        assertEquals(2, routines.size()); // Morning and Evening routines
        assertEquals("Morning", routines.get(0).getName());
        assertEquals("Evening", routines.get(1).getName());
    }

    @Test
    public void testDefaultTasksLoaded() {
        List<Task> tasks = dataSource.getAllTasks();
        assertEquals(13, tasks.size()); // Should match the number of predefined tasks
    }

    @Test
    public void testGetRoutineById() {
        Routine routine = dataSource.getRoutineById(1);
        assertNotNull(routine);
        assertEquals("Morning", routine.getName());
    }

    @Test
    public void testGetRoutineTasks() {
        List<Task> tasks = dataSource.getRoutineTasks(1); // Morning routine
        assertEquals(7, tasks.size()); // 7 tasks are in the Morning routine
    }

    @Test
    public void testAddNewTask() {
        Task newTask = new Task(14, "Meditate");
        dataSource.addTask(newTask);

        Task retrievedTask = dataSource.getTaskById(14);
        assertNotNull(retrievedTask);
        assertEquals("Meditate", retrievedTask.title());
    }

    @Test
    public void testAddTaskToRoutine() {
        Task newTask = new Task(15, "Exercise");
        dataSource.addTask(newTask);
        dataSource.addTaskToRoutine(1, 15); // Add to Morning Routine

        List<Task> tasks = dataSource.getRoutineTasks(1);
        assertTrue(tasks.contains(newTask));
    }
}
