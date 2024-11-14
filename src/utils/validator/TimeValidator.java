package utils.validator;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class TimeValidator implements Validator {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Override
    public boolean eval(String str) {
        if (str == null) return false;
        try {
            LocalTime time = LocalTime.parse(str, TIME_FORMATTER);
            String[] parts = str.split(":");
            return !parts[0].equals("24");
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    @Override
    public String reason() {
        return "Time must be in the format of HH:mm:ss (24-hour format)";
    }
}