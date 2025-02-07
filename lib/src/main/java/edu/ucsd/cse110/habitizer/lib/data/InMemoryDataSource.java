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

    // Default routines and tasks using lists
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

    // load default data
    public void loadDefaultData() {
        for (Task task : DEFAULT_TASKS) {
            tasks.put(task.id(), task);
        }

        for (Routine routine : DEFAULT_ROUTINES) {
            routines.put(routine.id(), routine);
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

    // returns all the current routines stored
    public List<Routine> getAllRoutines() {
        return new ArrayList<>(routines.values());
    }

    // returns all tasks in all routines
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    // return routine object from its id
    public Routine getRoutineById(int routineId) {
        return routines.get(routineId);
    }

    // return task object from its id
    public Task getTaskById(int taskId) {
        return tasks.get(taskId);
    }

    // get all tasks for a specific routine using the routine's id
    public List<Task> getRoutineTasks(int routineId) {
        Routine routine = getRoutineById(routineId);
        if (routine == null) return List.of();
        return routine.getTasks();
    }

    // add routine to list of routines
    public void addRoutine(Routine routine) {
        // prevents duplicates, a routine w/o an id defaults to 0, so create new object
        if (routine.id() == 0) {
            routine = new Routine(nextRoutineId++, routine.getName());
        }
        routines.put(routine.id(), routine);
    }

    // adds task to list of all tasks
    public void addTask(Task task) {
        // prevents duplicates, a task w/o an id defaults to 0, so create new object
        if (task.id() == 0) {
            task = new Task(nextTaskId++, task.title());
        }
        tasks.put(task.id(), task);
    }

    // Adds task object to routine using their ids
    public void addTaskToRoutine(int routineId, int taskId) {
        Routine routine = getRoutineById(routineId);
        Task task = getTaskById(taskId);

        // no null or duplicates
        if (routine != null && task != null && !tasks.containsKey(task)) {
            routine.addTask(task);
        }
    }
}
