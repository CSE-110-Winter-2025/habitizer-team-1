package edu.ucsd.cse110.habitizer.lib.domain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Routine implements Serializable {
    private @Nullable Integer id;
    private @NonNull String name;
    private List<Task> tasks;          // store task objects

    public Routine (int id, @NonNull String name) {
        this.id = id;
        this.name = name;
        this.tasks = new ArrayList<Task>();
    }

    public @Nullable Integer id() {
        return id;
    }

    public @NonNull String getName() {
        return name;
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    public List<Task> getTasks() {
        return tasks;                     // Returns task object list
    }

}
