package edu.ucsd.cse110.habitizer.app.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import edu.ucsd.cse110.habitizer.lib.domain.Routine;

@Entity(tableName = "routines")
public class RoutineEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public Integer id = null;

    @ColumnInfo(name="name")
    public String name;

    @ColumnInfo(name="timeEstimate")
    public Integer timeEstimate;

    @ColumnInfo(name = "isActive")
    public boolean isActive = false;

    @ColumnInfo(name = "elapsedTime")
    public Integer elapsedTime;


    public RoutineEntity(@NonNull String name, Integer timeEstimate){
        this.name=name;
        this.timeEstimate=timeEstimate;
        this.isActive = false;
        this.elapsedTime = 0;
    }

    public static RoutineEntity fromRoutine(@NonNull Routine routine) {
        RoutineEntity newRoutine = new RoutineEntity(routine.getName(), routine.getTimeEstimate());
        newRoutine.id = routine.id();
        newRoutine.isActive = false;
        newRoutine.elapsedTime = routine.getTotalTimer().getSecondsElapsed();
        return newRoutine;
    }

    public @NonNull Routine toRoutine() {
        Routine routine = new Routine(id, name);
        if (timeEstimate != null) {
            routine.setTimeEstimate(timeEstimate);
        }
        if (isActive && elapsedTime != null) {
            routine.setLastLapTime(elapsedTime);
        }
        return routine;
    }
}
