package edu.ucsd.cse110.habitizer.app.data;

import androidx.room.*;
import java.util.List;

@Dao
public interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insert(TaskEntity task);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insert(List<TaskEntity> tasks);

    @Query("SELECT * FROM tasks WHERE routineId = :routineId ORDER BY position")
    List<TaskEntity>  getTasksForRoutine(int routineId);

    @Query("SELECT * FROM tasks WHERE id =:id")
    TaskEntity find(int id);

    @Query("DELETE FROM tasks WHERE id = :id")
    void delete(int id);

    @Query("DELETE FROM tasks WHERE routineId = :routineId")
    void deleteTasksForRoutine(int routineId);

    @Query("UPDATE tasks SET isComplete = :isComplete WHERE id = :id")
    void updateCompletedState(int id, boolean isComplete);

    @Query("UPDATE tasks SET title = :newTitle WHERE id = :id")
    void updateTitle(int id, String newTitle);

    @Query("UPDATE tasks SET position = :newPosition WHERE id = :id")
    void updatePosition(int id, int newPosition);

    @Query("SELECT COALESCE(MAX(position), -1) + 1 FROM tasks WHERE routineId = :routineId")
    int getNextPosition(int routineId);

    @Query("UPDATE tasks SET position = position - 1 WHERE routineId = :routineId AND position > :oldPosition")
    void updateAllPositionsAfterDelete(int routineId, int oldPosition);

    @Transaction
    @Query("SELECT position FROM tasks WHERE id = :taskId")
    int getPosition(int taskId);
}

