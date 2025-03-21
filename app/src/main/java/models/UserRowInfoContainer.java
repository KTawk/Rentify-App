package models;

public class UserRowInfoContainer {
    public int UserId;
    public String DisplayText;
    public boolean Status;

    public UserRowInfoContainer(int userId, String displayText, Boolean status) {
        this.UserId = userId;
        this.DisplayText = displayText;
        this.Status = status;
    }


    // Getters and Setters
    public String getDisplayText() {
        return DisplayText;
    }

    public void setDisplayText(String text) {
        this.DisplayText = text;
    }

    public boolean isSwitchOn() {
        return Status;
    }

    public void setSwitchOn(boolean status) {
        Status = status;
    }
}
