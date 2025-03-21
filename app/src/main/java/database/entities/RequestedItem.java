package database.entities;

public class RequestedItem extends BaseEntity {
    public int ItemId;
    public int RenterId; // UserId of renter
    public int LesserId; // UserId of Lesser
    public boolean IsApproved;
    public boolean HasDecision;

    public RequestedItem() {

    }

    public RequestedItem(int itemId, int renterId, int lesserId, boolean isApproved) {
        this.IsApproved = isApproved;
        this.ItemId = itemId;
        this.RenterId = renterId;
        this.LesserId = lesserId;
        this.HasDecision = false;
    }
}
