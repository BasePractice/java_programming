package ru.mirea.ippo.time;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.ChronoUnit;

import static java.time.temporal.ChronoField.*;

public final class UseLocalDateTime {

    private static final DateTimeFormatter formatISO = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final DateTimeFormatter formatPattern = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.YYYY");
    private static final DateTimeFormatter formatBuild = new DateTimeFormatterBuilder()
            .appendValue(HOUR_OF_DAY, 2)
            .appendLiteral('.')
            .appendValue(MINUTE_OF_HOUR, 2)
            .appendLiteral('.')
            .appendValue(SECOND_OF_MINUTE, 2)
            .appendLiteral(' ')
            .appendValue(DAY_OF_MONTH, 2)
            .appendLiteral('.')
            .appendValue(MONTH_OF_YEAR, 2)
            .appendLiteral('.')
            .appendValue(YEAR, 4, 10, SignStyle.NOT_NEGATIVE)
            .toFormatter();

    public static void main(String[] args) {
        LocalDateTime local = LocalDateTime.now();
        System.out.println(String.format("Now default      : %s", local.toString()));
        System.out.println(String.format("Now formatISO    : %s", formatISO.format(local)));
        System.out.println(String.format("Now formatPattern: %s", formatPattern.format(local)));
        System.out.println(String.format("Now formatBuild  : %s", formatBuild.format(local)));

        final LocalDateTime plus = local.plus(1, ChronoUnit.MILLIS);
        System.out.println(String.format("Больше local: %s", gt(local, local)));
        System.out.println(String.format("Больше plus : %s", gt(local, plus)));
        System.out.println(String.format("Больше self : %s", gt(plus, local)));
    }

    private static boolean gt(LocalDateTime date, LocalDateTime then) {
        return date.isBefore(then);
    }
}
