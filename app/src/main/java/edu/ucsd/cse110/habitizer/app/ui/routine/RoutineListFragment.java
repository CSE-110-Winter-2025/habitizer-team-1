package edu.ucsd.cse110.habitizer.app.ui.routine;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import edu.ucsd.cse110.habitizer.app.R;
import edu.ucsd.cse110.habitizer.app.ui.HabitizerApplication;
import edu.ucsd.cse110.habitizer.app.ui.task.TaskFragment;
import edu.ucsd.cse110.habitizer.app.ui.task.EditTaskFragment;
import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.RoutineRepository;
import android.app.AlertDialog;
import android.widget.EditText;

public class RoutineListFragment extends Fragment {
    // Data
    private RoutineRepository repository;

    // UI objects
    private LinearLayout buttonContainer;
    private Button editButton;

    // State - tracks edit mode
    private boolean isEditing = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_routine, container, false);
        
        repository = ((HabitizerApplication) requireActivity().getApplication()).getRoutineRepository();
        buttonContainer = view.findViewById(R.id.routineButtonContainer);
        editButton = view.findViewById(R.id.editButton);

        editButton.setOnClickListener(v -> {
            toggleEditState();
        });

        for (Routine routine : repository.getRoutines()) {
            Button button = new Button(getContext());
            button.setText(routine.getName());
            
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,  
                LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 0, 16); 
            params.gravity = android.view.Gravity.CENTER;  
            
            button.setLayoutParams(params);

            button.setOnClickListener(v -> {
<<<<<<< HEAD
               TaskFragment taskFragment = new TaskFragment(routine);

                requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, taskFragment)
                    .addToBackStack(null)
                    .commit();
=======
                // Create a unique tag based on the routine ID.
                String fragmentTag = "TaskFragment_" + routine.id();
                // See if there is existing fragment with the tag
                Fragment currentFragment = requireActivity().getSupportFragmentManager().findFragmentByTag(fragmentTag);
                // Only replace if the desired fragment is not already displayed to reduce redundant UI updates
                if (currentFragment == null) {
                    TaskFragment taskFragment = TaskFragment.newInstance(routine);
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, taskFragment, fragmentTag)
                            .addToBackStack(null)
                            .commit();
                }
>>>>>>> e7b94d1d55a56b879054191a1bc10c3e899923b5
            });
            
            buttonContainer.addView(button);
        }

        return view;
    }


    private void toggleEditState(){
        this.isEditing = !this.isEditing;
        editButton.setText(this.isEditing ? "Edit" : "Start");
        renderRoutineButtons();
    }
    

    private void renderRoutineButtons() {
        buttonContainer.removeAllViews(); 
        
        for (Routine routine : repository.getRoutines()) {
            Button button = new Button(getContext());
            button.setText(routine.getName());
            
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 0, 16);
            params.gravity = android.view.Gravity.CENTER;
            button.setLayoutParams(params);


            // Set OnClickListener so the fragment change occurs only when clicking the button
            button.setOnClickListener(v -> {
                if (isEditing) {
                    // Open EditTaskFragment if in edit mode
                    EditTaskFragment editTaskFragment = EditTaskFragment.newInstance(routine);
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, editTaskFragment)
                            .addToBackStack(null)
                            .commit();
                } else {
                    // Otherwise open TaskFragment to start running the routine
                   TaskFragment taskFragment = TaskFragment.newInstance(routine);

                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, taskFragment)
                            .addToBackStack(null)
                            .commit();
                }
            });
            
            buttonContainer.addView(button);
        }
    }
}
    

