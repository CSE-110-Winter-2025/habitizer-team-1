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

    public List<Routine> getAllRoutines() {
        return dataSource.getAllRoutines();
    }

    public List<Task> getTasksForRoutine(int routineId) {
        Routine routine = getRoutineById(routineId);
        if (routine == null) return List.of();

        return routine.getTaskIds().stream()
                .map(dataSource::getTaskById)
                .collect(Collectors.toList());
    }

    public Routine getRoutineById(int routineId) {
        return dataSource.getAllRoutines().stream()
                .filter(routine -> routine.id() == routineId)
                .findFirst()
                .orElse(null);
    }

    public void addTaskToRoutine(int routineId, Task task) {
        dataSource.addTask(task);  // Save the new task
        Routine routine = getRoutineById(routineId);
        if (routine != null) {
            routine.addTask(task.id()); // Add task ID to routine
        }
    }
}
