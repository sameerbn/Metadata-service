// service/DateExtractor.java
package com.example.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateExtractor {
    private static final Pattern DATE_PATTERN = Pattern.compile("(\\d{4}-\\d{2}-\\d{2}|\\d{8})");

    public static Optional<LocalDate> extractFromPath(String path) {
        Matcher matcher = DATE_PATTERN.matcher(path);
        while (matcher.find()) {
            String matched = matcher.group(1);
            try {
                if (matched.contains("-")) {
                    return Optional.of(LocalDate.parse(matched));
                } else {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                    return Optional.of(LocalDate.parse(matched, formatter));
                }
            } catch (DateTimeParseException e) {
                // Ignore and continue
            }
        }
        return Optional.empty();
    }
}
