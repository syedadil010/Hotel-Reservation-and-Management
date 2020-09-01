package OnlineBookingSystem.ModelClasses;

/**
 * Abstract User class to store username and password.
 */
public abstract class User {

    public User(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    private int id = -1;
    private String username;
    private String password;

    public int getId() {return id;}
    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
