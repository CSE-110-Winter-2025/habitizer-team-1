package edu.ucsd.cse110.habitizer.app.ui;

import android.app.Application;
import edu.ucsd.cse110.habitizer.lib.domain.RoutineRepository;

public class HabitizerApplication extends Application {
    private RoutineRepository routineRepository;

    @Override
    public void onCreate() {
        super.onCreate();
        // Use one repository for the duration of running the app
        routineRepository = new RoutineRepository();
    }

    public RoutineRepository getRoutineRepository() {
        return routineRepository;
    }
}
