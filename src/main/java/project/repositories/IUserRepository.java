package project.repositories;

import project.models.User;

public interface IUserRepository {
    User addUser(String username, String password);

    User authenticate(String username, String password);
}
