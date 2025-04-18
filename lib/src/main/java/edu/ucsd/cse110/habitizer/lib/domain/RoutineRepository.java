 package edu.ucsd.cse110.habitizer.lib.domain;

 import edu.ucsd.cse110.habitizer.lib.data.InMemoryDataSource;
 import edu.ucsd.cse110.observables.PlainMutableSubject;
 import edu.ucsd.cse110.observables.Subject;

 import java.util.List;
 public class RoutineRepository implements SimpleRoutineRepository {
     private final InMemoryDataSource dataSource;

     public RoutineRepository() {
         this.dataSource = new InMemoryDataSource();
     }

      @Override
     public List<Routine> getRoutines() {
         return dataSource.getRoutines();
     }


     // gets task objects from routine
      @Override
     public List<Task> getRoutineTasks(int routineId) {
         Routine routine = getRoutineById(routineId);
         if (routine == null) return List.of();
         return routine.getTasks();
     }

     @Override
     public Subject<Routine> getRoutineByIdAsSubject(int routineId){
         return new PlainMutableSubject<>(dataSource.getRoutineById(routineId));
     }
      @Override
     public Routine getRoutineById(int routineId) {
         return dataSource.getRoutineById(routineId);
     }

     // we're not going to use this so i got lazy
     @Override
     public void renameTask(int routineId, Task task, String newName) {
         return;
     }

     // adds task objects to routine
      @Override
     public void addTaskToRoutine(int routineId, Task task) {
         dataSource.addTask(task);
         Routine routine = getRoutineById(routineId);
         // check for redundant tasks
         if (routine != null && !routine.getTasks().contains(task)) {
             routine.addTask(task);
         }
     }

      @Override
     public void markTaskComplete(Task task) {
         task.setComplete(true);
     }

      @Override
     public void resetRoutine(int routineId) {
         Routine routine = getRoutineById(routineId);
         if (routine != null && routine.getTasks() != null) {

             for(Task task : routine.getTasks()){
                 task.setComplete(false);
             }

         }

         routine.setEnded(false); // ensure that routine is not ended

     }

     @Override
     public void removeTaskFromRoutine(int routineId, Task task) {

     }

     @Override
     public void deleteRoutine(int routineId) {

     }

     @Override 
     public void updateTaskOrder(int routineId, Task task1, Task task2) {
         return;
     }

     @Override
     public int addRoutine(Routine routineId) {
         this.dataSource.addRoutine(routineId);
         return routineId.id();
     }

     @Override
     public void updateRoutineTimeEstimate(int routineId, Integer newTimeEstimate) {
         this.dataSource.getRoutineById(routineId).setTimeEstimate(newTimeEstimate);
     }

     @Override
     public void updateRoutineName(int routineId, String newRoutineName) {
         this.dataSource.getRoutineById(routineId).setName(newRoutineName);
     }

 }

