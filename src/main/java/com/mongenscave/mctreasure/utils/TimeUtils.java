package com.mongenscave.mctreasure.utils;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class TimeUtils {
    @NotNull
    public static String formatTime(long timeInMillis) {
        if (timeInMillis <= 0) return "0:00";

        long totalSeconds = timeInMillis / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        if (hours > 0) return String.format("%d:%02d:%02d", hours, minutes, seconds);
        else return String.format("%d:%02d", minutes, seconds);
    }
}
