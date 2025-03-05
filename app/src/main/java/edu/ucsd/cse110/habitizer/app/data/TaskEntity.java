package edu.ucsd.cse110.habitizer.app.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
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

    public TaskEntity(String title, Integer routineId){
        this.title=title;
        this.routineId=routineId;
        this.isComplete=false;
    }


    public static TaskEntity fromTask(Task task, Integer routineId){
        var entity = new TaskEntity(task.title(), routineId);
        entity.id = task.id();
        entity.isComplete =  task.complete();
        return entity;
    }

    public Task toTask(){
        var task = new Task(id, title);
        task.setComplete(isComplete);
        return task;

    }




}
