package edu.ucsd.cse110.habitizer.app.ui.task;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import edu.ucsd.cse110.habitizer.app.R;
import edu.ucsd.cse110.habitizer.app.ui.HabitizerApplication;
import edu.ucsd.cse110.habitizer.app.ui.MainViewModel;
import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.Task;
import edu.ucsd.cse110.habitizer.lib.domain.TotalTimer;
import edu.ucsd.cse110.observables.Observer;
import edu.ucsd.cse110.observables.Subject;

public class TaskFragment extends Fragment {

    private TextView routineName;
    private TextView timeEstimateView;
    private Routine routine;
    private TaskViewAdapter taskAdapter;

    // ViewModel
    private MainViewModel viewModel;

    private TotalTimer totalTimer;

    private TextView taskTimer;


    private boolean isEditing;
    private boolean resumeMode;
    private boolean isPausedByStopButton = false;

    private ImageButton stopButton;


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
            isEditing = getArguments().getBoolean("isEditing", false);

        }

        routineName = view.findViewById(R.id.routineName);
        RecyclerView tasks = view.findViewById(R.id.taskRecyclerView);
        Button backButton = view.findViewById(R.id.backButton);
        timeEstimateView = view.findViewById(R.id.timeEstimate);
        Button endRoutineButton = view.findViewById(R.id.endRoutineButton);
        TextView timeRemaining = view.findViewById(R.id.timeRemaining);
        stopButton = view.findViewById(R.id.button_stop);
        ImageButton advanceButton = view.findViewById(R.id.button_advance);
        taskTimer = view.findViewById(R.id.currentTaskTime);
        MaterialButton testPauseButton = view.findViewById(R.id.TestPause);

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

        resumeMode = getArguments() != null && getArguments().getBoolean("resume", false);
        stopButton.setOnClickListener(v -> {
            totalTimer.togglePause(true); // Call pause method
            isPausedByStopButton = totalTimer != null && totalTimer.isPausedByStopButton();
            toggleUIFreeze(isPausedByStopButton); // Freeze/unfreeze UI
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

                // Update the task timer UI
                int taskTime = (secondsElapsed - (int) routine.getLastLapTime()) / 60;
                requireActivity().runOnUiThread(() -> {
                    taskTimer.setText("Current Task: " + taskTime + " m"); // Display time in MM:SS format
                });
                viewModel.updateRoutineState(routine.id(), true, totalTimer.getTotalTime(), routine.getLastLapTime());
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
                    stopButton.setImageResource(isPaused ? R.drawable.play : R.drawable.pause); //let them both be pause icon for now
                });
            }
        });

        if (resumeMode) {
            int savedSeconds = routine.getTotalTimer().getTotalTime();
            totalTimer.setSecondsElapsed(savedSeconds);
            totalTimer.togglePause(true);  // keep timer paused on resume
            // Load tasks from DB (already done via ViewModel)
            taskAdapter.setTasks(viewModel.getRoutineById(routine.id()).getTasks());
            taskAdapter.notifyDataSetChanged();
        } else {
            viewModel.updateRoutineState(routine.id(), true, 0, 0);
            totalTimer.start();
        }

        backButton.setEnabled(false);
        backButton.setOnClickListener(v -> {
            // User exiting routine: reset tasks and stop timer
            viewModel.resetRoutine(routine.id());
            totalTimer.stop();
            requireActivity().getSupportFragmentManager().popBackStack();
        });

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

            //disable pause button
            stopButton.setEnabled(false);
            advanceButton.setEnabled(false);
            testPauseButton.setEnabled(false);

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

        testPauseButton.setOnClickListener(v -> {
            if (totalTimer.isRunning()) { // Only allow pause, no resume
                totalTimer.togglePause(false); // Pause, but don't allow resuming
            }
        });

        advanceButton.setOnClickListener(v -> {
            totalTimer.advanceTime(); // This should internally reset the lap timer (i.e. set lapStartTime = secondsElapsed
            // Now, commit the updated timer state to the database (Room)
            viewModel.updateRoutineState(routine.id(), true, totalTimer.getTotalTime(), routine.getLastLapTime());
        });

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        // If the fragment is pausing because the app is going to background
        if (!requireActivity().isFinishing() && !isRemoving() && !routine.getEnded()) {
            // Pause the routine and save state
            if (totalTimer.isRunning()) {
                totalTimer.togglePause(false);
            }
            viewModel.updateRoutineState(routine.id(), true, totalTimer.getTotalTime(), routine.getLastLapTime());
        }
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

        if (routine.getTotalTimer().getSecondsElapsed() > 0) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e){
            }

            totalTimer.togglePause(true);
            requireActivity().runOnUiThread(() -> toggleUIFreeze(true));
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
        } else{
            task.setComplete(true);
            // Use `recordLap()` from TotalTimer to get the lap duration
            long lapTime = totalTimer.getSecondsElapsed() - (int) routine.getLastLapTime();
            //task.setLapTime(lapTime); // Store the lap time for the task
            task.setLapTime(lapTime);
            routine.setLastLapTime(totalTimer.getSecondsElapsed()); // Update routine tracking

            int taskTime = ((int) lapTime) / 60;

            requireActivity().runOnUiThread(() -> {
                taskTimer.setText("Current Task: " + taskTime + " m"); // Display time in MM:SS format
            });

            // Ensure UI updates to reflect lap times
            requireActivity().runOnUiThread(() -> taskAdapter.notifyDataSetChanged());
            viewModel.markTaskComplete(task);

            // Ensure UI updates to reflect lap times
            requireActivity().runOnUiThread(() -> taskAdapter.notifyDataSetChanged());

            if (routine != null) { //Null check for safety
                if (routine.allTasksCompleted()) {
                    // Stop the timer if all tasks are complete
                    routine.setEnded(true);
                    totalTimer.stop();
                    Activity activity = getActivity();
                    stopButton.setEnabled(false);

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



    private void rerender(){
        routineName.setText(routine.getName());
        taskAdapter.setTasks(routine.getTasks());
        taskAdapter.notifyDataSetChanged();
        updateTimeEstimate(timeEstimateView);
    }

    private void toggleUIFreeze(boolean isFrozen) {
        requireActivity().runOnUiThread(() -> {
            // Disable all buttons
            View view = requireView();
            disableAllButtons(view, isFrozen);

            // Disable RecyclerView (task list)
            RecyclerView tasks = view.findViewById(R.id.taskRecyclerView);
            if (tasks != null) {
                tasks.setEnabled(!isFrozen); // Disable the RecyclerView itself
            }

            // Update the task adapter to prevent clicking tasks
            if (taskAdapter != null) {
                taskAdapter.setFrozen(isFrozen); // Disable/enable task clicks
            }
        });

        View overlay = requireView().findViewById(R.id.pauseOverlay);
        if (overlay != null) {
            overlay.setVisibility(isFrozen ? View.VISIBLE : View.GONE);
        }
    }

    private void disableAllButtons(View view, boolean disable) {
        // Ensure advance button and test pause button are disabled when routine ends
        if (routine.getEnded()) {
            if (view.getId() == R.id.button_advance || view.getId() == R.id.TestPause) {
                view.setEnabled(false);
                return; // Always disable these when routine is ended
            }
        }

        // Keep the stop button enabled until routine ends
        if (view.getId() == R.id.button_stop && !routine.getEnded()) {
            view.setEnabled(true);
            return;
        }

        // Keep the back button frozen until routine ends
        if (view.getId() == R.id.backButton && !routine.getEnded()) {
            return; // Don't enable the back button yet
        }

        if (view instanceof Button || view instanceof ImageButton || view instanceof MaterialButton) {
            view.setEnabled(!disable);
        } else if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                disableAllButtons(group.getChildAt(i), disable);
            }
        }
    }

}
