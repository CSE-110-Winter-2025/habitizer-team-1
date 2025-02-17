package edu.ucsd.cse110.habitizer.app.ui.task;

import static edu.ucsd.cse110.habitizer.lib.domain.TotalTimer.formatTime;

import android.app.AlertDialog;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import edu.ucsd.cse110.habitizer.lib.domain.RoutineRepository;
import edu.ucsd.cse110.habitizer.lib.domain.Task;
import edu.ucsd.cse110.habitizer.lib.domain.TotalTimer;
import edu.ucsd.cse110.habitizer.lib.domain.TotalTimer.TimerListener;
import edu.ucsd.cse110.habitizer.app.ui.HabitizerApplication;

public class TaskFragment extends Fragment {
    private Routine routine;
    private TaskViewAdapter taskAdapter;

    // shared repository throughout app
    private RoutineRepository repository;

    private TotalTimer totalTimer;
    private TotalTimer lapTimer;

    private List<Long> lapTimes = new ArrayList<>(); // Store lap timestamps


    public TaskFragment(Routine routine) {
        this.routine = routine;
        this.totalTimer = new TotalTimer(routine);
        this.lapTimer = new TotalTimer(routine);
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

        repository = ((HabitizerApplication) requireActivity().getApplication()).getRoutineRepository();

        // Retrieve the routine passed in, get the repository's routine instance
        if (getArguments() != null) {
            Routine passed = (Routine) getArguments().getSerializable("routine");
            if (passed != null && passed.id() != null) {
                routine = repository.getRoutineById(passed.id());
            }
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


        backButton.setOnClickListener(v -> {
            // Reset task states to incomplete before going back
            repository.resetRoutine(routine.id());
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

        // Stop the timer when the routine ends
        endRoutineButton.setOnClickListener(v -> {
            totalTimer.stop();

            // Get final elapsed time in seconds from the timer
            int finalTime = totalTimer.getTotalTime();

            // Round up to minutes when end routine button is pressed
            int minutes = (finalTime + 59) / 60;
            requireActivity().runOnUiThread(() ->
                    timeRemaining.setText(minutes + " m")
            );
        });



        lapTimer.setListener(new TimerListener() {
            @Override
            public void onTick(int secondsElapsed, String formattedTime) {
                // Update UI for all tasks (or just the current task) with elapsed time
                requireActivity().runOnUiThread(() -> {
                    // Here you can update the UI for tasks in progress
                    taskAdapter.notifyDataSetChanged();
                });
            }

            @Override
            public void onRoutineCompleted(int totalTime, String formattedTime) {
                // Handle when all tasks are completed
                requireActivity().runOnUiThread(() -> {
                    TextView timeRemaining = requireActivity().findViewById(R.id.timeRemaining);
                    timeRemaining.setText("Completed in:\n" + formattedTime);
                });
            }

            @Override
            public void onPauseToggled(boolean isPaused) {

            }
        });

        stopButton.setOnClickListener(v -> totalTimer.togglePause());
        advanceButton.setOnClickListener(v -> totalTimer.advanceTime());

        return view;
    }


    // Everytime fragment is used, get updated routine (if edited)
    @Override
    public void onResume() {
        super.onResume();
        // Use the same repository throughout the app
        Routine updated = repository.getRoutineById(routine.id());
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
        super.onDestroyView();
        repository.resetRoutine(routine.id()); //ensure that all tasks statuses are reset
        totalTimer.stop(); // Stop the timer, avoid memory leaks
    }

    public void markTaskComplete(Task task) {
        task.setComplete(true);

        // Use `recordLap()` from TotalTimer to get the lap duration
        long lapTime = totalTimer.recordLap();
        task.setLapTime(lapTime); // Store the lap time for the task
        routine.setLastLapTime(totalTimer.getSecondsElapsed()); // Update routine tracking

        // Ensure UI updates to reflect lap times
        requireActivity().runOnUiThread(() -> taskAdapter.notifyDataSetChanged());

        // Handle routine completion
        if (routine != null) { // Null check for safety
            routine.checkTasksCompleted();

            if (routine.allTasksCompleted()) {
                totalTimer.stop();
                requireActivity().runOnUiThread(() -> {
                    TextView timeRemaining = requireActivity().findViewById(R.id.timeRemaining);
                    timeRemaining.setText("Completed in:\n " + formatTime(totalTimer.getSecondsElapsed()));
                });
            }
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
