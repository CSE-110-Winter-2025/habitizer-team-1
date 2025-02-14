package edu.ucsd.cse110.habitizer.lib.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class LapTimer {
    private final List<String> lapTimes = new ArrayList<>();

    public void recordLap() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        lapTimes.add(timestamp);
    }

    public List<String> getLapTimes() {
        return new ArrayList<>(lapTimes);
    }
}

