package com.migmeninfo.cipservice.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
public class DateUtils {
    public static LocalDate parseLocalDate(String input) {
        if (ObjectUtils.isEmpty(input)) return null;
        try {
            if (input.contains("-")) {
                String[] split = input.split("-");
                if (split.length > 2) {
                    if (split[0].length() < 4) {
                        int integer = NumberUtils.toInt(split[1], 0);
                        if (integer == 0) return null;
                        if (integer > 12) {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
                            return LocalDate.parse(input, formatter);
                        }
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                        return LocalDate.parse(input, formatter);
                    }
                }
            }
            if (input.contains("/")) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                return LocalDate.parse(input, formatter);
            }
            return LocalDate.parse(input);
        } catch (Exception e) {
            log.error("parse-local-date-error: {}", e.getMessage(), e);
            return null;
        }
    }
}
