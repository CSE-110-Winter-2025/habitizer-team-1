package edu.ucsd.cse110.habitizer.app.ui.routine;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import edu.ucsd.cse110.habitizer.app.R;
import edu.ucsd.cse110.habitizer.app.ui.HabitizerApplication;
import edu.ucsd.cse110.habitizer.app.ui.MainViewModel;
import edu.ucsd.cse110.habitizer.app.ui.task.TaskFragment;
import edu.ucsd.cse110.habitizer.lib.domain.Routine;

public class RoutineListFragment extends Fragment {
    // ViewModel
    private MainViewModel viewModel;

    // UI objects
    private LinearLayout buttonContainer;
    private Button editButton;

    // State - tracks edit mode
    private boolean isEditing = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_routine, container, false);
        
        var factory = ((HabitizerApplication) requireActivity().getApplication()).getViewModelFactory();
        viewModel = new ViewModelProvider(requireActivity(), factory).get(MainViewModel.class);

        buttonContainer = view.findViewById(R.id.routineButtonContainer);
        editButton = view.findViewById(R.id.editButton);

        updateEditButtonText();
        
        editButton.setOnClickListener(v -> {
            viewModel.toggleEditingState();
            updateEditButtonText();
            renderRoutineButtons();
        });

        // Observe editing state changes
        viewModel.getIsEditingSubject().observe(isEditing -> {
            updateEditButtonText();
            renderRoutineButtons();
        });

        renderRoutineButtons();
        
        return view;
    }

    private void updateEditButtonText() {
        editButton.setText(viewModel.isEditing() ? "Edit" : "Start");
    }
    
    
    private void renderRoutineButtons() {
        buttonContainer.removeAllViews(); 
        
        for (Routine routine : viewModel.getRoutines()) {
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
                Boolean isEditing = viewModel.isEditing();
                // Use the unified TaskFragment with the appropriate mode
                String fragmentTag = "TaskFragment_" + routine.id() + "_" + isEditing;
                Fragment currentFragment = requireActivity().getSupportFragmentManager()
                    .findFragmentByTag(fragmentTag);
                
                if (currentFragment == null) {
                    TaskFragment taskFragment = TaskFragment.newInstance(routine, isEditing);
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, taskFragment, fragmentTag)
                            .addToBackStack(null)
                            .commit();
                }
            });
            
            buttonContainer.addView(button);
        }
    }
}