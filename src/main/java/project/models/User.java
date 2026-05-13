package project.models;

public class User {
    public String username;
    private String password;
    private int id;
    private boolean isAdmin = false;

    public User() {}

    public User(String username, String password, int id, boolean isAdmin) {
        this.username = username;
        this.password = password;
        this.id = id;
        this.isAdmin = isAdmin;
    }

    public boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
