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

        // check ids of evening routine
        List<Task> tasks = dataSource.getRoutineTasks(1);
        assertEquals("Charge devices", tasks.get(0).title());
        assertEquals((Integer) 7, tasks.get(0).id());
        assertEquals("Make dinner", tasks.get(1).title());

        assertEquals("Eat dinner", tasks.get(2).title());
        assertEquals("Wash dishes", tasks.get(3).title());
        assertEquals("Pack bag for morning", tasks.get(4).title());
        assertEquals("Homework", tasks.get(5).title());
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
        // add task by adding it to data source first
        Task newTask = new Task(25, "Exercise");
        dataSource.addTask(newTask);
        dataSource.addTaskToRoutine(1, newTask); // Add to Evening Routine

        List<Task> tasks = dataSource.getRoutineTasks(1);
        assertTrue(tasks.contains(newTask));

        // add task without adding it to data source
        Task newTask2 = new Task(15, "Brush Teeth");
        dataSource.addTaskToRoutine(1, newTask2); // Add to Evening Routine
        assertTrue(tasks.contains(newTask2));


        // Check correct task order with new task
        assertEquals("Charge devices", tasks.get(0).title());
        assertEquals("Make dinner", tasks.get(1).title());
        assertEquals("Eat dinner", tasks.get(2).title());
        assertEquals("Wash dishes", tasks.get(3).title());
        assertEquals("Pack bag for morning", tasks.get(4).title());
        assertEquals("Homework", tasks.get(5).title());
        assertEquals("Exercise", tasks.get(6).title());
        assertEquals("Brush Teeth", tasks.get(7).title());
        assertEquals((Integer) 25, tasks.get(6).id());
        assertEquals("Exercise", dataSource.getTaskById(25).title());
    }

    @Test
    public void testAddTaskToRoutineWithNull() {
        // add task by adding it to data source first
        Task newTask = new Task(null, "Exercise");
        newTask = dataSource.addTask(newTask);
        dataSource.addTaskToRoutine(1, newTask); // Add to Evening Routine

        List<Task> tasks = dataSource.getRoutineTasks(1);
        assertTrue(tasks.contains(newTask));

        assertEquals("Exercise", tasks.get(6).title());
        assertEquals((Integer) 13, tasks.get(6).id());
    }

    @Test
    public void testGetRoutines() {
        List<Routine> routines = dataSource.getRoutines();
        assertEquals("Morning", routines.get(0).getName());
        assertEquals((Integer) 0, routines.get(0).id());

        assertEquals("Evening", routines.get(1).getName());
        assertEquals((Integer) 1, routines.get(1).id());
    }
    
}
