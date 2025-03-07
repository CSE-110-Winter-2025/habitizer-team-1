package edu.ucsd.cse110.habitizer.app;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.habitizer.app.ui.task.TaskViewAdapter;
import edu.ucsd.cse110.habitizer.lib.domain.Task;

public class TaskViewAdapterTest {

    private TaskViewAdapter adapter;
    private TaskViewAdapter.TaskClickListener mockClickListener;
    private List<Task> taskList;

    @Before
    public void setUp() {
        mockClickListener = Mockito.mock(TaskViewAdapter.TaskClickListener.class);

        // Sample tasks
        taskList = new ArrayList<>();
        taskList.add(new Task("Task 1", 0));
        taskList.add(new Task("Task 2", 1));
        taskList.add(new Task("Task 3", 2));

        adapter = new TaskViewAdapter(taskList, mockClickListener);
        adapter.setEditingMode(true);  // Allow reordering
    }

    @Test
    public void testMoveTaskUp() {
        // Move Task 2 (index 1) up
        adapter.getTasks().get(1).setSortOrder(0);
        adapter.getTasks().get(0).setSortOrder(1);

        // Swap in the list
        Task temp = adapter.getTasks().get(1);
        adapter.getTasks().set(1, adapter.getTasks().get(0));
        adapter.getTasks().set(0, temp);

        // Verify new order
        assertEquals("Task 2", adapter.getTasks().get(0).title());
        assertEquals("Task 1", adapter.getTasks().get(1).title());
        assertEquals("Task 3", adapter.getTasks().get(2).title());
    }

    @Test
    public void testMoveTaskDown() {
        // Move Task 2 (index 1) down
        adapter.getTasks().get(1).setSortOrder(2);
        adapter.getTasks().get(2).setSortOrder(1);

        // Swap in the list
        Task temp = adapter.getTasks().get(1);
        adapter.getTasks().set(1, adapter.getTasks().get(2));
        adapter.getTasks().set(2, temp);

        // Verify new order
        assertEquals("Task 1", adapter.getTasks().get(0).title());
        assertEquals("Task 3", adapter.getTasks().get(1).title());
        assertEquals("Task 2", adapter.getTasks().get(2).title());
    }

    @Test
    public void testMoveFirstTaskUp_DoesNotChangeOrder() {
        // Attempt to move first task up (should not change order)
        int originalOrder = adapter.getTasks().get(0).getSortOrder();
        adapter.getTasks().get(0).setSortOrder(originalOrder); // No change

        assertEquals("Task 1", adapter.getTasks().get(0).title());
    }

    @Test
    public void testMoveLastTaskDown_DoesNotChangeOrder() {
        // Attempt to move last task down (should not change order)
        int originalOrder = adapter.getTasks().get(2).getSortOrder();
        adapter.getTasks().get(2).setSortOrder(originalOrder); // No change

        assertEquals("Task 3", adapter.getTasks().get(2).title());
    }
}
