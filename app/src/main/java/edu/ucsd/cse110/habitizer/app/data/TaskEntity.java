package edu.ucsd.cse110.habitizer.app.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.room.Ignore;
import edu.ucsd.cse110.habitizer.lib.domain.Task;
@Entity(tableName = "tasks")
public class TaskEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public Integer id = null;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "isComplete")
    public boolean isComplete;

    @ColumnInfo(name = "routineId")
    public Integer routineId;

    @ColumnInfo(name = "lapTime")               // Add field to store completion duration
    public long lapTime;

    @ColumnInfo(name = "position")
    public Integer position;

    // Main constructor that Room will use
    public TaskEntity(String title, Integer routineId, Integer position) {
        this.title = title;
        this.routineId = routineId;
        this.lapTime = 0;
        this.isComplete = false;
        this.position = position;
    }

    @Ignore
    public TaskEntity(String title, Integer routineId) {
        this.title = title;
        this.routineId = routineId;
        this.lapTime = 0;
        this.isComplete = false;
        this.position = Integer.MAX_VALUE;
    }

    public static TaskEntity fromTask(Task task, Integer routineId) {
        var entity = new TaskEntity(task.title(), routineId);
        entity.id = task.id();
        entity.isComplete =  task.complete();
        entity.position = task.getPosition();
        entity.lapTime = task.getLapTime();
        return entity;
    }

    public Task toTask() {
        var task = new Task(id, title);
        task.setComplete(isComplete);
        task.setPosition(position);
        task.setLapTime(lapTime);
        return task;
    }
}