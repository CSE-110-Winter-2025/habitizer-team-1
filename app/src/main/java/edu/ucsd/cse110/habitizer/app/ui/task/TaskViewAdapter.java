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

    public TaskViewAdapter(List<Task> tasks, TaskClickListener clickListener) {
        this.tasks = tasks;
        this.clickListener = clickListener;
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
        holder.taskDuration.setText("0 min"); // placeholder
//        holder.itemView.setOnClickListener(v -> clickListener.onTaskClick(task));

        // Apply strikethrough based on completion status
        if (task.complete()) {
            holder.taskName.setPaintFlags(holder.taskName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            // disable clicks on completed tasks
            holder.itemView.setOnClickListener(null); // Disable clicks

            holder.taskDuration.setVisibility(View.VISIBLE);

            holder.taskDuration.setText("Lap Time: " + lapformatTime(task.getLapTime()));

        } else {
            holder.taskName.setPaintFlags(holder.taskName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            // Set the click listener only if the task is NOT complete
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

        public TaskViewHolder(View itemView) {
            super(itemView);
            taskName = itemView.findViewById(R.id.taskName);
            taskDuration = itemView.findViewById(R.id.taskDuration);
            lapTime = itemView.findViewById(R.id.lapTime);
        }
    }

}
