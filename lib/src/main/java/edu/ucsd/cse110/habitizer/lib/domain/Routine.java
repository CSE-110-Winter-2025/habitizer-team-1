package edu.ucsd.cse110.habitizer.lib.domain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.List;

public class Routine implements Serializable {
    private final @Nullable Integer id;
    private @NonNull String name;
    private List<Task> tasks;           // store task objects
    private TotalTimer totalTimer;

    public Routine (int id, @NonNull String name) {
        this.id = id;
        this.name = name;
        this.tasks = new ArrayList<Task>();
        this.totalTimer = new TotalTimer(this);
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

    // Start the timer when routine begins
    public void startRoutine() {
        // Reset all tasks to incomplete to prevent an instant stop
        for (Task task : tasks) {
            task.setComplete(false);
        }

        totalTimer.reset(); // Reset time before starting a new routine
        totalTimer.start();
    }

    // Stop timer when routine ends
    public void endRoutine() {
        totalTimer.stop();
    }

    // Get elapsed time
    public int getTotalTime() {
        return totalTimer.getSecondsElapsed();
    }

    public TotalTimer getTotalTimer() {
        return totalTimer;

    // creates routine with a new id - used in InMemoryDataSource to avoid duplicate ids
    public Routine withId(int id) {
        return new Routine(id, this.name);

    }
}
