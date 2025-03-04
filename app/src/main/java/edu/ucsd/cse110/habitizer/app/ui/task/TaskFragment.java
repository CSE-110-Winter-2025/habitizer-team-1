package edu.ucsd.cse110.habitizer.app.ui.task;

import static edu.ucsd.cse110.habitizer.lib.domain.TotalTimer.formatTime;


import android.app.AlertDialog;
import android.graphics.Paint;

import android.app.Activity;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.EditText;
import android.text.InputType;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import edu.ucsd.cse110.habitizer.app.R;
import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.Task;
import edu.ucsd.cse110.habitizer.lib.domain.TotalTimer;
import edu.ucsd.cse110.habitizer.app.ui.HabitizerApplication;
import edu.ucsd.cse110.habitizer.app.ui.MainViewModel;
import edu.ucsd.cse110.observables.Observer;
import edu.ucsd.cse110.observables.Subject;

import androidx.lifecycle.ViewModelProvider;

public class TaskFragment extends Fragment {

    private TextView routineName;
    private TextView timeEstimateView;
    private Routine routine;
    private TaskViewAdapter taskAdapter;
    private Button backButton;
    private Button addTaskButton;
    private Button endRoutineButton;
    private static final int MAX_TIME_ESTIMATE = 10000;

    // ViewModel
    private MainViewModel viewModel;

    private TotalTimer totalTimer;

    private boolean isEditing;

    public TaskFragment(Routine routine, boolean isEditing) {
        this.routine = routine;
        this.isEditing = isEditing;
        this.totalTimer = new TotalTimer(routine);
    }

    public static TaskFragment newInstance(Routine routine, boolean isEditing) {
        TaskFragment fragment = new TaskFragment(routine, isEditing);
        Bundle args = new Bundle();
        args.putSerializable("routine", routine);
        args.putBoolean("isEditing", isEditing);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        var factory = ((HabitizerApplication) requireActivity().getApplication()).getViewModelFactory();
        viewModel = new ViewModelProvider(requireActivity(), factory).get(MainViewModel.class);

        if (getArguments() != null) {
            Routine passed = (Routine) getArguments().getSerializable("routine");
            if (passed != null && passed.id() != null) {
                Subject<Routine> routineSubject = viewModel.getRoutineByIdAsSubject(passed.id());
                routineSubject.observe(new Observer<Routine>() {
                    @Override
                    public void onChanged(@Nullable Routine value) {
                       if(value !=null) {
                          routine = value;
                          rerender();
                       }
                    }
                });
            }

        }
        View view = inflater.inflate(R.layout.fragment_task, container, false);
        
        routineName = view.findViewById(R.id.routineName);
        RecyclerView tasksView = view.findViewById(R.id.taskRecyclerView);
        backButton = view.findViewById(R.id.backButton);
        timeEstimateView = view.findViewById(R.id.timeEstimate);
        endRoutineButton = view.findViewById(R.id.endRoutineButton);
        addTaskButton = view.findViewById(R.id.addTaskButton);

        if (isEditing) {
            setupEditMode(view);
        } else {
            setupRunMode(view);
        }

        backButton.setOnClickListener(v -> {
            if (!isEditing) viewModel.resetRoutine(routine.id());
            requireActivity().getSupportFragmentManager().popBackStack();
        });
        backButton.setEnabled(false);
        
        // Setup task adapter with appropriate callback
        taskAdapter = new TaskViewAdapter(
                routine.getTasks(),
                task -> {
                    if (isEditing) {
                        renameTask(task);
                    } else {
                        markTaskComplete(task);
                    }
                }
        );
        taskAdapter.setEditingMode(isEditing);
        tasksView.setLayoutManager(new LinearLayoutManager(getActivity()));
        tasksView.setAdapter(taskAdapter);
        
        rerender();
        return view;
    }
    

    private void setupRunMode(View view) {
        TextView timeRemaining = view.findViewById(R.id.timeRemaining);
        ImageButton stopButton = view.findViewById(R.id.button_stop);
        ImageButton advanceButton = view.findViewById(R.id.button_advance);

        addTaskButton.setVisibility(View.GONE);
        
        totalTimer = new TotalTimer(routine);
        totalTimer.setListener(new TotalTimer.TimerListener() {
            @Override
            public void onTick(int secondsElapsed, String formattedTime) {
                int minutes = secondsElapsed / 60;
                requireActivity().runOnUiThread(() -> 
                    timeRemaining.setText(minutes + " m")
                );
            }

            @Override
            public void onRoutineCompleted(int totalTime, String formattedTime) {
                endRoutineButton.setText("Routine Ended");
                endRoutineButton.setEnabled(false);
                backButton.setEnabled(true);
                int minutes = (totalTime + 59) / 60;
                requireActivity().runOnUiThread(() ->
                    timeRemaining.setText(minutes + " m")
                );
            }

            @Override
            public void onPauseToggled(boolean isPaused) {
                requireActivity().runOnUiThread(() -> {
                    stopButton.setImageResource(R.drawable.pause);
                });
            }
        });
        
        totalTimer.start();
        
        endRoutineButton.setOnClickListener(v -> {
            routine.setEnded(true);
            taskAdapter.endRoutine();
            totalTimer.stop();
            endRoutineButton.setText("Routine Ended");
            endRoutineButton.setEnabled(false);
            backButton.setEnabled(true);
            int minutes = (totalTimer.getTotalTime() + 59) / 60;
            timeRemaining.setText(minutes + " m");
        });
        
        stopButton.setOnClickListener(v -> totalTimer.togglePause());
        advanceButton.setOnClickListener(v -> totalTimer.advanceTime());
    }

