package models;


import java.time.LocalDateTime;

public class TableColumns {

    public static class User {
        public static final String FirstName = "FirstName";
        public static final String LastName = "LastName";
        public static final String PasswordHash = "PasswordHash";
        public static final String UserName = "UserName";
        public static final String IsDeleted = "IsDeleted";
        public static final String IsDisabled = "IsDisabled";
    }

    public static class Role {
        public static final String Name = "Name";
    }

    public static class UserRole {
        public static final String UserId = "UserId";
        public static final String RoleId = "RoleId";
    }

    public static class Category {
        public static final String Name = "Name";
        public static final String Description = "Description";
    }

    public static class Item {
        public static final String LessorId = "LessorId";
        public static final String CategoryId = "CategoryId";
        public static final String Name = "Name";
        public static final String Description = "Description";
        public static final String AbleFromDate = "AbleFromDate";
        public static final String AbleToDate = "AbleToDate";
        public static final String Fee = "Fee";
        public static final String ImageBinary = "ImageBinary";
    }
}
