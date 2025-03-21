package database.entities;

import java.time.LocalDateTime;

public class Item extends BaseEntity {
    public int LessorId; // UserId of Lessor
    public int CategoryId;
    public String Name;
    public String Description;
    public LocalDateTime AbleFromDate;
    public LocalDateTime AbleToDate;
    public double Fee;
    public String ImageBinary;

    public Item() {

    }

    public Item(int lessorId, int categoryId, String name, String description,
                LocalDateTime ableFromDate, LocalDateTime ableToDate, double fee,
                String imageBinary) {
        this.LessorId = lessorId;
        this.CategoryId = categoryId;
        this.Name = name;
        this.Description = description;
        this.AbleFromDate = ableFromDate;
        this.AbleToDate = ableToDate;
        this.Fee = fee;
        this.ImageBinary = imageBinary;
    }
}
