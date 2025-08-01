package model;

public class User {
    private final String username;
    private final String role;

    public User(String username, String role) {
        this.username = username;
        this.role = role.toLowerCase();
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }
}
