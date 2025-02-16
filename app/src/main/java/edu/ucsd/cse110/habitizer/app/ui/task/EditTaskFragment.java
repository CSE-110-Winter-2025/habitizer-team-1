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
import edu.ucsd.cse110.habitizer.lib.domain.RoutineRepository;
import edu.ucsd.cse110.habitizer.lib.domain.Task;
import edu.ucsd.cse110.habitizer.app.ui.HabitizerApplication;

public class EditTaskFragment extends Fragment {
    private Routine routine;
    private TaskViewAdapter taskAdapter;

    private RoutineRepository repository;

    public EditTaskFragment(Routine routine) {
        this.routine = routine;
    }

    public static EditTaskFragment newInstance(Routine routine) {
        EditTaskFragment fragment = new EditTaskFragment(routine);
        Bundle args = new Bundle();
        args.putSerializable("routine", routine);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_edit, container, false);

        // use one shared repository
        repository = ((HabitizerApplication) requireActivity().getApplication()).getRoutineRepository();

        // Retrieve the routine passed via arguments, and then get the repository instance.
        if (getArguments() != null) {
            Routine passed = (Routine) getArguments().getSerializable("routine");
            // Work on same routine instance stored in the repository
            routine = repository.getRoutineById(passed.id());
        }

        TextView routineName = view.findViewById(R.id.routineName);
        RecyclerView tasks = view.findViewById(R.id.taskRecyclerView);
        Button backButton = view.findViewById(R.id.backButton);
        Button addTaskButton =  view.findViewById(R.id.addTaskButton);

        routineName.setText(routine.getName());

        taskAdapter = new TaskViewAdapter(routine.getTasks(), task -> renameTask(task));
        tasks.setLayoutManager(new LinearLayoutManager(getActivity()));
        tasks.setAdapter(taskAdapter);

        backButton.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        addTaskButton.setOnClickListener(v -> addTask());

        TextView timeEstimateView = view.findViewById(R.id.timeEstimate);
        updateTimeEstimateText(timeEstimateView); // set initial text

        // Set an onClick listener so that tapping timeEstimate opens a dialog
        timeEstimateView.setOnClickListener(v -> {
            final EditText input = new EditText(getActivity());
            input.setHint("Enter time estimate in minutes");
            input.setInputType(InputType.TYPE_CLASS_NUMBER);

            new AlertDialog.Builder(getActivity())
                    .setTitle("Set Time Estimate")
                    .setView(input)
                    .setPositiveButton("OK", (dialog, which) -> {
                        String text = input.getText().toString().trim();
                        // Use null if empty
                        Integer newEstimate = text.isEmpty() ? null : Integer.parseInt(text);

                        // If user inputs a value less than 10000, then update the time estimate
                        if (newEstimate != null && newEstimate < 10000) {
                            routine.setTimeEstimate(newEstimate);
                            updateTimeEstimateText(timeEstimateView);
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        return view;
    }

    // when user taps a task and can rename it
    private void renameTask(Task task) {
        final EditText input = new EditText(getActivity());
        input.setText(task.title());
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Rename Task")
                .setMessage("Enter a new name for the task")
                .setView(input)
                .setPositiveButton("Rename", (dialog, id) -> {
                    String newTaskName = input.getText().toString().trim();
                    // no empty task names
                    if (!newTaskName.isEmpty()) {
                        task.setTitle(input.getText().toString().trim());
                        taskAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // when user presses the add task button
    private void addTask() {
        final EditText input = new EditText(getActivity());
        input.setHint("Add task");
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add")
                .setView(input)
                .setPositiveButton("Add", (dialog, id) -> {
                    String newTaskName = input.getText().toString().trim();
                    // Do not allow empty tasks
                    if (!newTaskName.isEmpty()) {
                        Task newTask = new Task(null, newTaskName);
                        repository.addTaskToRoutine(routine.id(), newTask);
                        taskAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // updates timeEstimate
    private void updateTimeEstimateText(TextView view) {
        Integer estimate = routine.getTimeEstimate();

        // null means no change and default is -
        if (estimate == null) {
            view.setText("- minutes");
        } else {
            view.setText(estimate + " minutes");
        }
    }


}
