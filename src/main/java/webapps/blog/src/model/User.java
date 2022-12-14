package webapps.blog.src.model;

public class User {
    public String username;
    public String firstName;
    public String lastName;
    public String email;
    public String password;
    public String salt;

    public User(String username, String firstName, String lastName, String email, String password, String salt) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.salt = salt;
    }
}
