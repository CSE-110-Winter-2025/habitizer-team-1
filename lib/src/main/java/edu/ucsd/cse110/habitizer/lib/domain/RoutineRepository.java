package edu.ucsd.cse110.habitizer.lib.domain;

import edu.ucsd.cse110.habitizer.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.Task;
import java.util.List;
import java.util.stream.Collectors;

public class RoutineRepository {
    private final InMemoryDataSource dataSource;
    private final LapTimer lapTimer;

    public RoutineRepository() {
        this.dataSource = new InMemoryDataSource();
        this.lapTimer = new LapTimer();
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
        if (routine != null) {
            routine.addTask(task);
        }
    }

    public void markTaskComplete(int taskId) {
        Task task = dataSource.getTaskById(taskId);
        if (task != null && !task.complete()) {
            task.setComplete(true);
            lapTimer.recordLap();
        }
    }

    public List<String> getLapTimes() {
        return lapTimer.getLapTimes();
    }


}

