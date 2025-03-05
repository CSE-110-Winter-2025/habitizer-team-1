package edu.ucsd.cse110.habitizer.app.ui;

import android.app.Application;
import androidx.room.Room;
import edu.ucsd.cse110.habitizer.app.data.RoutineDatabase;
import edu.ucsd.cse110.habitizer.lib.domain.SimpleRoutineRepository;
import edu.ucsd.cse110.habitizer.app.data.RoomRoutineRepository;

public class HabitizerApplication extends Application {
    private MainViewModel.Factory viewModelFactory;

    @Override
    public void onCreate() {
        super.onCreate();
        var db = Room.databaseBuilder(this, RoutineDatabase.class, "habitizer-database").allowMainThreadQueries().build();

        SimpleRoutineRepository repository = new RoomRoutineRepository(db.RoutineDao(), db.taskDao());
        viewModelFactory = new MainViewModel.Factory(repository);
    }

    public MainViewModel.Factory getViewModelFactory() {
        return viewModelFactory;
    }
}