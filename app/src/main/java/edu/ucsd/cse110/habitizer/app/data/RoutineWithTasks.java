package edu.ucsd.cse110.habitizer.app.data;

import androidx.room.Embedded;
import androidx.room.Relation;
import java.util.List;

import edu.ucsd.cse110.habitizer.lib.domain.Routine;


public class RoutineWithTasks {
    @Embedded
    public RoutineEntity routine;

    @Relation(
            parentColumn = "id",
            entityColumn = "routineId"
    )
    
    public List<TaskEntity> tasks;

    public Routine toRoutine(){
       var routine = this.routine.toRoutine();
       tasks.stream()
               .map(TaskEntity::toTask)
               .forEach(routine::addTask);
       return routine;
    }

}

