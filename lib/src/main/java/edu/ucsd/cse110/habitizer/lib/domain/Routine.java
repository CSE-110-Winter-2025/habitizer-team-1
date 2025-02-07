package edu.ucsd.cse110.habitizer.lib.domain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Routine implements Serializable {
    private @Nullable Integer id;
    private @NonNull String name;
    private List<Integer> taskIds;          // Store task ids instead of task objects

    public Routine (int id, String name) {
        this.id = id;
        this.name = name;
        this.tasksIds = new ArrayList<>();
    }

    public @Nullable Integer id() {
        return id;
    }

    public @NonNull String getName() {
        return name;
    }

    public void addTask(int taskid) {
        tasksIds.add(taskId);
    }

    public List<Integer> getTaskIds() {
        return taskIds;                     // Returns task IDs
    }

}
