package edu.ucsd.cse110.habitizer.lib.domain;

import java.util.List;

public interface SimpleRoutineRepository {
    List<Routine> getRoutines();

    // gets task objects from routine
    List<Task> getRoutineTasks(int routineId);

    Routine getRoutineById(int routineId);

    // adds task objects to routine
    void addTaskToRoutine(int routineId, Task task);

    void markTaskComplete(Task task);

    void resetRoutine(int routineId);
}
