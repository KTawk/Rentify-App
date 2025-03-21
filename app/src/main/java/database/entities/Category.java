package database.entities;

public class Category extends BaseEntity {
    public String Name;
    public String Description;

    public Category() {
    }

    public Category(String name, String description) {
        this.Name = name;
        this.Description = description;
    }
}
