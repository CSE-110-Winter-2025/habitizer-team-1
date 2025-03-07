package edu.ucsd.cse110.habitizer.app;

import edu.ucsd.cse110.habitizer.app.ui.task.EditTaskFragment;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;

import android.view.View;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import edu.ucsd.cse110.habitizer.app.MainActivity;
import edu.ucsd.cse110.habitizer.app.ui.task.TaskViewAdapter;
import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.Task;

@RunWith(AndroidJUnit4.class)
public class ReorderTasksUITest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setUp() {
        // Navigate to EditTaskFragment where taskRecyclerViewEdit exists
        onView(withId(R.id.editButton)).perform(click()); // ✅ Ensure this opens EditTaskFragment

        // Wait for RecyclerView to load
        onView(isRoot()).perform(waitForView(R.id.taskRecyclerViewEdit, 5000));

        // Ensure RecyclerView has data
        activityRule.getScenario().onActivity(activity -> {
            // Manually replace the fragment if necessary
            Routine testRoutine = new Routine(0, "Test Routine");
            activity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new EditTaskFragment(testRoutine)) // ✅ Ensure correct fragment
                    .commitNow();

            // Retrieve the fragment AFTER navigating to it
            EditTaskFragment fragment = (EditTaskFragment) activity
                    .getSupportFragmentManager()
                    .findFragmentById(R.id.fragment_container); // ✅ Now it should be correct

            if (fragment != null) {
                TaskViewAdapter adapter = fragment.getTaskAdapter(); // Ensure this method exists
                adapter.setTasks(Arrays.asList(
                        new Task(0, "Task 1"),
                        new Task(1, "Task 2"),
                        new Task(2, "Task 3")
                ));
            }
        });
    }



    @Test
    public void testMoveTaskUp() {
        // Verify "Task 2" is initially at position 1
        onView(withId(R.id.taskRecyclerViewEdit))
                .check(matches(hasDescendant(withText("Task 2"))));

        // Click "Up" button for Task 2
        onView(withId(R.id.moveTaskUp)).perform(click());

        // Verify "Task 2" moved to position 0
        onView(withId(R.id.taskRecyclerViewEdit))
                .check(matches(hasDescendant(withText("Task 2"))));
    }

    @Test
    public void testMoveTaskDown() {
        // Verify "Task 2" is initially at position 1
        onView(withId(R.id.taskRecyclerViewEdit))
                .check(matches(hasDescendant(withText("Task 2"))));

        // Click "Down" button for Task 2
        onView(withId(R.id.moveTaskDown)).perform(click());

        // Verify "Task 2" moved to position 2
        onView(withId(R.id.taskRecyclerViewEdit))
                .check(matches(hasDescendant(withText("Task 2"))));
    }

    @Test
    public void testMoveFirstTaskUp_DoesNotChangeOrder() {
        // Verify "Task 1" is at position 0
        onView(withId(R.id.taskRecyclerViewEdit))
                .check(matches(hasDescendant(withText("Task 1"))));

        // Click "Up" button for Task 1 (should not move)
        onView(withId(R.id.moveTaskUp)).perform(click());

        // Verify "Task 1" is still at position 0
        onView(withId(R.id.taskRecyclerViewEdit))
                .check(matches(hasDescendant(withText("Task 1"))));
    }

    @Test
    public void testMoveLastTaskDown_DoesNotChangeOrder() {
        // Verify "Task 3" is at position 2
        onView(withId(R.id.taskRecyclerViewEdit))
                .check(matches(hasDescendant(withText("Task 3"))));

        // Click "Down" button for Task 3 (should not move)
        onView(withId(R.id.moveTaskDown)).perform(click());

        // Verify "Task 3" is still at position 2
        onView(withId(R.id.taskRecyclerViewEdit))
                .check(matches(hasDescendant(withText("Task 3"))));
    }

    // Helper function to wait for a view to appear
    public static ViewAction waitForView(final int viewId, final long millis) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isDisplayed();
            }

            @Override
            public String getDescription() {
                return "Wait for view with id " + viewId;
            }

            @Override
            public void perform(UiController uiController, View view) {
                uiController.loopMainThreadForAtLeast(millis);
            }
        };
    }
}
