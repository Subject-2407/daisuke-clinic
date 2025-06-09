package utility;

import java.util.Scanner;

public class InputV2 {
    private String input;
    private boolean isExit;

    private static final String EXIT_KEY = "0";

    private final Scanner scanner;
    private final String prompt;

    // ===== Constructor =====
    public InputV2(Scanner scanner, String message) {
        this.scanner = scanner;
        this.prompt = message;
    }

    // ===== Core InputV2 Loop =====
    private void askUntilValid(Validator validator) {
        do {
            System.out.print(prompt);
            input = scanner.nextLine().trim();

            if (EXIT_KEY.equals(input)) {
                isExit = true;
                UserInterface.info("You have exited the input.");
                break;
            }

        } while (!validator.validate(input));
    }

    // ===== Public Entry Points =====

    public InputV2 isNotEmpty() {
        askUntilValid(input -> {
            if (input.isEmpty()) {
                UserInterface.warning("The given input is empty!");
                return false;
            }
            return true;
        });
        return this;
    }

    public InputV2 isNumeric() {
        askUntilValid(input -> {
            if (!input.matches("\\d+")) {
                UserInterface.warning("The given input is not numeric!");
                return false;
            }
            return true;
        });
        return this;
    }

    public InputV2 isPositiveNumber() {
        askUntilValid(input -> {
            try {
                int value = Integer.parseInt(input);
                if (value <= 0) {
                    UserInterface.warning("InputV2 must be a positive number!");
                    return false;
                }
                return true;
            } catch (NumberFormatException e) {
                UserInterface.warning("Invalid number!");
                return false;
            }
        });
        return this;
    }

    public InputV2 isAlphabetic() {
        askUntilValid(input -> {
            if (!input.matches("[a-zA-Z\\s]+")) {
                UserInterface.warning("The given input is not alphabetic!");
                return false;
            }
            return true;
        });
        return this;
    }

    public InputV2 isAlphanumeric() {
        askUntilValid(input -> {
            if (!input.matches("[a-zA-Z0-9]+")) {
                UserInterface.warning("The given input is not alphanumeric!");
                return false;
            }
            return true;
        });
        return this;
    }

    public InputV2 isValidPhoneNumber() {
        askUntilValid(input -> {
            if (!input.matches("[+]?[0-9\\s\\-()]+")) {
                UserInterface.warning("The given input is not a valid phone number!");
                return false;
            }
            return true;
        });
        return this;
    }

    // ===== Output Getters =====

    public boolean isExit() {
        return isExit;
    }

    public String get() {
        return isExit ? null : input;
    }

    public int getAsInt() {
        if (isExit) return Integer.MIN_VALUE;
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return Integer.MIN_VALUE;
        }
    }

    @Override
    public String toString() {
        return input;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof String) {
            return input.equals(obj);
        }
        return false;
    }

    // ===== Functional Interface =====
    private interface Validator {
        boolean validate(String input);
    }
}
