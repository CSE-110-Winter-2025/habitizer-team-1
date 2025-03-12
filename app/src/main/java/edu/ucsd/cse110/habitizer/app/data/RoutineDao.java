package edu.ucsd.cse110.habitizer.app.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;


@Dao
public interface RoutineDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insert(RoutineEntity routine);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insert(List<RoutineEntity> routines);

    @Query("SELECT * FROM routines WHERE id = :id")
    RoutineEntity find(int id);

    @Query("SELECT * FROM routines")
    List<RoutineEntity> findAll();

    @Query("SELECT * FROM routines where id = :id")
    LiveData<RoutineEntity> findAsLiveData(int id);

    @Query("SELECT * FROM routines")
    LiveData<List<RoutineEntity>> findAllAsLiveData();

    @Transaction
    @Query("SELECT * FROM routines WHERE id = :id")
    RoutineWithTasks findWithTasks(int id);

    @Transaction
    @Query("SELECT * FROM routines")
    List<RoutineWithTasks> findAllWithTasks();

    @Transaction
    @Query("SELECT * FROM routines")
    LiveData<List<RoutineWithTasks>> findAllWithTasksLiveData();

    @Query("SELECT COUNT(*) FROM routines")
    int count();

    @Transaction
    default int append(RoutineEntity routine){
        var newRoutine = new RoutineEntity(routine.name, routine.timeEstimate);
        return Math.toIntExact(insert(newRoutine));
    }
    @Query("DELETE FROM routines where id = :id")
    void delete(int id);

    @Query("UPDATE routines SET timeEstimate = :newTimeEstimate WHERE id = :routineId")
    void updateTimeEstimate(int routineId, Integer newTimeEstimate);

    @Query("UPDATE routines SET name = :newName WHERE id = :routineId")
    void updateRoutineName(int routineId, String newName);

    @Transaction
    @Query("SELECT * FROM routines WHERE isActive = 1 LIMIT 1")
    RoutineWithTasks getActiveRoutineWithTasks();

    @Query("UPDATE routines SET isActive = :isActive, elapsedTime = :elapsedTime WHERE id = :id")
    void updateRoutineState(int id, boolean isActive, long elapsedTime);
}
