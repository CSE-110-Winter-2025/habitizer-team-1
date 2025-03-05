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

import edu.ucsd.cse110.habitizer.app.data.RoutineDao;
import edu.ucsd.cse110.habitizer.app.data.RoutineDatabase;
import edu.ucsd.cse110.habitizer.app.data.RoutineEntity;

@RunWith(AndroidJUnit4.class) // Runs on an Android device/emulator
public class RoutineDaoTest {
    private RoutineDatabase db;
    private RoutineDao routineDao;

    @Before
    public void setup() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, RoutineDatabase.class)
                .build();
        routineDao = db.RoutineDao();
    }

    @After
    public void tearDown() {
        db.close();
    }

    @Test
    public void testInsertAndRetrieveRoutine() {
        RoutineEntity testRoutine = new RoutineEntity("Morning 2", 5);
        long routineId = routineDao.insert(testRoutine);

        RoutineEntity retrievedRoutine = routineDao.find((int) routineId);
        assertNotNull(retrievedRoutine);
        assertEquals("Morning 2", retrievedRoutine.name);
        assertEquals(5, retrievedRoutine.timeEstimate.intValue());
    }
}

