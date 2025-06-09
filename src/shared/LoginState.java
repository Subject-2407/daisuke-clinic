package shared;

import shared.enums.Role;

public class LoginState {
    private static Role role = Role.NONE;
    private static int loginId = -1;
    private static boolean loggedIn = false;

    public static Role getRole() { return role; }
    public static int getLoginId() { return loginId; }
    public static boolean isLoggedIn() { return loggedIn; }

    public static void login(Role newRole, int newLoginId) { 
        role = newRole; 
        loginId = newLoginId;
        loggedIn = true;
    }

    public static void logout() {
        role = Role.NONE;
        loginId = -1;
        loggedIn = false;
    }
}
