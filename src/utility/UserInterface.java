package utility;

import java.util.Scanner;

public class UserInterface {
    private static final String TITLE = "Daisuke Clinic";
    // color ansi codes
    public static final String GREEN = "\u001B[92m";
    public static final String BLUE = "\u001B[1;34m";
    public static final String YELLOW = "\u001B[33m";
    private static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[91m";

    // asks user to enter to update the UI
    public static void enter(Scanner scanner) {
        System.out.print(" > Press Enter to continue.. ");
        scanner.nextLine();
    }

    public static String colorize(String string, String ansiCode) {
        return ansiCode + string + RESET;
    }

    // update UI
    public static void update() {
        try {
            final String os = System.getProperty("os.name");
            if (os.contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor(); // clear console using os command
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush(); 
            }
            // program header
            System.out.println(colorize("========== ", BLUE) + TITLE + colorize(" ==========\n", BLUE));
        } catch (Exception e) {
            System.out.println("Error clearing console.");
        }
    }

    // update UI (with custom header title)
    public static void update(String header) {
        try {
            final String os = System.getProperty("os.name");
            if (os.contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor(); // clear console using os command
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush(); 
            }
            // program header
            System.out.println(colorize("========== ", BLUE) + TITLE + " (" + header + ")" + colorize(" ==========\n", BLUE));
        } catch (Exception e) {
            System.out.println("Error clearing console.");
        }
    }

    public static void createOptions(String[] options) {
        for (int i = 1; i <= options.length; i++) {
            System.out.println(colorize("[" + i + "]",YELLOW) + " " + options[i - 1]);
        }
        System.out.println(colorize("[0]", YELLOW) + " " + "Exit");
    }

    public static void info(String message) {
        System.out.println(" > " + message);
    }

    public static void success(String message) {
        System.out.println(colorize(" > " + message, GREEN));
    }

    public static void warning(String message) {
        System.out.println(colorize(" > " + message, RED));
    }
}
