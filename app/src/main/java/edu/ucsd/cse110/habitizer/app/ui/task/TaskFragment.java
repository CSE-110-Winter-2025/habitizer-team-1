package edu.ucsd.cse110.habitizer.app.ui.task;

import android.app.AlertDialog;
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

import java.util.List;

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

        backButton.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

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
        totalTimer.stop(); // Stop the timer, avoid memory leaks
    }


    private void markTaskComplete(Task task) {
        task.setComplete(true);
        taskAdapter.notifyDataSetChanged();
    }

    // Updates the time estimate TextView
    private void updateTimeEstimate(TextView timeEstimateView) {
        Integer estimate = routine.getTimeEstimate();

        // default if no inputted estimated time is null
        if (estimate == null) {
            timeEstimateView.setText("out of - minutes");
        } else {
            // use user input
            timeEstimateView.setText("out of " + estimate + " minutes");
        }
    }
}
