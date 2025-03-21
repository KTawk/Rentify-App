package models;

public class RequestedItemsInfoContainer {
    public  String Name;
    public  String Category;
    public  String Desc;
    public  int ItemId;

    public RequestedItemsInfoContainer(String name, String category, String desc, int itemId) {
        this.Name = name;
        this.Category = category;
        this.Desc = desc;
        this.ItemId = itemId;
    }
}
