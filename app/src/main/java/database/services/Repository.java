package database.services;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import database.database.AppDatabase;
import database.entities.BaseEntity;
import database.entities.Category;
import models.TableColumns;

import java.lang.reflect.Field;
import java.util.Map;

public class Repository<T extends BaseEntity> {
    private String _tableName;
    AppDatabase _db;
    private Class<T> clazz; // Class type for T

    public Repository(AppDatabase db, String tableName, Class<T> clazz) {
        this._tableName = tableName;
        this._db = db;
        this.clazz = clazz;

        CreateTableIfNotExists();
    }


    private String GetAllQuery() {
        StringBuilder query = new StringBuilder();
        query.append("SELECT * FROM ").append(this._tableName);

        return query.toString();
    }

    private String GetByIdQuery(int id) {
        StringBuilder query = new StringBuilder(GetAllQuery());
        query.append(" WHERE ").append(this._tableName).append(".Id = ").append(id);

        return query.toString();
    }

    private String DeleteByIdQuery(int id) {
        return "DELETE FROM " + _tableName + " WHERE Id = " + id;
    }

    private void CreateTableIfNotExists() {
        SQLiteDatabase db = _db.getWritableDatabase();
        StringBuilder sql = new StringBuilder();

        sql.append("CREATE TABLE IF NOT EXISTS ")
                .append(_tableName)
                .append(" (Id INTEGER PRIMARY KEY AUTOINCREMENT, ");

        for (Field field : clazz.getDeclaredFields()) {
            String fieldName = field.getName();
            String fieldType = "";

            if (field.getType() == int.class || field.getType() == boolean.class) {
                fieldType = "INTEGER";
            } else if (field.getType() == String.class) {
                fieldType = "TEXT";
            } else if (field.getType() == float.class || field.getType() == double.class) {
                fieldType = "REAL"; // Both float and double are REAL in SQLite
            }

            sql.append(fieldName).append(" ").append(fieldType).append(", ");
        }

        sql.setLength(sql.length() - 2); // Remove the last comma and space
        sql.append(");");

        db.execSQL(sql.toString());
        db.close();
    }