    private void setupEditMode(View view) {

        endRoutineButton.setVisibility(View.GONE);
        addTaskButton.setOnClickListener(v -> addTask());
        
        timeEstimateView.setOnClickListener(v -> {
            final EditText input = new EditText(getActivity());
            input.setHint("Enter time estimate in minutes");
            input.setInputType(InputType.TYPE_CLASS_NUMBER);

            new AlertDialog.Builder(getActivity())
                    .setTitle("Set Time Estimate")
                    .setView(input)
                    .setPositiveButton("OK", (dialog, which) -> {
                        String text = input.getText().toString().trim();
                        if (!text.isEmpty()) {
                            try {
                                Integer newEstimate = Integer.parseInt(text);
                                if (newEstimate < MAX_TIME_ESTIMATE && newEstimate >= 0) {
                                    routine.setTimeEstimate(newEstimate);
                                    updateTimeEstimate();
                                }
                            } catch (NumberFormatException e) {
                                return;
                            }
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void rerender() {
        if (routine == null) return;
        
        routineName.setText(routine.getName());
        if (taskAdapter != null) {
            taskAdapter.setTasks(routine.getTasks());
            taskAdapter.notifyDataSetChanged();
        }
        updateTimeEstimate();
    }
    
    private void updateTimeEstimate() {
        Integer estimate = routine.getTimeEstimate();
        if (isEditing) {
            timeEstimateView.setText(estimate == null ? "- minutes" : estimate + " minutes");
        } else {
            timeEstimateView.setText(estimate == null ? "/ - minutes" : "/ " + estimate + " minutes");
        }
    }
    
    private void markTaskComplete(Task task) {
        if (routine != null && routine.getEnded()) return;
        
        task.setComplete(true);
        long lapTime = totalTimer.recordLap();
        task.setLapTime(lapTime);
        routine.setLastLapTime(totalTimer.getSecondsElapsed());
        
        requireActivity().runOnUiThread(() -> taskAdapter.notifyDataSetChanged());
        
        if (routine != null && routine.allTasksCompleted()) {
            routine.setEnded(true);
            totalTimer.stop();
            
            requireActivity().runOnUiThread(() -> {
                View view = getView();
                if (view != null) {
                    TextView timeRemaining = view.findViewById(R.id.timeRemaining);
                    Button endRoutineButton = view.findViewById(R.id.endRoutineButton);
                    Button backButton = view.findViewById(R.id.backButton);
                    
                    if (timeRemaining != null) {
                        int time = (totalTimer.getSecondsElapsed() + 59)/60;
                        timeRemaining.setText(time + " m");
                    }
                    
                    if (endRoutineButton != null) {
                        endRoutineButton.setText("Routine Ended");
                        endRoutineButton.setEnabled(false);
                    }
                    
                }
            });
        }
    }
    
    private void renameTask(Task task) {
        final EditText input = new EditText(getActivity());
        input.setText(task.title());
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        new AlertDialog.Builder(getActivity())
            .setTitle("Rename Task")
            .setMessage("Enter a new name for the task")
            .setView(input)
            .setPositiveButton("Rename", (dialog, id) -> {
                String newTaskName = input.getText().toString().trim();
                if (!newTaskName.isEmpty()) {
                    viewModel.renameTask(routine.id(), task, newTaskName);
                    task.setTitle(newTaskName);
                    taskAdapter.notifyDataSetChanged();
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
        rerender();
    }
    
    private void addTask() {
        final EditText input = new EditText(getActivity());
        input.setHint("Add task");
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        new AlertDialog.Builder(getActivity())
            .setTitle("Add")
            .setView(input)
            .setPositiveButton("Add", (dialog, id) -> {
                String newTaskName = input.getText().toString().trim();
                if (!newTaskName.isEmpty()) {
                    Task newTask = new Task(null, newTaskName);
                    viewModel.addTaskToRoutine(routine.id(), newTask);
                    taskAdapter.notifyDataSetChanged();
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
        rerender();
    }
    
    @Override
    public void onDestroyView() {
        if (!isEditing) viewModel.resetRoutine(routine.id());
        super.onDestroyView();
        if (totalTimer != null) totalTimer.stop();
    }
}
