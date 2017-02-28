package ru.mirea.ippo.time;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

public final class UseJodaTime {

    private static final DateTimeFormatter formatISO = DateTimeFormat.longDateTime();
    private static final DateTimeFormatter formatPattern = DateTimeFormat.forPattern("HH:mm:ss dd.MM.YYYY");
    private static final DateTimeFormatter formatBuild = new DateTimeFormatterBuilder()
            .appendHourOfDay(2)
            .appendLiteral('.')
            .appendMinuteOfHour(2)
            .appendLiteral('.')
            .appendSecondOfMinute(2)
            .appendLiteral(' ')
            .appendDayOfMonth(2)
            .appendLiteral('.')
            .appendMonthOfYear(2)
            .appendLiteral('.')
            .appendYear(4, 10)
            .toFormatter();


    public static void main(String[] args) {
        LocalDateTime local = LocalDateTime.now();
        System.out.println(String.format("Now default      : %s", local.toString()));
        System.out.println(String.format("Now formatISO    : %s", local.toString(formatISO)));
        System.out.println(String.format("Now formatPattern: %s", local.toString(formatPattern)));
        System.out.println(String.format("Now formatBuild  : %s", local.toString(formatBuild)));

        final LocalDateTime plus = local.plusMillis(1);
        System.out.println(String.format("Больше local: %s", gt(local, local)));
        System.out.println(String.format("Больше plus : %s", gt(local, plus)));
        System.out.println(String.format("Больше self : %s", gt(plus, local)));
    }

    private static boolean gt(LocalDateTime date, LocalDateTime then) {
        return date.isBefore(then);
    }
}
