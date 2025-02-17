package edu.ucsd.cse110.habitizer.lib.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
                // Morning
                new Task(0, "Shower"),
                new Task(1, "Brush teeth"),
                new Task(2, "Dress"),
                new Task(3, "Make coffee"),
                new Task(4, "Make lunch"),
                new Task(5, "Dinner prep"),
                new Task(6, "Pack bag"),
                // Evening
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

        // first 7 tasks (ids 0-6) are morning routine = size of 7
        for (int i = 0; i < 7; i++) {
            routines.get(0).addTask(getTaskById(i));
        }

        // task ids 7-12 are evening routine = size of 6
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
    public Routine getRoutineById(Integer routineId) {
        for (int i = 0; i < routines.size(); i++) {
            Routine curr = routines.get(i);
            if (routineId.equals(curr.id())) {
                return curr;
            }
        }

        return null;
    }

    // return task object from its id
    public Task getTaskById(Integer taskId) {
        // searches arraylist for task id
        for (int i = 0; i < tasks.size(); i++) {
            Task curr = tasks.get(i);
            if (taskId.equals(curr.id())) {
                return curr;
            }
        }

        // task id not found
        return null;
    }

    // get all tasks for a specific routine using the routine's id
    public List<Task> getRoutineTasks(int routineId) {
        Routine routine = getRoutineById(routineId);
        if (routine == null) {
            return List.of();
        }

        return routine.getTasks();
    }

    // add routine to list of routines
    public Routine addRoutine(Routine routine) {
        // check id and creates new object if invalid
        var newRoutine = preInsertRoutine(routine);
        routines.add(newRoutine);

        // returns new routine object - used for tests
        return newRoutine;
    }

    // adds task to list of all tasks
    public Task addTask(Task task) {
        // checks id and creates new task object if invalid
        var newTask = preInsertTask(task);
        tasks.add(newTask);

        // used for tests
        return newTask;
    }

    // Adds task object to routine using their ids
    public void addTaskToRoutine(int routineId, Task task) {
        Routine routine = getRoutineById(routineId);

        // no null or duplicates - adds task to total task list
        if (routine != null && task != null && !tasks.contains(task)) {
            addTask(task);
        }

        // if routine does not have task, add it
        if (routine != null && !routine.getTasks().contains(task)) {
            routine.addTask(task);
        }
    }

    // private utility method to maintain state of the db: ensures that new tasks inserted have an id
    private Task preInsertTask(Task task) {
        var id = task.id();
        if (id == null) {
            return task.withId(nextTaskId++);
        }
        else if (id > nextTaskId) {
            nextTaskId = id + 1;
        }

        return task;
    }

    // private utility method to maintain state of the db: ensures that new routines inserted have an id
    private Routine preInsertRoutine(Routine routine) {
        var id = routine.id();
        if (id == null) {
            // if routine has no id, then give it one
            return routine.withId(nextRoutineId++);
        }
        else if (id > nextRoutineId) {
            // if the card has an id, update nextRoutine id if necessary to avoid giving out the same one
            // this is important for when we pre-load the default data
            nextRoutineId = id + 1;
        }

        return routine;
    }

}
