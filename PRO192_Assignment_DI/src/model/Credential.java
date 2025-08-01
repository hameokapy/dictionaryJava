package model;

public class Credential {
    private final String password;
    private final String role;

    public Credential(String password, String role){
        this.password = password;
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }
}
