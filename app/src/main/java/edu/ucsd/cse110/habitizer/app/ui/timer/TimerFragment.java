<<<<<<< Updated upstream
//package edu.ucsd.cse110.habitizer.app.ui.timer;
//
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.TextView;
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//
//import edu.ucsd.cse110.habitizer.app.R;
//import edu.ucsd.cse110.habitizer.lib.domain.Routine;
//import edu.ucsd.cse110.habitizer.lib.domain.TotalTimer;
//
//public class TimerFragment extends Fragment {
//    private Routine routine;
//    private TextView timerText;
//    private Button startRoutineButton, endRoutineButton;
//
//    public TimerFragment() {
//        // Required empty public constructor
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_timer, container, false);
//
//        timerText = view.findViewById(R.id.timerText);
//        startRoutineButton = view.findViewById(R.id.startRoutineButton);
//        endRoutineButton = view.findViewById(R.id.endRoutineButton);
//
//        // Initialize routine (dummy routine for now)
//        routine = new Routine(1, "Morning Routine");
//
//
//        // Update to match new listener method with formatted time
//        routine.getTotalTimer().setListener(new TotalTimer.TimerListener() {
//            @Override
//            public void onTick(int secondsElapsed, String formattedTime) {
//                requireActivity().runOnUiThread(() -> timerText.setText("Time: " + formattedTime));
//            }
//
//            @Override
//            public void onRoutineCompleted(int totalTime, String formattedTime) {
//                requireActivity().runOnUiThread(() -> timerText.setText("Routine completed in " + formattedTime));
//            }
//
//        });
//
//
//
//        startRoutineButton.setOnClickListener(v -> routine.getTotalTimer().start());
//        endRoutineButton.setOnClickListener(v -> routine.getTotalTimer().stop());
//
//        return view;
//    }
//}
=======
package edu.ucsd.cse110.habitizer.app.ui.timer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import edu.ucsd.cse110.habitizer.app.R;
import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.TotalTimer;

public class TimerFragment extends Fragment {
    private Routine routine;
    private TextView timerText;
    private Button startRoutineButton, endRoutineButton;


    public TimerFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timer, container, false);

        timerText = view.findViewById(R.id.timerText);
        startRoutineButton = view.findViewById(R.id.startRoutineButton);
        endRoutineButton = view.findViewById(R.id.endRoutineButton);


        // Update to match new listener method with formatted time
        routine.getTotalTimer().setListener(new TotalTimer.TimerListener() {
            @Override
            public void onTick(int secondsElapsed, String formattedTime) {
                requireActivity().runOnUiThread(() -> timerText.setText("Time: " + formattedTime));
            }

            @Override
            public void onRoutineCompleted(int totalTime, String formattedTime) {
                requireActivity().runOnUiThread(() -> timerText.setText("Routine completed in " + formattedTime));
            }
        });

        startRoutineButton.setOnClickListener(v -> routine.getTotalTimer().start());
        endRoutineButton.setOnClickListener(v -> routine.getTotalTimer().stop());



        return view;
    }


}
>>>>>>> Stashed changes
