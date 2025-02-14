package edu.ucsd.cse110.habitizer.app.ui.task;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.ucsd.cse110.habitizer.app.R;
import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.Task;

public class TaskFragment extends Fragment {
    private Routine routine;
    private TaskViewAdapter taskAdapter;

    public TaskFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task, container, false);

        // Testing values
        routine = new Routine(1, "Morning");
        routine.addTasks(List.of(
                // Morning
                new Task(1, "Shower"),
                new Task(2, "Brush teeth"),
                new Task(3, "Dress"),
                new Task(4, "Make coffee"),
                new Task(5, "Make lunch"),
                new Task(6, "Dinner prep"),
                new Task(7, "Pack bag")
        ));

        TextView routineName = view.findViewById(R.id.routineName);
        TextView time = view.findViewById(R.id.timeRemaining);
        RecyclerView tasks = view.findViewById(R.id.taskRecyclerView);
        Button backButton = view.findViewById(R.id.backButton);
        Button endRoutineButton = view.findViewById(R.id.endRoutineButton);

        routineName.setText(routine.getName());
        taskAdapter = new TaskViewAdapter(routine.getTasks(), task -> {

            final EditText input = new EditText(getActivity());
            input.setText(task.title());
            input.setInputType(InputType.TYPE_CLASS_TEXT);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Rename Task")
                    .setMessage("Enter a new name for the task")
                    .setView(input)
                    .setPositiveButton("Rename", (dialog, id) -> {
                        task.setTitle(input.getText().toString().trim());
                        taskAdapter.notifyDataSetChanged();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
        tasks.setLayoutManager(new LinearLayoutManager(getActivity()));
        tasks.setAdapter(taskAdapter);

        backButton.setOnClickListener(v -> {}); // US2
        endRoutineButton.setOnClickListener(v -> {});

        time.setText("0 minutes"); // Placeholder
        return view;
    }
}
