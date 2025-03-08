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


    public RoutineEntity(@NonNull String name, Integer timeEstimate){
        this.name=name;
        this.timeEstimate=timeEstimate;
    }

    public static RoutineEntity fromRoutine(@NonNull Routine routine){
        RoutineEntity newRoutine = new RoutineEntity(routine.getName(), routine.getTimeEstimate());
        newRoutine.id = routine.id();
        return newRoutine;
    }

    public @NonNull Routine toRoutine(){
       var routine = new Routine(id, name);
       if (timeEstimate != null) {
           routine.setTimeEstimate(timeEstimate);
       }
       return routine;
    }
}
