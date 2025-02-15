package edu.ucsd.cse110.habitizer.app.ui.routine;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import edu.ucsd.cse110.habitizer.app.R;
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
        
        repository = new RoutineRepository();
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
                TaskFragment taskFragment = new TaskFragment(routine);
                requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, taskFragment)
                    .addToBackStack(null)
                    .commit();
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

    /*

    Code that can be used to rename a routine

    private void showRenameDialog(Routine routine, Button button) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        EditText input = new EditText(requireContext());
        input.setText(routine.getName());
        
        builder.setTitle("Rename Routine")
               .setView(input)
               .setPositiveButton("OK", (dialog, which) -> {
                   String newName = input.getText().toString().trim();
                   if (!newName.isEmpty()) {
                       routine.setName(newName);
                       button.setText(newName);
                   }
               })
               .setNegativeButton("Cancel", null)
               .show();
    }

     */
}
    

