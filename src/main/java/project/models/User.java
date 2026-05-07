package project.models;

public class User {
    private final String username;
    private final String password;
    private final int id;
    private final boolean admin;

    public User(String username, String password, int id, boolean admin) {
        this.username = username;
        this.password = password;
        this.id = id;
        this.admin = admin;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getId() {
        return id;
    }

    public boolean isAdmin() {
        return admin;
    }
}
