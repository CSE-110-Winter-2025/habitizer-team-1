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
            routine = repository.getRoutineById(passed.id());
        }

        TextView routineName = view.findViewById(R.id.routineName);
        //TextView elapsedTimeView = view.findViewById(R.id.elapsedTime);
        RecyclerView tasks = view.findViewById(R.id.taskRecyclerView);
        Button backButton = view.findViewById(R.id.backButton);
        Button endRoutineButton = view.findViewById(R.id.endRoutineButton);
        ImageButton stopButton = view.findViewById(R.id.button_stop);
        ImageButton advanceButton = view.findViewById(R.id.button_advance);
        TextView timeRemaining = view.findViewById(R.id.timeRemaining);


        routineName.setText(routine.getName());

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
                requireActivity().runOnUiThread(() -> timeRemaining.setText(formattedTime));
            }

            @Override
            public void onRoutineCompleted(int totalTime, String formattedTime) {
                requireActivity().runOnUiThread(() -> timeRemaining.setText("Completed in: " + formattedTime));
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

private void markTaskComplete(Task task) {
    task.setComplete(true);

    long currentTime = totalTimer.getSecondsElapsed();
    long lastLapTime = routine.getLastLapTime(); // Retrieve the last lap time
    long lapTime = currentTime - lastLapTime; // Calculate the time since last task completion
    task.setLapTime(lapTime);
    routine.setLastLapTime(currentTime);

    //requireActivity().runOnUiThread(() -> updateTaskLapTimeUI(task));


    // Save the lap time for the task
    //int taskLapTime = totalTimer.getSecondsElapsed(); // Assuming `totalTimer` tracks elapsed time for the current task
    //task.setLapTime(taskLapTime);

    if (routine != null) { //Null check for safety

        routine.checkTasksCompleted();

        if (routine.allTasksCompleted()) {
            // Stop the timer if all tasks are complete
            totalTimer.stop();
            requireActivity().runOnUiThread(() -> {
                TextView timeRemaining = requireActivity().findViewById(R.id.timeRemaining);
                timeRemaining.setText("Completed in:\n " + formatTime(totalTimer.getSecondsElapsed()));
            });
        }
    }
    taskAdapter.notifyDataSetChanged();
}



}
