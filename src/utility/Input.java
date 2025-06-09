package utility;

import java.util.Scanner;
import java.util.function.Predicate;
import java.util.function.Function;
import adt.LinkedList;

public class Input {
    private final Scanner scanner;
    private String input;
    private final String prompt;
    private boolean isExit;
    private final LinkedList<Validator> validators = new LinkedList<>();
    private Function<String, ?> formatter = s -> s; // Default formatter (identity)
    private boolean isIntegerFormat = false;

    public Input(Scanner scanner, String prompt) {
        if (scanner == null) {
            throw new IllegalArgumentException("Scanner cannot be null");
        }
        this.scanner = scanner;
        this.prompt = prompt == null ? "" : prompt;
    }

    public Input validate() {
        while (true) {
            System.out.print(prompt);
            input = scanner.nextLine().trim();
            
            if (input.equals("0")) {
                isExit = true;
                break;
            }

            boolean allPassed = true;
            Object[] validatorList = validators.toArray();
            for (Object obj : validatorList) {
                Validator v = (Validator) obj;
                if (!v.predicate.test(input)) {
                    UserInterface.warning(v.errorMessage);
                    allPassed = false;
                    break;
                }
            }

            if (allPassed) break;
        }
        return this;
    }

    /* ======= Validations ======= */
    public Input isNotEmpty() {
        validators.insert(new Validator(
            input -> input != null && !input.trim().isEmpty(), 
            "The given input is empty!"
        ));
        return this;
    }

    public Input isAlphabetic() {
        validators.insert(new Validator(
            input -> input.matches("[a-zA-Z\\s]+"), 
            "The given input is not alphabetic!"
        ));
        return this;
    }

    public Input isNumeric() {
        validators.insert(new Validator(
            input -> input.matches("-?\\d+"), 
            "The given input is not numeric!"
        ));
        return this;
    }

    public Input isPositiveNumber() {
        validators.insert(new Validator(
            input -> {
                try {
                    return Integer.parseInt(input) > 0;
                } catch (NumberFormatException e) {
                    return false;
                }
            }, 
            "Input must be a positive number!"
        ));
        return this;
    }

    public Input isAlphanumeric() {
        validators.insert(new Validator(
            input -> input.matches("[a-zA-Z0-9]+"), 
            "The given input is not alphanumeric!"
        ));
        return this;
    }

    public Input isValidPhoneNumber() {
        validators.insert(new Validator(
            input -> input.matches("[+]?[0-9\\s\\-\\(\\)]+"), 
            "The given input is not a valid phone number!"
        ));
        return this;
    }

    /* ======= Formatters ======= */
    public Input asInteger() {
        this.formatter = s -> {
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                return Integer.MIN_VALUE;
            }
        };
        this.isIntegerFormat = true;
        return this;
    }

    public Input asDouble() {
        this.formatter = Double::parseDouble;
        this.isIntegerFormat = false;
        return this;
    }

    public Input asUpperCase() {
        this.formatter = String::toUpperCase;
        this.isIntegerFormat = false;
        return this;
    }

    public Input asLowerCase() {
        this.formatter = String::toLowerCase;
        this.isIntegerFormat = false;
        return this;
    }

    /* ======= Getters ======= */
    @SuppressWarnings("unchecked")
    public <T> T get() {
        if (isExit) return isIntegerFormat ? (T) Integer.valueOf(Integer.MIN_VALUE) : null;
        
        try {
            T result = (T) formatter.apply(input);
            if (isIntegerFormat && result instanceof Integer && (Integer)result == Integer.MIN_VALUE) {
                UserInterface.warning("Invalid integer format!");
            }
            return result;
        } catch (Exception e) {
            UserInterface.warning("Failed to format input: " + e.getMessage());
            return isIntegerFormat ? (T) Integer.valueOf(Integer.MIN_VALUE) : null;
        }
    }

    public String getString() {
        return get();
    }

    public Integer getInteger() {
        Integer result = asInteger().get();
        return result == null ? Integer.MIN_VALUE : result;
    }

    public Double getDouble() {
        return asDouble().get();
    }

    public boolean isExit() { 
        return isExit; 
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof String) {
            return input.equals(obj);
        }
        return false;
    }

    @Override
    public String toString() { 
        return input; 
    }

    private static class Validator {
        final Predicate<String> predicate;
        final String errorMessage;

        Validator(Predicate<String> predicate, String errorMessage) {
            this.predicate = predicate;
            this.errorMessage = errorMessage;
        }
    }
}