package edu.ucsd.cse110.habitizer.lib.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.Task;

public class InMemoryDataSource {
    private int nextRoutineId = 2;  // Start after default routines - index 0 based
    private int nextTaskId = 13;    // Start after default tasks - index 0 based

    private final ArrayList<Routine> routines = new ArrayList<>();
    private final ArrayList<Task> tasks = new ArrayList<>();

    // When create the data source, load the default routines and tasks
    public InMemoryDataSource() {
        loadDefaultData();
    }

    // load default data
    public void loadDefaultData() {
        // clear in case of duplicates
        routines.clear();
        tasks.clear();

        // add default tasks to tasks list
        tasks.addAll(List.of(
                new Task(0, "Shower"),
                new Task(1, "Brush teeth"),
                new Task(2, "Dress"),
                new Task(3, "Make coffee"),
                new Task(4, "Make lunch"),
                new Task(5, "Dinner prep"),
                new Task(6, "Pack bag"),
                new Task(7, "Charge devices"),
                new Task(8, "Make dinner"),
                new Task(9, "Eat dinner"),
                new Task(10, "Wash dishes"),
                new Task(11, "Pack bag for morning"),
                new Task(12, "Homework")
        ));

        // add default routines to routines list
        routines.add(new Routine(0, "Morning"));
        routines.add(new Routine(1, "Evening"));

        // method to assign tasks to routines
        assignDefaultTasks();
    }

    // used to assign the default tasks to routines
    private void assignDefaultTasks() {
        // Clear existing tasks to prevent duplicates
        routines.get(0).getTasks().clear();
        routines.get(1).getTasks().clear();

        // first 7 tasks (ids 0-6) are morning routine
        for (int i = 0; i < 7; i++) {
            routines.get(0).addTask(getTaskById(i));
        }

        // task ids 7-12 are evening routine
        for (int i = 7; i < 13; i++) {
            routines.get(1).addTask(getTaskById(i));
        }
    }

    // returns all the current routines stored
    public List<Routine> getRoutines() {
        return new ArrayList<>(routines);
    }

    // returns all tasks in all routines
    public List<Task> getTasks() {
        return new ArrayList<>(tasks);
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
        routines.add(new Routine(nextRoutineId++, routine.getName()));
    }

    // adds task to list of all tasks
    public void addTask(Task task) {
        tasks.add(new Task(nextTaskId++, task.title()));
    }

    // Adds task object to routine using their ids
    public void addTaskToRoutine(int routineId, Task task) {
        Routine routine = getRoutineById(routineId);

        // no null or duplicates
        if (routine != null && task != null && !tasks.contains(task)) {
            routine.addTask(task);
        }
    }
}
