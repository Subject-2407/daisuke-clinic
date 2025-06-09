package implementation.model.interfaces;

import shared.enums.Role;

public interface AuthenticableUser extends User {
    Role getRole();
    AuthenticableUser findById(int id);
}
