package edu.ucsd.cse110.habitizer.lib.domain;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.List;

public class LapTimerTest {
    private LapTimer lapTimer;

    @Before
    public void setUp() {
        lapTimer = new LapTimer();
    }

    @Test
    public void testInitialState() {
        assertTrue("Lap times list should be empty initially", lapTimer.getLapTimes().isEmpty());
    }

    @Test
    public void testRecordSingleLap() {
        lapTimer.recordLap();
        List<String> lapTimes = lapTimer.getLapTimes();
        assertEquals("Lap times list should have one entry after recording a lap", 1, lapTimes.size());
        assertNotNull("Lap time should not be null", lapTimes.get(0));
        assertFalse("Lap time should not be empty", lapTimes.get(0).isEmpty());
    }

    @Test
    public void testRecordMultipleLaps() {
        lapTimer.recordLap();
        lapTimer.recordLap();
        lapTimer.recordLap();

        List<String> lapTimes = lapTimer.getLapTimes();
        assertEquals("Lap times list should have three entries after recording three laps", 3, lapTimes.size());
    }

    @Test
    public void testLapTimesAreImmutable() {
        lapTimer.recordLap();
        List<String> lapTimes = lapTimer.getLapTimes();
        lapTimes.add("Fake Time"); // Attempt to modify

        assertEquals("getLapTimes() should return a new list, ensuring immutability", 1, lapTimer.getLapTimes().size());
    }
}
