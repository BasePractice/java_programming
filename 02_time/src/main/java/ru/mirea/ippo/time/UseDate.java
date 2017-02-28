package ru.mirea.ippo.time;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public final class UseDate {
    private static final DateFormat format24 = new SimpleDateFormat("HH:mm:ss dd.MM.YYYY");
    private static final DateFormat format12 = new SimpleDateFormat("hh:mm:ss dd.MM.YYYY");

    public static void main(String[] args) {
        Date local = new Date();
        System.out.println(String.format("Now default    : %s", local.toString()));
        System.out.println(String.format("Now format24   : %s", format24.format(local)));
        System.out.println(String.format("Now format12   : %s", format12.format(local)));

        final Calendar plus = Calendar.getInstance(TimeZone.getDefault());
        plus.setTime(local);
        plus.set(Calendar.MILLISECOND, plus.get(Calendar.MILLISECOND) + 1);
        System.out.println(String.format("Больше local   : %s", gt(local, local)));
        System.out.println(String.format("Больше plus    : %s", gt(local, plus.getTime())));
        System.out.println(String.format("Больше self    : %s", gt(plus.getTime(), local)));

        TimeZone globalTimeZone = TimeZone.getTimeZone("Asia/Tokyo");
        format24.setTimeZone(globalTimeZone);
        System.out.println(String.format("Global format24: %s", format24.format(local)));
    }

    private static boolean gt(Date date, Date then) {
        return date.before(then);
    }
}
