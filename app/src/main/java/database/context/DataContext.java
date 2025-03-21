package database.context;

import android.content.Context;

import database.database.AppDatabase;
import database.entities.Category;
import database.entities.Item;
import database.entities.RequestedItem;
import database.entities.Role;
import database.entities.User;
import database.entities.UserRole;
import models.TableNames;
import database.services.Repository;

public class DataContext {
    private final AppDatabase _db;

    public Repository<User> Users;
    public Repository<Role> Roles;
    public Repository<UserRole> UserRoles;
    public Repository<Category> Categories;
    public Repository<Item> Items;
    public Repository<RequestedItem> RequestedItems;

    public DataContext(Context context) {
        _db = new AppDatabase(context);
        this.Roles = new Repository<Role>(_db, TableNames.Role, Role.class);
        this.Users = new Repository<User>(_db, TableNames.User, User.class);
        this.UserRoles = new Repository<UserRole>(_db, TableNames.UserRole, UserRole.class);
        this.Categories = new Repository<Category>(_db, TableNames.Category, Category.class);
        this.Items = new Repository<Item>(_db, TableNames.Item, Item.class);
        this.RequestedItems = new Repository<RequestedItem>(_db, TableNames.RequestedItem, RequestedItem.class);

        InitDatabase();
    }

    private void InitDatabase() {
        if (Roles.Count() == 0) {
            Roles.Insert(new Role("Admin"));
            Roles.Insert(new Role("Renter"));
            Roles.Insert(new Role("Lesser"));
        }
    }
}
