package edu.ucsd.cse110.habitizer.app.ui;

import android.app.Application;
import edu.ucsd.cse110.habitizer.lib.domain.RoutineRepository;

public class HabitizerApplication extends Application {
    private MainViewModel.Factory viewModelFactory;

    @Override
    public void onCreate() {
        super.onCreate();
        RoutineRepository repository = new RoutineRepository();
        viewModelFactory = new MainViewModel.Factory(repository);
    }

    public MainViewModel.Factory getViewModelFactory() {
        return viewModelFactory;
    }
}