package edu.ucsd.cse110.habitizer.app.ui.task;


import static edu.ucsd.cse110.habitizer.lib.domain.TotalTimer.formatTime;
import static edu.ucsd.cse110.habitizer.lib.domain.TotalTimer.lapformatTime;


import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import edu.ucsd.cse110.habitizer.lib.domain.Task;

import edu.ucsd.cse110.habitizer.app.R;

import java.util.List;

public class TaskViewAdapter extends RecyclerView.Adapter<TaskViewAdapter.TaskViewHolder> {

    private List<Task> tasks;
    private final TaskClickListener clickListener;

    private boolean isRoutineEnded;
    private boolean isEditing;

    public TaskViewAdapter(List<Task> tasks, TaskClickListener clickListener) {
        this.tasks = tasks;
        this.clickListener = clickListener;
        this.isRoutineEnded = false;
        this.isEditing = false;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(view);
    }

    @Override

    public void onBindViewHolder(TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.taskName.setText(task.title());

        if(isRoutineEnded && !task.complete()) {
            holder.taskDuration.setText("-");
        }else{
            holder.taskDuration.setText("0 min"); // placeholder
        }

        // Apply strikethrough based on completion status
        if (!isEditing && task.complete()) {
            holder.taskName.setPaintFlags(holder.taskName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            // disable clicks on completed tasks
            holder.itemView.setOnClickListener(null); // Disable clicks
          
            holder.taskDuration.setVisibility(View.VISIBLE);

            holder.taskDuration.setText(lapformatTime(task.getLapTime()));

        } else if(!isEditing){
            holder.taskName.setPaintFlags(holder.taskName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            // Set the click listener only if the task is NOT complete and if routine has not ended
            holder.itemView.setOnClickListener(v -> {
                if (!isRoutineEnded) {
                    // Mark the task as complete
                    task.setComplete(true);

                    // Apply strikethrough
                    holder.taskName.setPaintFlags(holder.taskName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                    // Disable clicks on the task
                    holder.itemView.setOnClickListener(null);

                    clickListener.onTaskClick(task); // Notify the fragment
                    notifyItemChanged(position); // Update the view
                }
            });
        }
        else if (!isRoutineEnded){
            holder.itemView.setOnClickListener(v -> {
                clickListener.onTaskClick(task); // Notify the fragment
            });
            holder.taskDuration.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskName, taskDuration, lapTime;
        ImageButton upButton, downButton;

        public TaskViewHolder(View itemView) {
            super(itemView);
            taskName = itemView.findViewById(R.id.taskName);
            taskDuration = itemView.findViewById(R.id.taskDuration);
            lapTime = itemView.findViewById(R.id.lapTime);
//            upButton =  itemView.findViewById(R.id.button_up);
//            downButton =  itemView.findViewById(R.id.button_down);
        }
    }



    public void endRoutine() {
        isRoutineEnded = true;
        notifyDataSetChanged(); // Update the entire adapter if needed
    }

    // Setter for editing mode
    public void setEditingMode(boolean isEditing) {
        this.isEditing = isEditing;
        notifyDataSetChanged(); // Refresh the list if editing mode changes
    }


}
