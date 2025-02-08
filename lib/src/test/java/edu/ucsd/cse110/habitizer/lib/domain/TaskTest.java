package edu.ucsd.cse110.habitizer.lib.domain;

import static org.junit.Assert.*;

import org.junit.Test;

public class TaskTest {
    @Test
    public void testGetters(){
        var task = new Task(1, "Brush Teeth");
        assertEquals(Integer.valueOf(1), task.id());
        assertEquals("Brush Teeth", task.title());
        assertEquals(false, task.complete());
    }

    @Test
    public void testSetters(){
        var task = new Task(1, "Brush Teeth");
        task.setComplete(true);
        task.setTitle("Eat food");
        assertEquals(true, task.complete());
        assertEquals("Eat food", task.title());
    }

    @Test
    public void testEquals() {
        var task1 = new Task(1, "Brush Teeth");
        var task2 = new Task(1, "Brush Teeth");
        var task3 = new Task(2, "Brush Teeth");

        assertEquals(task1, task2);
        assertNotEquals(task1, task3);
    }

}
