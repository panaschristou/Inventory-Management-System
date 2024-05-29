package Backend;

public class User {
    private String username;
    private String role; // e.g., "admin", "user"

    public User(String username, String role) {
        this.username = username;
        this.role = role;
    }

    // Getter for username
    public String getUsername() {
        return username;
    }

    // Setter for username
    public void setUsername(String username) {
        this.username = username;
    }

    // Getter for role
    public String getRole() {
        return role;
    }

    // Setter for role
    public void setRole(String role) {
        this.role = role;
    }
    
    @Override
    public String toString() {
        return "User{" +
               "username='" + username + '\'' +
               ", role='" + role + '\'' +
               '}';
    }
}
