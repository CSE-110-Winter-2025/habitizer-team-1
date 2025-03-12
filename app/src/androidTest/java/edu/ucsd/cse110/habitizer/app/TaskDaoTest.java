package edu.ucsd.cse110.habitizer.app;
import static org.junit.Assert.*;

import android.content.Context;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.*;
import org.junit.runner.RunWith;
import edu.ucsd.cse110.habitizer.app.data.*;

@RunWith(AndroidJUnit4.class)
public class TaskDaoTest {
    private RoutineDatabase db;
    private TaskDao taskDao;
    private RoutineDao routineDao;

    @Before
    public void setup() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, RoutineDatabase.class)
                .allowMainThreadQueries()
                .build();
        taskDao = db.taskDao();
        routineDao = db.RoutineDao();
    }

    @After
    public void tearDown() {
        db.close();
    }

    @Test
    public void testInsertAndRetrieveTask() {
        RoutineEntity routine = new RoutineEntity("Morning 2", 5);
        long routineId = routineDao.insert(routine);

        TaskEntity task = new TaskEntity("Brush teeth", (int) routineId);
        long taskId = taskDao.insert(task);

        TaskEntity retrievedTask = taskDao.find((int) taskId);
        assertNotNull(retrievedTask);
        assertEquals("Brush teeth", retrievedTask.title);
        assertFalse(retrievedTask.isComplete);
    }

    @Test
    public void testUpdateTaskCompletion() {
        RoutineEntity routine = new RoutineEntity("Morning 2", 5);
        long routineId = routineDao.insert(routine);

        TaskEntity task = new TaskEntity("Make coffee", (int) routineId);
        long taskId = taskDao.insert(task);

        taskDao.updateCompletedState((int) taskId, true);
        TaskEntity updatedTask = taskDao.find((int) taskId);

        assertTrue(updatedTask.isComplete);
    }

    @Test
    public void testDeleteTask() {
        RoutineEntity routine = new RoutineEntity("Evening 2", 10);
        long routineId = routineDao.insert(routine);

        TaskEntity task = new TaskEntity("Read book", (int) routineId);
        long taskId = taskDao.insert(task);

        taskDao.delete((int) taskId);
        TaskEntity retrievedTask = taskDao.find((int) taskId);

        assertNull(retrievedTask);
    }

    // US 15 reordering tasks test
    @Test
    public void testSwapTaskPositions() {
        // Insert routine
        RoutineEntity routine = new RoutineEntity("Morning Routine", 1);
        long routineId = routineDao.insert(routine);

        // Insert two tasks
        TaskEntity task1 = new TaskEntity("Task A", (int) routineId, 0);
        TaskEntity task2 = new TaskEntity("Task B", (int) routineId, 1);
        long taskId1 = taskDao.insert(task1);
        long taskId2 = taskDao.insert(task2);

        // Swap positions
        int pos1 = taskDao.getPosition((int) taskId1);
        int pos2 = taskDao.getPosition((int) taskId2);
        taskDao.updatePosition((int) taskId1, pos2);
        taskDao.updatePosition((int) taskId2, pos1);

        // Verify positions are swapped
        int newPos1 = taskDao.getPosition((int) taskId1);
        int newPos2 = taskDao.getPosition((int) taskId2);

        assertEquals(1, newPos1);
        assertEquals(0, newPos2);
    }
}
