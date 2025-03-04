package edu.ucsd.cse110.habitizer.app.data;

import androidx.room.*;
import java.util.List;

@Dao
public interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insert(TaskEntity task);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insert(List<TaskEntity> tasks);

    @Query("SELECT * FROM tasks WHERE routineId = :routineId")
    List<TaskEntity>  getTasksForRoutine(int routineId);

    @Query("SELECT * FROM tasks WHERE id =:id")
    TaskEntity find(int id);


    @Query("DELETE FROM tasks WHERE id = :id")
    void delete(int id);


    @Query("UPDATE tasks SET isComplete = :isComplete WHERE id = :id")
    void updateCompletedState(int id, boolean isComplete);

}
