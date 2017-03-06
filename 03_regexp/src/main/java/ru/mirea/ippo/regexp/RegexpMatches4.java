package ru.mirea.ippo.regexp;

import java.util.Arrays;
import java.util.regex.Pattern;

public final class RegexpMatches4 {
    public static final Pattern EMAIL_PATTERN = Pattern.compile("^([a-z0-9_\\.-]+)@([a-z0-9_\\.-]+)\\.([a-z\\.]{2,6})$",
            Pattern.CASE_INSENSITIVE);
    public static final Pattern IP_ADDRESS_PATTERN =
            Pattern.compile("(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)");

    private static void patternSplit() {
        Pattern pattern = Pattern.compile(":|;");
        String[] animals = pattern.split("cat:dog;bird:cow");
        Arrays.asList(animals).forEach(animal -> System.out.print(animal + " "));
    }
}
