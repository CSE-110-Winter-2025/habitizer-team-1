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
import androidx.lifecycle.ViewModelProvider;

public class TaskFragment extends Fragment {
    private Routine routine;
    private TaskViewAdapter taskAdapter;

    // ViewModel
    private MainViewModel viewModel;

    private TotalTimer totalTimer;

    private boolean isEditing;

    public TaskFragment(Routine routine) {
        this.routine = routine;
        this.totalTimer = new TotalTimer(routine);
    }

    public static TaskFragment newInstance(Routine routine) {
        TaskFragment fragment = new TaskFragment(routine);
        Bundle args = new Bundle();
        args.putSerializable("routine", routine);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task, container, false);

        var factory = ((HabitizerApplication) requireActivity().getApplication()).getViewModelFactory();
        viewModel = new ViewModelProvider(requireActivity(), factory).get(MainViewModel.class);

        if (getArguments() != null) {
            Routine passed = (Routine) getArguments().getSerializable("routine");
            if (passed != null && passed.id() != null) {
                routine = viewModel.getRoutineById(passed.id());
            }
            isEditing = getArguments().getBoolean("isEditing", false);

        }

        TextView routineName = view.findViewById(R.id.routineName);
        RecyclerView tasks = view.findViewById(R.id.taskRecyclerView);
        Button backButton = view.findViewById(R.id.backButton);
        TextView timeEstimateView = view.findViewById(R.id.timeEstimate);
        Button endRoutineButton = view.findViewById(R.id.endRoutineButton);
        TextView timeRemaining = view.findViewById(R.id.timeRemaining);
        ImageButton stopButton = view.findViewById(R.id.button_stop);
        ImageButton advanceButton = view.findViewById(R.id.button_advance);

        routineName.setText(routine.getName());

        // if the estimated time has changes, it is updated
        updateTimeEstimate(timeEstimateView);


        taskAdapter = new TaskViewAdapter(routine.getTasks(), task -> markTaskComplete(task));
        tasks.setLayoutManager(new LinearLayoutManager(getActivity()));
        tasks.setAdapter(taskAdapter);


        backButton.setEnabled(false);
        backButton.setOnClickListener(v -> {
            // Reset task states to incomplete before going back
            viewModel.resetRoutine(routine.id());
            requireActivity().getSupportFragmentManager().popBackStack();
            this.onDestroyView();
        });

        backButton.setOnClickListener(v -> {
            // Reset task states to incomplete before going back
            viewModel.resetRoutine(routine.id());
            requireActivity().getSupportFragmentManager().popBackStack();
        });
        // Initialize and start the TotalTimer when the fragment loads
        totalTimer = new TotalTimer(routine);
        totalTimer.setListener(new TotalTimer.TimerListener() {
            @Override
            public void onTick(int secondsElapsed, String formattedTime) {
                // round down to minutes during running time
                int minutes = secondsElapsed / 60;
                requireActivity().runOnUiThread(() ->
                        timeRemaining.setText(minutes + " m")
                );
            }

            @Override
            public void onRoutineCompleted(int totalTime, String formattedTime) {
                endRoutineButton.setText("Routine Ended"); // change the text for once end routined
                endRoutineButton.setEnabled(false);
                // round up when completed
                int minutes = (totalTime + 59) / 60;
                requireActivity().runOnUiThread(() ->
                        timeRemaining.setText(minutes + " m")
                );
            }

            @Override
            public void onPauseToggled(boolean isPaused) {
                requireActivity().runOnUiThread(() -> {
                    stopButton.setImageResource(isPaused ? R.drawable.pause : R.drawable.pause); //let them both be pause icon for now
                });
            }
        });

        totalTimer.start(); // Start the timer when the fragment loads

        // Stop the timer when the routine ends and mark routine as ended
        endRoutineButton.setOnClickListener(v -> {
            routine.setEnded(true);
            taskAdapter.endRoutine();
            totalTimer.stop();

            // Set the button text to "Routine Ended" and disable it
            endRoutineButton.setText("Routine Ended");
            endRoutineButton.setEnabled(false);

            // enable back button
            backButton.setEnabled(true);

            // Get final elapsed time in seconds from the timer
            int finalTime = totalTimer.getTotalTime();

            // Round up to minutes when end routine button is pressed
            int minutes = (finalTime + 59) / 60;
            requireActivity().runOnUiThread(() ->
                    timeRemaining.setText(minutes + " m")
            );
        });



        // text changes
        taskAdapter.setEditingMode(false); // ensures that it can strikethrough

        stopButton.setOnClickListener(v -> totalTimer.togglePause());

        advanceButton.setOnClickListener(v -> {
            totalTimer.advanceTime(); // This should internally reset the lap timer (i.e. set lapStartTime = secondsElapsed
        });

        return view;
    }


    // Everytime fragment is used, get updated routine (if edited)
    @Override
    public void onResume() {
        super.onResume();
        Routine updated = viewModel.getRoutineById(routine.id());
        if (updated != null) {
            routine = updated;
            // Update the adapter's dataset and change the UI
            // This allows for routines to be edited and reflect it during the duration of the app
            taskAdapter.setTasks(routine.getTasks());
            taskAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroyView() {
        viewModel.resetRoutine(routine.id()); //ensure that all tasks statuses are reset
        super.onDestroyView();
        totalTimer.stop(); // Stop the timer, avoid memory leaks
    }


    public void markTaskComplete(Task task) {
        if(routine != null && routine.getEnded()) {
            return; // ensure that tasks can't be marked complete after the routine ends
        }else{
            task.setComplete(true);
          
          // Use `recordLap()` from TotalTimer to get the lap duration
          long lapTime = totalTimer.recordLap();
          task.setLapTime(lapTime); // Store the lap time for the task
          routine.setLastLapTime(totalTimer.getSecondsElapsed()); // Update routine tracking
          
          // Ensure UI updates to reflect lap times
          requireActivity().runOnUiThread(() -> taskAdapter.notifyDataSetChanged());

            if (routine != null) { //Null check for safety
                if (routine.allTasksCompleted()) {
                    // Stop the timer if all tasks are complete
                    routine.setEnded(true);
                    totalTimer.stop();
                    Activity activity = getActivity();

                    if (activity != null) {
                        activity.runOnUiThread(() -> {
                            TextView timeRemaining = activity.findViewById(R.id.timeRemaining);
                            Button endRoutineButton = activity.findViewById(R.id.endRoutineButton); // find endroutine button
                            Button backButton = activity.findViewById(R.id.backButton);

                            if (timeRemaining != null) {
                                int time = (totalTimer.getSecondsElapsed() + 59)/60;
                                timeRemaining.setText(time + " m");
                            }

                            // Set "Routine Ended" text and disable the end routine button
                            if (endRoutineButton != null) {
                                endRoutineButton.setText("Routine Ended");
                                endRoutineButton.setEnabled(false); // Disable the button
                            }

                            // enable back button
                            if(backButton != null) {
                                backButton.setEnabled(true);
                            }
                        });
                    }
                }
            }
            taskAdapter.notifyDataSetChanged();
        }
    }

    // Updates the time estimate TextView
    private void updateTimeEstimate(TextView timeEstimateView) {
        Integer estimate = routine.getTimeEstimate();

        // default if no inputted estimated time is null
        if (estimate == null) {
            timeEstimateView.setText("/ - minutes");
        } else {
            // use user input
            timeEstimateView.setText("/ " + estimate + " minutes");
        }
    }
}
