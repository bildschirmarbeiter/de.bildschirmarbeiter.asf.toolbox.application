package de.bildschirmarbeiter.asf.toolbox.application;

import java.util.Scanner;

public class Helper {

    public static String message(final String format, Object... args) {
        return String.format(format, args);
    }

    public static String line(final String string) {
        final Scanner scanner = new Scanner(string);
        if (scanner.hasNextLine()) {
            return scanner.nextLine();
        }
        return string;
    }

}
