package edu.ucsd.cse110.habitizer.lib.domain;

import java.util.List;
import edu.ucsd.cse110.observables.Subject;

public interface SimpleRoutineRepository {
    List<Routine> getRoutines();

    // gets task objects from routine
    List<Task> getRoutineTasks(int routineId);

    Subject<Routine> getRoutineByIdAsSubject(int routineId);

    void renameTask(int routineId, Task task, String newName);

    Routine getRoutineById(int routineId);

    // adds task objects to routine
    void addTaskToRoutine(int routineId, Task task);

    void markTaskComplete(Task task);

    void resetRoutine(int routineId);

    int addRoutine(Routine routine);

    void deleteRoutine(int routineId);

    void updateRoutineTimeEstimate(int routineId, Integer newTimeEstimate);

    public void updateRoutineName(int id, String newRoutineName);
}
