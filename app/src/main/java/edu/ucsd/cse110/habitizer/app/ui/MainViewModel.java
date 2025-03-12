package edu.ucsd.cse110.habitizer.app.ui;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import edu.ucsd.cse110.habitizer.app.data.RoomRoutineRepository;
import edu.ucsd.cse110.habitizer.app.data.RoutineEntity;
import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.SimpleRoutineRepository;
import edu.ucsd.cse110.habitizer.lib.domain.Task;
import edu.ucsd.cse110.observables.Subject;
import edu.ucsd.cse110.observables.PlainMutableSubject;
public class MainViewModel extends ViewModel {
    private final SimpleRoutineRepository repository;

    private final PlainMutableSubject<Boolean> isEditingSubject = new PlainMutableSubject<>(false);

    public MainViewModel(SimpleRoutineRepository repository) {
        this.repository = repository;
    }

    public List<Routine> getRoutines() {
        return repository.getRoutines();
    }
    public Routine getRoutineById(int id) {
        return repository.getRoutineById(id);
    }

    public Subject<Routine> getRoutineByIdAsSubject(int routineId){
        return repository.getRoutineByIdAsSubject(routineId);
    }

    public void addTaskToRoutine(int routineId, Task task) {
        repository.addTaskToRoutine(routineId, task);
    }

    public void renameTask(int routineId, Task task, String newName) {
        repository.renameTask(routineId, task, newName);
    }

    public void resetRoutine(int routineId) {
        repository.resetRoutine(routineId);
    }

    public int addRoutine(Routine routine) {
        return repository.addRoutine(routine);
    }

    public void deleteRoutine(int routineId) {
        repository.deleteRoutine(routineId);
    }

    public void updateRoutineTimeEstimate(int routineId, Integer newTimeEstimate) {
        repository.updateRoutineTimeEstimate(routineId, newTimeEstimate);
    }

    public void removeTaskFromRoutine(int routineId, Task task) {
        repository.removeTaskFromRoutine(routineId, task);
    }

    public void updateRoutineName(Integer id, String newRoutineName) {
        repository.updateRoutineName(id, newRoutineName);
    }
    
    public void updateTaskOrder(int routineId, Task task1, Task task2) {
        repository.updateTaskOrder(routineId, task1, task2);
    }

    public Routine getActiveRoutine() {
        return repository instanceof RoomRoutineRepository
                ? ((RoomRoutineRepository) repository).getActiveRoutine() : null;
    }

    public void updateRoutineState(int routineId, boolean isActive, long elapsedTime, long lastLapTime) {
        if (repository instanceof RoomRoutineRepository) {
            ((RoomRoutineRepository) repository).updateRoutineState(routineId, isActive, elapsedTime, lastLapTime);
        }
    }

    public void markTaskComplete(Task task) {
        if (repository instanceof RoomRoutineRepository) {
            ((RoomRoutineRepository) repository).markTaskComplete(task);
        }
    }

    public static class Factory implements ViewModelProvider.Factory {
        private final SimpleRoutineRepository repository;

        public Factory(SimpleRoutineRepository repository) {
            this.repository = repository;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            return (T) new MainViewModel(repository);
        }
    }
}
