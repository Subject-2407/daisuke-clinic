package implementation.model.interfaces;

// Interface for all type of users

public interface User extends Identifiable {
    boolean validatePassword(String password);
}
