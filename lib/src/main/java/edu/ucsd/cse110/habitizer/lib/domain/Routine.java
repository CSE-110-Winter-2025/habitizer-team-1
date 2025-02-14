package edu.ucsd.cse110.habitizer.lib.domain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Routine implements Serializable {
    private final @Nullable Integer id;
    private @NonNull String name;
    private List<Task> tasks;           // store task objects

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

    public void addTasks(Collection<Task> tasks) {
        this.tasks.addAll(tasks);
    }

    public List<Task> getTasks() {
        return tasks;                     // Returns task object list
    }

    // creates routine with a new id - used in InMemoryDataSource to avoid duplicate ids
    public Routine withId(int id) {
        return new Routine(id, this.name);
    }

    public void setName(String name) {
        this.name = name;
    }
}
