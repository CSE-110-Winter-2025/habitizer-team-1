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
    private List<Task> tasks = new ArrayList<>(); // Ensure it's never null
    private TotalTimer totalTimer; // Reference to TotalTimer

    public Routine(int id, @NonNull String name) {
        this.id = id;
        this.name = name;
        this.tasks = new ArrayList<>();
        this.totalTimer = new TotalTimer(this); // Initialize TotalTimer
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

    public TotalTimer getTotalTimer() {
        return totalTimer;
    }


    // creates routine with a new id - used in InMemoryDataSource to avoid duplicate ids
    public Routine withId ( int id){
        return new Routine(id, this.name);
    }

    public void setName(String name) {
        this.name = name;
    }
    public void startRoutine () {
        totalTimer.start();
    }


    /**
     * Checks whether all tasks in the routine are completed.
     * If all tasks are done, the timer is stopped, and the listener is notified.
     */

    public void checkTasksCompleted() {
        System.out.println("Checking if all tasks are complete...");

        if (!tasks.isEmpty() && tasks.stream().allMatch(Task::complete)) {
            totalTimer.stop(); // Ensure this is executed
            TotalTimer.TimerListener listener = totalTimer.getListener();
            if (listener != null) {
                listener.onRoutineCompleted(totalTimer.getSecondsElapsed(), TotalTimer.formatTime(totalTimer.getSecondsElapsed()));
            }
        }
    }

    public boolean allTasksCompleted(){
        boolean allDone = true;
        for(Task task : this.getTasks()) {
            if(!task.complete()) {
                allDone = false;
                return allDone;
            }
        }
        return allDone;
    }


}

