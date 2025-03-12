package edu.ucsd.cse110.habitizer.app.ui.task;


import static edu.ucsd.cse110.habitizer.lib.domain.TotalTimer.formatTime;
import static edu.ucsd.cse110.habitizer.lib.domain.TotalTimer.lapformatTime;


import android.graphics.Paint;
import android.util.Log;
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
    public TaskReorderListener reorderListener;

    private boolean isRoutineEnded = false;
    private boolean isEditing = false;

    private boolean isFrozen = false;

    public void setFrozen(boolean frozen) {
        this.isFrozen = frozen;
        notifyDataSetChanged(); // Ensure the adapter updates if the frozen state changes
    }

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


        if (isFrozen) {
            holder.itemView.setEnabled(false); // Disable the task click view
        } else {
            holder.itemView.setEnabled(true); // Enable the task click view
        }


        // Reset styles before applying changes
        holder.taskName.setPaintFlags(0);


        // Apply strikethrough only if the task is complete
        if (task.complete()) {
            holder.taskName.setPaintFlags(holder.taskName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        // Set task duration text
        String durationText = "";
        if (task.complete()) {
            durationText = lapformatTime(task.getLapTime());
        } else if (isRoutineEnded) {
            durationText = "-";
        }
        holder.taskDuration.setText(durationText);
        holder.taskDuration.setVisibility(task.complete() ? View.VISIBLE : View.GONE);

        // determine which mode to set up
        if (isEditing) {
            setupEditMode(holder, task);
        } else {
            setupStartMode(holder, task);
            holder.deleteButton.setVisibility(View.GONE);
            holder.deleteButton.setOnClickListener(v -> deleteListener.onTaskClick(task));
        }
    }

    /*
        Sets up editing mode
    */
    private void setupEditMode(TaskViewHolder holder, Task task) {
        holder.deleteButton.setVisibility(View.VISIBLE);
        holder.deleteButton.setOnClickListener(v -> deleteListener.onTaskClick(task));

        holder.upButton.setVisibility(View.VISIBLE);
        holder.downButton.setVisibility(View.VISIBLE);

        // Apply strikethrough
        holder.taskName.setPaintFlags(holder.taskName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.upButton.setOnClickListener(v -> moveTaskUp(holder.getAdapterPosition()));
        holder.downButton.setOnClickListener(v -> moveTaskDown(holder.getAdapterPosition()));

        holder.itemView.setOnClickListener(v -> clickListener.onTaskClick(task)); // Rename on click
    }


    /*
        Sets up running a routine mode
     */
    private void setupStartMode(TaskViewHolder holder, Task task) {
        holder.deleteButton.setVisibility(View.GONE);
        holder.upButton.setVisibility(View.GONE);
        holder.downButton.setVisibility(View.GONE);
        holder.taskDuration.setVisibility(View.VISIBLE);

        if (!task.complete() && !isRoutineEnded) {
            holder.itemView.setOnClickListener(v -> markTaskComplete(task, holder));
        } else {
            holder.itemView.setOnClickListener(null);
        }
    }

    /*
        Logic for marking a task as complete
     */
    private void markTaskComplete(Task task, TaskViewHolder holder) {
        task.setComplete(true);
        holder.taskName.setPaintFlags(holder.taskName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.itemView.setOnClickListener(null); // Disable future clicks
        clickListener.onTaskClick(task); // Notify fragment
    }

    /*
        Logic for moving a task up when the up button is used
     */
    private void moveTaskUp(int position) {
        if (position > 0) {
            swapTasks(position, position - 1);
        }
    }

    /*
        Logic for moving a task down if the down button is used
     */
    private void moveTaskDown(int position) {
        if (position < tasks.size() - 1) {
            swapTasks(position, position + 1);
        }
    }

    /*
        Swaps tasks
     */
    private void swapTasks(int firstPos, int secondPos) {
        Task firstTask = tasks.get(firstPos);
        Task secondTask = tasks.get(secondPos);

        tasks.set(firstPos, secondTask);
        tasks.set(secondPos, firstTask);

        notifyItemMoved(firstPos, secondPos);

        if (reorderListener != null) {
            reorderListener.onTaskReordered(firstTask, secondTask);
        }


    }


    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public void updateTaskState(int taskId, long lapTime, long lastLapTime, boolean isComplete) {
        for (Task task : tasks) {
            if (task.id() == taskId) {
                task.setComplete(isComplete);
                task.setLapTime(lapTime);
                task.setLastLapTime(lastLapTime);
                notifyDataSetChanged();
                break;
            }
        }
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

    public void setTimerPaused(boolean isPaused) {
        if (isPaused) {
            notifyDataSetChanged(); // Refresh UI to reflect pause state
        }
    }




}