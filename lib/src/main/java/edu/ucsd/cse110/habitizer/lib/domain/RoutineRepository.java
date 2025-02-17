package edu.ucsd.cse110.habitizer.lib.domain;

import edu.ucsd.cse110.habitizer.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.Task;
import java.util.List;
import java.util.stream.Collectors;

public class RoutineRepository {
    private final InMemoryDataSource dataSource;

    public RoutineRepository() {
        this.dataSource = new InMemoryDataSource();
    }

    public List<Routine> getRoutines() {
        return dataSource.getRoutines();
    }

    // gets task objects from routine
    public List<Task> getRoutineTasks(int routineId) {
        Routine routine = getRoutineById(routineId);
        if (routine == null) return List.of();
        return routine.getTasks();
    }

    public Routine getRoutineById(int routineId) {
        return dataSource.getRoutineById(routineId);
    }

    // adds task objects to routine
    public void addTaskToRoutine(int routineId, Task task) {
        dataSource.addTask(task);
        Routine routine = getRoutineById(routineId);
        // check for redundant tasks
        if (routine != null && !routine.getTasks().contains(task)) {
            routine.addTask(task);
        }
    }

    public void resetRoutine(int routineId) {
        Routine routine = getRoutineById(routineId);
        if (routine != null && routine.getTasks() != null) {
            for(Task task : routine.getTasks()){
                task.setComplete(false);
            }

        }
        routine.setEnded(false); // ensure that routine is not ended
    }

}

