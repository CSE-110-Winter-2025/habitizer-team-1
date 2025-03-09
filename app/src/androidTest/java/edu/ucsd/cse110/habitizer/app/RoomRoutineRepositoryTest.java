package edu.ucsd.cse110.habitizer.app;

import static org.junit.Assert.*;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import edu.ucsd.cse110.habitizer.app.data.RoomRoutineRepository;
import edu.ucsd.cse110.habitizer.app.data.RoutineDao;
import edu.ucsd.cse110.habitizer.app.data.RoutineDatabase;
import edu.ucsd.cse110.habitizer.app.data.RoutineEntity;
import edu.ucsd.cse110.habitizer.app.data.TaskDao;
import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.Task;


/*
    US 12, 13, 14, 16 tests
 */
@RunWith(AndroidJUnit4.class)
public class RoomRoutineRepositoryTest {
    private RoutineDatabase database;
    private RoomRoutineRepository repository;
    private RoutineDao routineDao;
    private TaskDao taskDao;

    @Before
    public void setup() {
        Context context = ApplicationProvider.getApplicationContext();
        database = Room.inMemoryDatabaseBuilder(context, RoutineDatabase.class)
                .allowMainThreadQueries() // For testing only
                .build();

        routineDao = database.RoutineDao();
        taskDao = database.taskDao();
        repository = new RoomRoutineRepository(routineDao, taskDao);
    }

    @After
    public void tearDown() {
        database.close();
    }

    @Test
    public void testInsertAndRetrieveRoutine() {
        RoutineEntity routineEntity = new RoutineEntity("Morning Routine 2", 5);
        int routineId = Math.toIntExact(routineDao.insert(routineEntity));

        Routine retrievedRoutine = repository.getRoutineById(routineId);

        assertNotNull(retrievedRoutine);
        assertEquals("Morning Routine 2", retrievedRoutine.getName());
    }

    @Test
    public void testAddTaskToRoutine() {
        RoutineEntity routineEntity = new RoutineEntity("Morning Routine 2", 5);
        int routineId = Math.toIntExact(routineDao.insert(routineEntity));

        Task task = new Task(null, "Brush Teeth");
        repository.addTaskToRoutine(routineId, task);

        List<Task> tasks = repository.getRoutineTasks(routineId);
        assertEquals(1, tasks.size());
        assertEquals("Brush Teeth", tasks.get(0).title());
    }

    @Test
    public void testRenameTask() {
        RoutineEntity routineEntity = new RoutineEntity("Evening Routine 2", 20);
        int routineId = Math.toIntExact(routineDao.insert(routineEntity));

        Task task = new Task(null, "Go to Bed");
        repository.addTaskToRoutine(routineId, task);

        Task savedTask = repository.getRoutineTasks(routineId).get(0);
        assertNotNull(savedTask);

        repository.renameTask(routineId, savedTask, "Go to Sleep");

        List<Task> updatedTasks = repository.getRoutineTasks(routineId);
        assertEquals("Go to Sleep", updatedTasks.get(0).title());
    }

    @Test
    public void testMarkTaskComplete() {
        RoutineEntity routineEntity = new RoutineEntity("Evening Routine 2", 5);
        int routineId = Math.toIntExact(routineDao.insert(routineEntity));

        Task task = new Task(null, "Read a Book");
        repository.addTaskToRoutine(routineId, task);

        Task savedTask = repository.getRoutineTasks(routineId).get(0);
        repository.markTaskComplete(savedTask);

        Task completedTask = repository.getRoutineTasks(routineId).get(0);

        assertTrue(completedTask.complete());
    }
    
    @Test
    public void testGetRoutines() {
        routineDao.insert(new RoutineEntity("Routine 1", 25));
        routineDao.insert(new RoutineEntity("Routine 2", 35));

        List<Routine> routines = repository.getRoutines();

        // 4 because of default routines
        assertEquals(4, routines.size());
    }

    /* US13 - add routine bdd*/
    @Test
    public void us13bddadd() {
        // Given: Initial count of routines
        List<Routine> initialRoutines = repository.getRoutines();
        int initialSize = initialRoutines.size();

        // When: Adding a new routine (default name is "New Routine")
        Routine newRoutine = new Routine(null, "New Routine");
        int newRoutineId = repository.addRoutine(newRoutine);
        Routine savedRoutine = repository.getRoutineById(newRoutineId);

        // Then: The routine should be saved and retrievable
        assertNotNull(savedRoutine);
        assertEquals("New Routine", savedRoutine.getName());

        // And: The total number of routines should increase
        List<Routine> updatedRoutines = repository.getRoutines();
        assertEquals(initialSize + 1, updatedRoutines.size());
    }

    /* US13 - rename routine bdd*/
    @Test
    public void us13bddrename() {
        // Given: A routine exists in the database
        RoutineEntity routineEntity = new RoutineEntity("Old Routine Name", null);
        int routineId = Math.toIntExact(routineDao.insert(routineEntity));

        // When: The user renames the routine
        Routine routine = repository.getRoutineById(routineId);
        assertNotNull(routine);
        repository.updateRoutineName(routineId, "Updated Routine Name");

        // Then: The routine name should be updated in the database
        Routine updatedRoutine = repository.getRoutineById(routineId);
        assertNotNull(updatedRoutine);
        assertEquals("Updated Routine Name", updatedRoutine.getName());
    }


    @Test
    public void testDeleteTask() {
        RoutineEntity routineEntity = new RoutineEntity("Sleep Routine", 45);
        int routineId = Math.toIntExact(routineDao.insert(routineEntity));
        Task task = new Task(2, "Brush teeth");
        repository.addTaskToRoutine(routineId, task);

        assertTrue(taskDao.getTasksForRoutine(routineId).stream()
                .anyMatch(task1 -> task1.title.equals("Brush teeth")));

        repository.removeTaskFromRoutine(routineId, task);

        assertFalse(taskDao.getTasksForRoutine(routineId).stream()
                .anyMatch(task1 -> task1.title.equals("Brush teeth")));
    }

    @Test
    public void testDeleteTaskObservables() {
        RoutineEntity routineEntity = new RoutineEntity("Sleep Routine", 45);
        int routineId = Math.toIntExact(routineDao.insert(routineEntity));
        Task task = new Task(2, "Brush teeth");

        repository.removeTaskFromRoutine(routineId, task);
        var routineAfter = repository.getRoutineById(routineId);
        assertFalse(routineAfter.getTasks().stream().anyMatch(t -> t != null && t.title() != null && t.title().equals("Brush teeth")));
    }
}
