package edu.ucsd.cse110.habitizer.app.ui.task;

import edu.ucsd.cse110.habitizer.lib.domain.Task;
import java.util.List;

public interface TaskReorderListener {
        void onTaskReordered(Task task1, Task task2);
    }
