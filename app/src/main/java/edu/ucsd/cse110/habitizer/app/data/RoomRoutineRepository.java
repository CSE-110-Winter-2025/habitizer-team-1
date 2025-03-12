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
        return routineSubjects.computeIfAbsent(routineId, id ->
            new PlainMutableSubject<>(getRoutineById(id))
        );
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
        int nextPosition = taskDao.getNextPosition(routineId);
        TaskEntity taskEntity = TaskEntity.fromTask(task, routineId);
        taskEntity.position = nextPosition;
        taskDao.insert(taskEntity);

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
    public int addRoutine(Routine routine) {
        long id = routineDao.insert(new RoutineEntity(routine.getName(), null));
        return Math.toIntExact(id);
    }

    @Override
    public void updateRoutineTimeEstimate(int routineId, Integer newTimeEstimate) {
        routineDao.updateTimeEstimate(routineId, newTimeEstimate);
    }

    @Override
    public void updateRoutineName(int id, String newRoutineName) {
        routineDao.updateRoutineName(id, newRoutineName);
    }

    @Override
    public void removeTaskFromRoutine(int routineId, Task task){
        int deletedPosition = taskDao.getPosition(task.id());
        taskDao.delete(task.id());
        taskDao.updateAllPositionsAfterDelete(routineId, deletedPosition);

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

    @Override
    public void updateTaskOrder(int routineId, Task task1, Task task2) {
        int pos1 = taskDao.getPosition(task1.id());
        int pos2 = taskDao.getPosition(task2.id());
        taskDao.updatePosition(task1.id(), pos2);
        taskDao.updatePosition(task2.id(), pos1);
        
        if (routineSubjects.containsKey(routineId)) {
            Routine updatedRoutine = getRoutineById(routineId);
            routineSubjects.get(routineId).setValue(updatedRoutine);
        }
    }

    private void loadDefaultData() {
        if (routineDao.count() > 0) return;

        var morningRoutine = new RoutineEntity("Morning", null);
        var eveningRoutine = new RoutineEntity("Evening", null);

        int morningId = Math.toIntExact(routineDao.insert(morningRoutine));
        int eveningId = Math.toIntExact(routineDao.insert(eveningRoutine));

        List<TaskEntity> morningTasks = List.of(
                new TaskEntity("Shower", morningId, 0),
                new TaskEntity("Brush teeth", morningId, 1),
                new TaskEntity("Dress", morningId, 2),
                new TaskEntity("Make coffee", morningId, 3),
                new TaskEntity("Make lunch", morningId, 4),
                new TaskEntity("Dinner prep", morningId, 5),
                new TaskEntity("Pack bag", morningId, 6)
        );


        List<TaskEntity> eveningTasks = List.of(
                new TaskEntity("Charge devices", eveningId, 0),
                new TaskEntity("Make dinner", eveningId, 1),
                new TaskEntity("Eat dinner", eveningId, 2),
                new TaskEntity("Wash dishes", eveningId, 3),
                new TaskEntity("Pack bag for morning", eveningId, 4),
                new TaskEntity("Homework", eveningId, 5)
        );
        taskDao.insert(morningTasks);
        taskDao.insert(eveningTasks);
    }


}
