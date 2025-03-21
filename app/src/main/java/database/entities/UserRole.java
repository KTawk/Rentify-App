package database.entities;

public class UserRole extends BaseEntity {
    public int UserId;
    public int RoleId;

    public UserRole() {
    }

    public UserRole(int userId, int roleId) {
        this.UserId = userId;
        this.RoleId = roleId;
    }
}
