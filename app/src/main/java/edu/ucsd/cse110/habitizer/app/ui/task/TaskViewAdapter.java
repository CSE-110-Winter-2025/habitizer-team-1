package edu.ucsd.cse110.habitizer.app.ui.task;


import static edu.ucsd.cse110.habitizer.lib.domain.TotalTimer.formatTime;
import static edu.ucsd.cse110.habitizer.lib.domain.TotalTimer.lapformatTime;


import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import edu.ucsd.cse110.habitizer.lib.domain.Task;
import edu.ucsd.cse110.habitizer.lib.domain.TotalTimer;

import edu.ucsd.cse110.habitizer.app.R;

import java.util.List;

public class TaskViewAdapter extends RecyclerView.Adapter<TaskViewAdapter.TaskViewHolder> {

    private List<Task> tasks;
    private final TaskClickListener clickListener;
    private TaskClickListener deleteListener;

    private boolean isRoutineEnded = false;
    private boolean isEditing = false;

    public TaskViewAdapter(List<Task> tasks, TaskClickListener clickListener) {
        this.tasks = tasks;
        this.clickListener = clickListener;
    }

    public TaskViewAdapter(List<Task> tasks, TaskClickListener clickListener, TaskClickListener deleteListener) {
        this.tasks = tasks;
        this.clickListener = clickListener;
        this.deleteListener = deleteListener;
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

//      holder.taskName.setPaintFlags(holder.taskName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
    
    if (isEditing) {
        System.out.println("$#$$$$$$$#$#$#$#$#$");
        holder.deleteButton.setVisibility(View.VISIBLE);
        holder.deleteButton.setOnClickListener(v -> deleteListener.onTaskClick(task));
    } 
    // Now handle the different states for task appearance and click behavior
    if (!isEditing && task.complete()) {
        // Completed task in non-editing mode
        holder.taskName.setPaintFlags(holder.taskName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.itemView.setOnClickListener(null); // Disable clicks
        holder.taskDuration.setVisibility(View.VISIBLE);
        holder.taskDuration.setText(lapformatTime(task.getLapTime()));
        holder.deleteButton.setVisibility(View.GONE);
    } else if (!isEditing) {
        // Incomplete task in non-editing mode
        holder.taskDuration.setVisibility(View.GONE);
        holder.deleteButton.setVisibility(View.GONE);
        
        // Set click listener only if routine has not ended
        if (!isRoutineEnded) {
            holder.itemView.setOnClickListener(v -> {
                // Mark the task as complete
                task.setComplete(true);
                
                // Apply strikethrough
                holder.taskName.setPaintFlags(holder.taskName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                
                // Disable clicks on the task
                holder.itemView.setOnClickListener(null);
                
                clickListener.onTaskClick(task); // Notify the fragment
                notifyItemChanged(position); // Update the view
            });
        } else {
            holder.itemView.setOnClickListener(null); // Disable clicks if routine ended
        }
    } else {
        // In editing mode
        holder.taskDuration.setVisibility(View.GONE);
        
        // Set click listener for renaming
        holder.itemView.setOnClickListener(v -> {
            clickListener.onTaskClick(task); // Notify the fragment for renaming
        });
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
        View deleteButton;

        public TaskViewHolder(View itemView) {
            super(itemView);
            taskName = itemView.findViewById(R.id.taskName);
            taskDuration = itemView.findViewById(R.id.taskDuration);
            lapTime = itemView.findViewById(R.id.lapTime);
            deleteButton = itemView.findViewById(R.id.deleteButton);
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
