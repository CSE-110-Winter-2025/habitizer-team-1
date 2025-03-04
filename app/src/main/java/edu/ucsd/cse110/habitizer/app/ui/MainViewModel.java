package edu.ucsd.cse110.habitizer.app.ui;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.SimpleRoutineRepository;
import edu.ucsd.cse110.habitizer.lib.domain.Task;

public class MainViewModel extends ViewModel {
    private final SimpleRoutineRepository repository;

    public MainViewModel(SimpleRoutineRepository repository) {
        this.repository = repository;
    }

    public List<Routine> getRoutines() {
        return repository.getRoutines();
    }

    public Routine getRoutineById(int id) {
        return repository.getRoutineById(id);
    }

    public void addTaskToRoutine(int routineId, Task task) {
        repository.addTaskToRoutine(routineId, task);
    }

    public void resetRoutine(int routineId) {
        repository.resetRoutine(routineId);
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