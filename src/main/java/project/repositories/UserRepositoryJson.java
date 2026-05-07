package project.repositories;

import java.util.LinkedHashMap;
import java.util.Map;

import project.models.User;

public class UserRepositoryJson implements IUserRepository {
    private final Map<String, User> users = new LinkedHashMap<>();
    private int nextId = 1;

    @Override
    public User addUser(String username, String password) {
        if (users.containsKey(username)) {
            return null;
        }

        User user = new User(username, password, nextId++, false);
        users.put(username, user);
        return user;
    }

    @Override
    public User authenticate(String username, String password) {
        User user = users.get(username);
        if (user == null || !user.getPassword().equals(password)) {
            return null;
        }
        return user;
    }
}
