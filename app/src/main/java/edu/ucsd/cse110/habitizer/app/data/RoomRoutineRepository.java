package edu.ucsd.cse110.habitizer.app.data;


import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;

import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.Task;
import edu.ucsd.cse110.habitizer.lib.domain.SimpleRoutineRepository;
import edu.ucsd.cse110.observables.PlainMutableSubject;
import edu.ucsd.cse110.observables.Subject;



public class RoomRoutineRepository implements SimpleRoutineRepository {

    private final RoutineDao routineDao;
    private final TaskDao taskDao;
    private final Map<Integer, PlainMutableSubject<Routine>> routineSubjects = new HashMap<>();

    public RoomRoutineRepository(RoutineDao routineDao, TaskDao taskDao){
        this.routineDao = routineDao;
        this.taskDao = taskDao;
        loadDefaultData();
    }

    @Override
    public List<Routine> getRoutines() {
        return routineDao.findAllWithTasks().stream()
                .map(RoutineWithTasks::toRoutine)
                .collect(Collectors.toList());

    }
    @Override
    public List<Task> getRoutineTasks(int routineId){
        List<TaskEntity> taskEntities = taskDao.getTasksForRoutine(routineId);

        return taskEntities.stream().map(TaskEntity::toTask).collect(Collectors.toList());
    }

    @Override 
    public Subject<Routine> getRoutineByIdAsSubject(int routineId) {
        if (!routineSubjects.containsKey(routineId)) {
            var subject = new PlainMutableSubject<Routine>(getRoutineById(routineId));
            routineSubjects.put(routineId, subject);
        }
        return routineSubjects.get(routineId);
    }

    @Override
    public Routine getRoutineById(int routineId){
        var routineWithTasks = routineDao.findWithTasks(routineId);
        return routineWithTasks.toRoutine();
    }

    @Override
    public void renameTask(int routineId, Task task, String newName) {
    taskDao.updateTitle(task.id(), newName);
    
    if (routineSubjects.containsKey(routineId)) {
        Routine updatedRoutine = getRoutineById(routineId);
            routineSubjects.get(routineId).setValue(updatedRoutine);
        }
    }

    @Override
    public void addTaskToRoutine(int routineId, Task task){
       taskDao.insert(TaskEntity.fromTask(task, routineId));

       if (routineSubjects.containsKey(routineId)) {
        Routine updatedRoutine = getRoutineById(routineId);
        routineSubjects.get(routineId).setValue(updatedRoutine);
    }
    }

    @Override
    public void markTaskComplete(Task task){
        taskDao.updateCompletedState(task.id(), true);
    }

    @Override
    public void resetRoutine(int routineId){
        return;
    }

    @Override
    public void removeTaskFromRoutine(int routineId, Task task){
        taskDao.delete(task.id());

        if (routineSubjects.containsKey(routineId)) {
            Routine updatedRoutine = getRoutineById(routineId);
            routineSubjects.get(routineId).setValue(updatedRoutine);
        }
    }

    @Override
    public void deleteRoutine(int routineId){
        taskDao.deleteTasksForRoutine(routineId);
        routineDao.delete(routineId);
        if (routineSubjects.containsKey(routineId)) {
            routineSubjects.remove(routineId);
        }
    }



    private void loadDefaultData() {
        if (routineDao.count() > 0) return;

        var morningRoutine = new RoutineEntity("Morning", 0);
        var eveningRoutine = new RoutineEntity("Evening", 0);

        int morningId = Math.toIntExact(routineDao.insert(morningRoutine));
        int eveningId = Math.toIntExact(routineDao.insert(eveningRoutine));

        List<TaskEntity> morningTasks = List.of(
                new TaskEntity("Shower", morningId),
                new TaskEntity("Brush teeth", morningId),
                new TaskEntity("Dress", morningId),
                new TaskEntity("Make coffee", morningId),
                new TaskEntity("Make lunch", morningId),
                new TaskEntity("Dinner prep", morningId),
                new TaskEntity("Pack bag", morningId)
                );


        List<TaskEntity> eveningTasks = List.of(
                new TaskEntity("Charge devices", eveningId),
                new TaskEntity("Make dinner", eveningId),
                new TaskEntity("Eat dinner", eveningId),
                new TaskEntity("Wash dishes", eveningId),
                new TaskEntity("Pack bag for morning", eveningId),
                new TaskEntity("Homework", eveningId)
                );
        taskDao.insert(morningTasks);
        taskDao.insert(eveningTasks);
    }


}
