package database.entities;


public class User extends BaseEntity {
    public String FirstName;
    public String LastName;
    public String PasswordHash;
    public String UserName;
    public boolean IsDeleted;
    public boolean IsDisabled;

    public User() {

    }

    public User(String firstName, String lastName, String userName, String passwordHash) {
        this.FirstName = firstName;
        this.LastName = lastName;
        this.PasswordHash = passwordHash;
        this.UserName = userName;
        this.IsDeleted = false;
        this.IsDisabled = false;
    }

    public User(String firstName, String lastName, String userName, String passwordHash, boolean isDeleted, boolean isDisabled) {
        this.FirstName = firstName;
        this.LastName = lastName;
        this.PasswordHash = passwordHash;
        this.UserName = userName;
        this.IsDeleted = isDeleted;
        this.IsDisabled = isDisabled;
    }
}
