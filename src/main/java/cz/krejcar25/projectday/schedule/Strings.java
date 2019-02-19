package cz.krejcar25.projectday.schedule;

import java.util.ResourceBundle;

public final class Strings {
    private static final ResourceBundle strings = ResourceBundle.getBundle("strings");

    public static String get(String key) {
        return strings.containsKey(key) ? strings.getString(key) : key;
    }
}
