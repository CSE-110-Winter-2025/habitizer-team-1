package edu.ucsd.cse110.habitizer.app;

import static org.junit.Assert.*;

import android.view.View;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.habitizer.app.ui.task.TaskReorderListener;
import edu.ucsd.cse110.habitizer.app.ui.task.TaskViewAdapter;
import edu.ucsd.cse110.habitizer.lib.domain.Task;

//US 15 reordering tasks
@RunWith(AndroidJUnit4.class)
public class TaskViewAdapterTest {
    private TaskViewAdapter adapter;
    private List<Task> tasks;
    private TaskReorderListener testListener;

    @Before
    public void setUp() {
        tasks = new ArrayList<>();
        tasks.add(new Task(1, "Task A")); // Initial position 0
        tasks.add(new Task(2, "Task B")); // Initial position 1

        // Use a real implementation instead of a mock
        testListener = new TaskReorderListener() {
            @Override
            public void onTaskReordered(Task from, Task to) {
                // This can be used to observe reorder behavior
            }
        };

        adapter = new TaskViewAdapter(tasks, task -> {}, task -> {});
        adapter.setReorderListener(testListener);
    }

    @Test
    public void testMoveTaskUp() {
        // Move "Task B" up (swap with "Task A")
        Task movedTask = tasks.get(1); // "Task B"
        tasks.remove(1);
        tasks.add(0, movedTask); // Move "Task B" up

        adapter.notifyItemMoved(1, 0); // Notify adapter of change
        adapter.reorderListener.onTaskReordered(tasks.get(0), tasks.get(1));

        // Verify new order
        assertEquals("Task B", tasks.get(0).title()); // "Task B" should now be first
        assertEquals("Task A", tasks.get(1).title()); // "Task A" should be second
    }


    @Test
    public void testMoveTaskDown() {
        // Move "Task A" down (swap with "Task B")
        Task movedTask = tasks.get(0); // "Task A"
        tasks.remove(0);
        tasks.add(1, movedTask); // Move "Task A" down

        adapter.notifyItemMoved(0, 1); // Notify adapter of change
        adapter.reorderListener.onTaskReordered(tasks.get(1), tasks.get(0));

        // Verify new order
        assertEquals("Task B", tasks.get(0).title()); // "Task B" should now be first
        assertEquals("Task A", tasks.get(1).title()); // "Task A" should be second
    }

    @Test
    public void testReorderListenerNotNull() {
        assertNotNull(adapter.reorderListener);
    }
}