    public int Count() {
        String countQuery = "SELECT COUNT(*) FROM " + _tableName;
        SQLiteDatabase db = _db.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    // Method to get all records
    public List<T> GetAll() {
        List<T> objectList = new ArrayList<>();
        SQLiteDatabase db = _db.getReadableDatabase();
        Cursor cursor = db.rawQuery(GetAllQuery(), null);

        MapDataSet(cursor, objectList);

        cursor.close();
        db.close();

        return objectList;
    }

    public List<T> Where(HashMap<String, String> filters) {
        List<T> objectList = new ArrayList<>();
        SQLiteDatabase db = _db.getReadableDatabase();

        // Construct the base query
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT * FROM ").append(_tableName);

        // Check if there are filters
        if (!filters.isEmpty()) {
            queryBuilder.append(" WHERE ");
            // Iterate over the filters to build the WHERE clause
            for (Map.Entry<String, String> entry : filters.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                queryBuilder.append(key).append(" = '").append(value).append("' AND ");
            }

            // Remove the last " AND " from the query
            queryBuilder.setLength(queryBuilder.length() - 5);
        }

        Cursor cursor = db.rawQuery(queryBuilder.toString(), null);

        MapDataSet(cursor, objectList);

        cursor.close();
        db.close();

        return objectList;
    }

    // Method to get a record by ID
    public T GetById(int id) {
        var data = new ArrayList<T>();
        SQLiteDatabase db = _db.getReadableDatabase();
        Cursor cursor = db.rawQuery(GetByIdQuery(id), null);

        MapDataSet(cursor, data);

        cursor.close();
        db.close();

        if (data.size() == 0)
            return null;

        return data.get(0);
    }

    // Method to delete a record by ID
    public void DeleteById(int id) {
        SQLiteDatabase db = _db.getWritableDatabase();
        db.execSQL(DeleteByIdQuery(id));
        db.close();
    }

    public long Insert(T object) {
        SQLiteDatabase db = _db.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Iterate over the fields of the object and populate ContentValues
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true); // Make the field accessible

            try {
                String fieldName = field.getName();
                Object fieldValue = field.get(object); // Get the field value

                // Check the field type and add to ContentValues
                if (field.getType() == int.class) {
                    values.put(fieldName, (Integer) fieldValue);
                } else if (field.getType() == String.class) {
                    values.put(fieldName, (String) fieldValue);
                } else if (field.getType() == float.class) {
                    values.put(fieldName, (Float) fieldValue);
                } else if (field.getType() == double.class) {
                    values.put(fieldName, (Double) fieldValue);
                } else if (field.getType() == boolean.class) {
                    values.put(fieldName, (Boolean) fieldValue);
                } else if (field.getType() == LocalDateTime.class) {
                    String dateTimeString = ((LocalDateTime) fieldValue).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    values.put(fieldName, dateTimeString);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        // Insert the row into the table and return the row ID
        long newRowId = db.insert(_tableName, null, values);
        db.close();
        return newRowId;
    }

    public void Update(T object) {
        SQLiteDatabase db = _db.getWritableDatabase();
        ContentValues values = new ContentValues();

        int idValue = -1;  // The Id from BaseEntity, which will be used for the WHERE clause

        // Iterate over fields of the object including inherited fields
        Class<?> currentClass = clazz;

        while (currentClass != null) {
            for (Field field : currentClass.getDeclaredFields()) {
                field.setAccessible(true); // Make the field accessible

                try {
                    String fieldName = field.getName();
                    Object fieldValue = field.get(object); // Get the field value

                    // Exclude Id and CreateTime fields (which are inherited from BaseEntity)
                    if ("Id".equals(fieldName) || "CreateTime".equals(fieldName)) {
                        if ("Id".equals(fieldName)) {
                            idValue = (int) fieldValue; // Get the Id for the WHERE clause
                        }
                        continue; // Skip Id and CreateTime fields
                    }

                    // Add the field to ContentValues based on its type
                    if (field.getType() == int.class) {
                        values.put(fieldName, (Integer) fieldValue);
                    } else if (field.getType() == String.class) {
                        values.put(fieldName, (String) fieldValue);
                    } else if (field.getType() == float.class) {
                        values.put(fieldName, (Float) fieldValue);
                    } else if (field.getType() == double.class) {
                        values.put(fieldName, (Double) fieldValue);
                    } else if (field.getType() == boolean.class) {
                        values.put(fieldName, (Boolean) fieldValue);
                    } else if (field.getType() == LocalDateTime.class) {
                        String dateTimeString = ((LocalDateTime) fieldValue).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                        values.put(fieldName, dateTimeString);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            // Move to the superclass to check for inherited fields (e.g., BaseEntity)
            currentClass = currentClass.getSuperclass();
        }

        // Ensure the object has a valid Id for the WHERE clause
        if (idValue == -1) {
            throw new IllegalArgumentException("The object must have a valid Id field.");
        }

        // Perform the update using the Id in the WHERE clause
        db.update(_tableName, values, "Id = ?", new String[]{String.valueOf(idValue)});
        db.close();
    }

    public void Update(int Id, String columnName, String columnNewValue) {
        SQLiteDatabase db = _db.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Check if the column exists in the table
        boolean columnExists = false;
        for (Field field : clazz.getDeclaredFields()) {
            if (field.getName().equals(columnName)) {
                columnExists = true;
                break;
            }
        }

        // passed column does not exist
        if (!columnExists) {
            throw new IllegalArgumentException("Column '" + columnName + "' does not exist in the table.");
        }

        values.put(columnName, columnNewValue);

        db.update(_tableName, values, "Id = ?", new String[]{String.valueOf(Id)});
        db.close();
    }


    public boolean Exists(HashMap<String, String> filters) {
        return !Where(filters).isEmpty();
    }

    // Helpers

    private void MapDataSet(Cursor cursor, List<T> objectList) {
        // Process the results
        if (cursor.moveToFirst()) {
            do {
                try {
                    // Create a new instance of the main entity (T)
                    T mainObject = clazz.newInstance();

                    // Map fields from the current class and its superclasses
                    Class<?> currentClass = clazz;

                    // Iterate over the class hierarchy (including base classes)
                    while (currentClass != null) {
                        for (Field field : currentClass.getDeclaredFields()) {
                            field.setAccessible(true); // Allow access to private fields
                            String fieldName = field.getName();
                            int columnIndex = cursor.getColumnIndex(fieldName);

                            // Ensure the field exists in the result set
                            if (columnIndex != -1 && !cursor.isNull(columnIndex)) {
                                if (field.getType() == int.class) {
                                    field.setInt(mainObject, cursor.getInt(columnIndex));
                                } else if (field.getType() == String.class) {
                                    field.set(mainObject, cursor.getString(columnIndex));
                                } else if (field.getType() == float.class) {
                                    field.setFloat(mainObject, cursor.getFloat(columnIndex));
                                } else if (field.getType() == double.class) {
                                    field.setDouble(mainObject, cursor.getDouble(columnIndex));
                                } else if (field.getType() == boolean.class) {
                                    // Read the integer value from the cursor and map to boolean
                                    field.setBoolean(mainObject, cursor.getInt(columnIndex) == 1);
                                } else if (field.getType() == LocalDateTime.class) {
                                    // Parse LocalDateTime from the ISO-8601 string
                                    String dateTimeString = cursor.getString(columnIndex);
                                    LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                                    field.set(mainObject, dateTime);
                                }
                            }
                        }

                        // Move to the superclass to map inherited fields
                        currentClass = currentClass.getSuperclass();
                    }

                    // Add the main object (and any related data) to the list
                    objectList.add(mainObject);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }
    }
}
