package database.entities;

import java.time.LocalDateTime;

public class BaseEntity {
    public int Id;
    public LocalDateTime CreateTime;

    BaseEntity() {
        this.CreateTime = LocalDateTime.now();
    }
}
