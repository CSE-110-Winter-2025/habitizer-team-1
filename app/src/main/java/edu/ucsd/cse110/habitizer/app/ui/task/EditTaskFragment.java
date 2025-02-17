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

        taskAdapter.setEditingMode(true); // ensure that it can't strikethrough
        return view;
    }

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
}
