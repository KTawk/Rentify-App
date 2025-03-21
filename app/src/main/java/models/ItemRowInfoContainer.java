package models;

import java.time.LocalDateTime;

public class ItemRowInfoContainer {

    public int ItemId;
    public int LessorId;
    public int CategoryId;
    public String Name;
    public String Description;
    public LocalDateTime AbleFromDate;
    public LocalDateTime AbleToDate;
    public double Fee;
    public String ImageBinary;

    public ItemRowInfoContainer(int itemId,int lessorId, int categoryId, String name, String description,
                                LocalDateTime ableFromDate, LocalDateTime ableToDate, double fee,
                                String imageBinary) {
        this.ItemId = itemId;
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
