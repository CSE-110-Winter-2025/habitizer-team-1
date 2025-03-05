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
}
