package edu.ucsd.cse110.habitizer.lib.domain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.Objects;

public class Task implements Serializable {
    private final @Nullable Integer id;
    private @NonNull String title; // should it be final? aak in office hours

    private @NonNull Boolean complete;

    private long lapTime;
    private long lastLapTime = 0; //last recorded laptime

    private int position;


    public int getLapTime() {
        return (int)lapTime;
    }

    public void setLapTime(long lapTime) {
        this.lapTime = lapTime;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setLastLapTime(long lastLapTime) {
        this.lastLapTime = lastLapTime;
    }

    public long getLastLapTime() {
        return lastLapTime;
    }

    public Task(@NonNull Integer id, @NonNull String title){
        this.id = id;
        this.title = title;
        this.complete = false; // default creating a task sets it to false
    }

    public @Nullable Integer id() {
        return id;
    }

    public @Nullable String title() {
        return title;
    }

    public  @NonNull Boolean complete() {return complete; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id) && Objects.equals(title, task.title) && Objects.equals(complete, task.complete);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, complete);
    }


    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    // allows you to custom set complete
    public void setComplete(@NonNull Boolean complete) {
        this.complete = complete;
    }

    public Task withId(int id) {
        //Task newTask = new Task(id, this.title);
        //newTask.setComplete(this.complete); // Preserve the complete status
        //return newTask;
        return new Task(id, this.title);
    }
}
