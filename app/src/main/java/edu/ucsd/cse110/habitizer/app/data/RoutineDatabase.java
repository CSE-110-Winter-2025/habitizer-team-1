package edu.ucsd.cse110.habitizer.app.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {RoutineEntity.class, TaskEntity.class}, version = 5)
public abstract class RoutineDatabase extends RoomDatabase {
        public abstract RoutineDao RoutineDao();
        public abstract TaskDao taskDao();
    }


