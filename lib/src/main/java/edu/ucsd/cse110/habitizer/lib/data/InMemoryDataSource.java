package edu.ucsd.cse110.habitizer.lib.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.Task;

public class InMemoryDataSource {
    private int nextRoutineId = 3;  // Start after default routines
    private int nextTaskId = 14;    // Start after default tasks

    private final Map<Integer, Routine> routines = new HashMap<>();
    private final Map<Integer, Task> tasks = new HashMap<>();

    public InMemoryDataSource() {
        loadDefaultData();
    }

    // Default routines and tasks using static lists
    public static final List<Routine> DEFAULT_ROUTINES = List.of(
            new Routine(1, "Morning"),
            new Routine(2, "Evening")
    );

    public static final List<Task> DEFAULT_TASKS = List.of(
            new Task(1, "Shower"),
            new Task(2, "Brush teeth"),
            new Task(3, "Dress"),
            new Task(4, "Make coffee"),
            new Task(5, "Make lunch"),
            new Task(6, "Dinner prep"),
            new Task(7, "Pack bag"),
            new Task(8, "Charge devices"),
            new Task(9, "Make dinner"),
            new Task(10, "Eat dinner"),
            new Task(11, "Wash dishes"),
            new Task(12, "Pack bag for morning"),
            new Task(13, "Homework")
    );

    public void loadDefaultData() {
        for (Task task : DEFAULT_TASKS) {
            addTask(task);
        }

        for (Routine routine : DEFAULT_ROUTINES) {
            addRoutine(routine);
        }

        // Assign task objects to routines
        addTaskToRoutine(1, 1);  // Morning Routine
        addTaskToRoutine(1, 2);
        addTaskToRoutine(1, 3);
        addTaskToRoutine(1, 4);
        addTaskToRoutine(1, 5);
        addTaskToRoutine(1, 6);
        addTaskToRoutine(1, 7);

        addTaskToRoutine(2, 8);  // Evening Routine
        addTaskToRoutine(2, 9);
        addTaskToRoutine(2, 10);
        addTaskToRoutine(2, 11);
        addTaskToRoutine(2, 12);
        addTaskToRoutine(2, 13);
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

    // Get all tasks for a specific routine
    public List<Task> getTasksForRoutine(int routineId) {
        Routine routine = getRoutineById(routineId);
        if (routine == null) return List.of();
        return routine.getTasks();
    }

    public void addRoutine(Routine routine) {
        if (routine.id() == 0) {
            routine = new Routine(nextRoutineId++, routine.getName());
        }
        routines.put(routine.id(), routine);
    }

    public void addTask(Task task) {
        if (task.id() == 0) {
            task = new Task(nextTaskId++, task.title());
        }
        tasks.put(task.id(), task);
    }

    // Adds task object to routine
    public void addTaskToRoutine(int routineId, int taskId) {
        Routine routine = getRoutineById(routineId);
        Task task = getTaskById(taskId);

        if (routine != null && task != null) {
            routine.addTask(task);
        }
    }
}
