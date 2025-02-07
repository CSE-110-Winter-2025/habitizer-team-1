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
        this.taskIds = new ArrayList<Integer>();
    }

    public @Nullable Integer id() {
        return id;
    }

    public @NonNull String getName() {
        return name;
    }

    public void addTask(int task) {
        taskIds.add(task);
    }

    public List<Integer> getTaskIds() {
        return taskIds;                     // Returns task IDs
    }

}
