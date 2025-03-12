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
import edu.ucsd.cse110.habitizer.app.data.RoutineWithTasks;
import edu.ucsd.cse110.habitizer.app.data.TaskDao;
import edu.ucsd.cse110.habitizer.app.data.TaskEntity;
import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.Task;


/*
    US 12, 13, 14, 15, 16, 19 tests
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

    /* US13 - rename routine bdd */
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


    /* us14 tests and bdd */
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
    @Test
    public void deleteRoutineTest() {
        RoutineEntity routineEntity = new RoutineEntity("New Routine", null);
        int routineId = Math.toIntExact(routineDao.insert(routineEntity));

        assertNotNull(repository.getRoutineById(routineId));
        repository.deleteRoutine(routineId);

        assertNull(routineDao.findWithTasks(routineId));
        assertTrue(repository.getRoutineTasks(routineId).isEmpty());
    }

    /* US16 - delete routine bdd */
    @Test
    public void us16bdd() {
        // Given: A routine with tasks is created
        RoutineEntity routineEntity = new RoutineEntity("Sleep Routine", null);
        int routineId = Math.toIntExact(routineDao.insert(routineEntity));

        Task task1 = new Task(null, "Yawn");
        Task task2 = new Task(null, "Go to bed");
        repository.addTaskToRoutine(routineId, task1);
        repository.addTaskToRoutine(routineId, task2);

        assertNotNull(repository.getRoutineById(routineId));
        assertEquals(2, repository.getRoutineTasks(routineId).size());

        // When: The user deletes the routine
        repository.deleteRoutine(routineId);

        // Then: The routine and its tasks should no longer exist in the database
        assertNull(routineDao.findWithTasks(routineId));
        assertTrue(repository.getRoutineTasks(routineId).isEmpty());
    }

    // US 15 reordering tasks
    @Test
    public void testUpdateTaskOrder() {
        // Given: A routine and two tasks
        RoutineEntity routineEntity = new RoutineEntity("Morning Routine", 1);
        int routineId = Math.toIntExact(routineDao.insert(routineEntity));

        Task task1 = new Task(null, "Task A");
        Task task2 = new Task(null, "Task B");

        repository.addTaskToRoutine(routineId, task1);
        repository.addTaskToRoutine(routineId, task2);

        List<Task> tasksBeforeSwap = repository.getRoutineTasks(routineId);
        assertEquals("Task A", tasksBeforeSwap.get(0).title());
        assertEquals("Task B", tasksBeforeSwap.get(1).title());

        // When: The tasks are swapped
        repository.updateTaskOrder(routineId, tasksBeforeSwap.get(0), tasksBeforeSwap.get(1));

        // Then: The order should be reversed
        List<Task> tasksAfterSwap = repository.getRoutineTasks(routineId);
        assertEquals("Task B", tasksAfterSwap.get(0).title());
        assertEquals("Task A", tasksAfterSwap.get(1).title());
    }

    @Test
    public void testSaveTaskCompletionState() {
        RoutineEntity routine = new RoutineEntity("Morning Routine", 30);
        int routineId = Math.toIntExact(routineDao.insert(routine));
        TaskEntity task = new TaskEntity("Brush Teeth", routineId);
        int taskId = Math.toIntExact(taskDao.insert(task));
        taskDao.updateCompletedState(taskId, true, 120);
        TaskEntity updatedTask = taskDao.find(taskId);
        assertNotNull(updatedTask);
        assertTrue(updatedTask.isComplete);
        assertEquals(120, updatedTask.lapTime);
    }

    @Test
    public void testRestoreTaskCompletionState() {
        RoutineEntity routine = new RoutineEntity("Evening Routine", 45);
        int routineId = Math.toIntExact(routineDao.insert(routine));
        TaskEntity task1 = new TaskEntity("Charge devices", routineId);
        TaskEntity task2 = new TaskEntity("Read a book", routineId);
        task1.id = Math.toIntExact(taskDao.insert(task1));
        task2.id = Math.toIntExact(taskDao.insert(task2));
        taskDao.updateCompletedState(task1.id, true, 150);
        RoutineWithTasks routineWithTasks = routineDao.findWithTasks(routineId);
        assertNotNull(routineWithTasks);
        assertEquals(2, routineWithTasks.tasks.size());
        TaskEntity restoredTask1 = routineWithTasks.tasks.get(0);
        TaskEntity restoredTask2 = routineWithTasks.tasks.get(1);
        assertTrue(restoredTask1.isComplete);
        assertEquals(150, restoredTask1.lapTime);
        assertFalse(restoredTask2.isComplete);
        assertEquals(0, restoredTask2.lapTime);
    }

    @Test
    public void testRoutineResetClearsTaskStates() {
        RoutineEntity routine = new RoutineEntity("Workout Routine", 60);
        int routineId = Math.toIntExact(routineDao.insert(routine));
        TaskEntity task1 = new TaskEntity("Warm-up", routineId);
        TaskEntity task2 = new TaskEntity("Push-ups", routineId);
        task1.id = Math.toIntExact(taskDao.insert(task1));
        task2.id = Math.toIntExact(taskDao.insert(task2));
        taskDao.updateCompletedState(task1.id, true, 180);
        taskDao.updateCompletedState(task2.id, true, 200);
        taskDao.resetTasksForRoutine(routineId);
        List<TaskEntity> tasks = taskDao.getTasksForRoutine(routineId);
        for (TaskEntity task : tasks) {
            assertFalse(task.isComplete);
            assertEquals(0, task.lapTime);
        }
    }

    @Test
    public void testPauseAndResumeRoutine() {
        RoutineEntity routine = new RoutineEntity("Study Session", 120);
        int routineId = Math.toIntExact(routineDao.insert(routine));
        routineDao.updateRoutineState(routineId, true, 600);
        RoutineWithTasks activeRoutine = routineDao.getActiveRoutineWithTasks();
        assertNotNull(activeRoutine);
        assertTrue(activeRoutine.routine.isActive);
        assertEquals((Object) activeRoutine.routine.elapsedTime, 600);
    }
}
