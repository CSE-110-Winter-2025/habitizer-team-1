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
import edu.ucsd.cse110.habitizer.lib.domain.TotalTimer;

import edu.ucsd.cse110.habitizer.app.R;

import java.util.List;
import java.util.stream.Collectors;
public class TaskViewAdapter extends RecyclerView.Adapter<TaskViewAdapter.TaskViewHolder> {

    private List<Task> tasks;
    private final TaskClickListener clickListener;
    private TaskClickListener deleteListener;
    private TaskReorderListener reorderListener;

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

    public void setReorderListener(TaskReorderListener reorderListener) {
        this.reorderListener = reorderListener;
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

            holder.upButton.setVisibility(View.GONE); //make sure can't move during nonediting
            holder.downButton.setVisibility(View.GONE);
        } else if (!isEditing) {
            // Incomplete task in non-editing mode
            holder.taskDuration.setVisibility(View.GONE);
            holder.deleteButton.setVisibility(View.GONE);
            holder.upButton.setVisibility(View.GONE); //make sure can't move during nonediting
            holder.downButton.setVisibility(View.GONE);
            
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

            // Up button click event
            holder.upButton.setOnClickListener(v -> {

                int currPos = holder.getAdapterPosition();

                // Ensure the current position is not the first item
                if (currPos > 0) {
                    // Swap the tasks
                    Task taskToMove = tasks.get(currPos);
                    Task taskAbove = tasks.get(currPos - 1);

                    tasks.set(currPos, taskAbove);
                    tasks.set(currPos - 1, taskToMove);

                    // Notify the adapter
                    notifyItemMoved(currPos, currPos - 1);

                    if (reorderListener != null) {
                        reorderListener.onTaskReordered(taskToMove, taskAbove);
                    }

                }
            });

// Down button click event
            holder.downButton.setOnClickListener(v -> {
                int currPos = holder.getAdapterPosition();

                // Ensure the current position is not the last item
                if (currPos < tasks.size() - 1) {
                    // Swap the tasks
                    Task taskToMove = tasks.get(currPos);
                    Task taskBelow = tasks.get(currPos + 1);

                    tasks.set(currPos, taskBelow);
                    tasks.set(currPos + 1, taskToMove);

                    // Notify the adapter
                    notifyItemMoved(currPos, currPos + 1);

                    if (reorderListener != null) {
                        reorderListener.onTaskReordered(taskToMove, taskBelow);
                    }
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks.stream()
        .sorted((t1, t2) -> Integer.compare(t1.getPosition(), t2.getPosition()))
        .collect(Collectors.toList());
    notifyDataSetChanged();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskName, taskDuration, lapTime;
        ImageButton upButton, downButton;
        View deleteButton;

        public TaskViewHolder(View itemView) {
            super(itemView);
            taskName = itemView.findViewById(R.id.taskName);
            taskDuration = itemView.findViewById(R.id.taskDuration);
            lapTime = itemView.findViewById(R.id.lapTime);
            upButton = itemView.findViewById(R.id.move_up);
            downButton = itemView.findViewById(R.id.move_down);
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