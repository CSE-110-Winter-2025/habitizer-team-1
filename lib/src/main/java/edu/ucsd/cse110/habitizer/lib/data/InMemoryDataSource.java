package edu.ucsd.cse110.habitizer.lib.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.Task;

public class InMemoryDataSource {
    private int nextRoutineId = 3;      // Starts after default routines
    private int nextTaskId = 14;         // Start after default tasks

    private final Map<Integer, Routine> routines = new HashMap<>();
    private final Map<Integer, Task> tasks = new HashMap<>();

    public InMemoryDataSource() {
        loadDefaultData();
    }

    // Default routines and tasks using static list
    public final static List<Routine> DEFAULT_ROUTINES = List.of(
            new Routine(1, "Morning"),
            new Routine(2, "Evening")
    );

    public static final List<Task> DEFAULT_TASKS = List.of(
            // Morning
            new Task(1, "Shower"),
            new Task(2, "Brush teeth"),
            new Task(3, "Dress"),
            new Task(4, "Make coffee"),
            new Task(5, "Make lunch"),
            new Task(6, "Dinner prep"),
            new Task(7, "Pack bag"),
            // Evening
            new Task(8, "Charge devices"),
            new Task(9, "Make dinner"),
            new Task(10, "Eat dinner"),
            new Task(11, "Wash dishes"),
            new Task(12, "Pack bag for morning"),
            new Task(13, "Homework")
            // Add jammies and brush teeth?
    );

    public void loadDefaultData() {
        for (Task task : DEFAULT_TASKS) {
            addTask(task);
        }

        for (Routine routine : DEFAULT_ROUTINES) {
            addRoutine(routine);
        }

        // assign tasks to routines
        routines.get(1).addTask(1);
        routines.get(1).addTask(2);
        routines.get(1).addTask(3);
        routines.get(1).addTask(4);
        routines.get(1).addTask(5);
        routines.get(1).addTask(6);
        routines.get(1).addTask(7);

        routines.get(2).addTask(8);
        routines.get(2).addTask(9);
        routines.get(2).addTask(10);
        routines.get(2).addTask(11);
        routines.get(2).addTask(12);
        routines.get(2).addTask(13);
    }

    public List<Routine> getAllRoutines() {
        return new ArrayList<>(routines.values());
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public Routine getRoutineById(int routineId) {
        return routines.get(routineId);
    }

    public Task getTaskById(int taskId) {
        return tasks.get(taskId);
    }

    public void addRoutine(Routine routine) {
        routine = new Routine(nextRoutineId++, routine.getName());
        routines.put(routine.id(), routine);
    }

    public void addTask(Task task) {
        task = new Task(nextTaskId++, task.title());
        tasks.put(task.id(), task);
    }
}
