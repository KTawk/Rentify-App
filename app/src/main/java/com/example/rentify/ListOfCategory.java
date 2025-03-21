package com.example.rentify;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.List;

import database.context.DataContext;
import database.entities.Category;
import helpers.CollectionHelper;
import helpers.models.Pair;
import models.TableColumns;

public class ListOfCategory extends AppCompatActivity {

    private TableLayout categoryTable;
    private DataContext _dbContext;
    private ImageButton addButton, deleteButton;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_category);
        EdgeToEdge.enable(this);

        categoryTable = findViewById(R.id.categoryTable);
        addButton = findViewById(R.id.button_add);
        deleteButton = findViewById(R.id.button_delete);
        backButton = findViewById(R.id.button_back);

        _dbContext = new DataContext(this);

        // Load the existing categories and display them in the table
        refreshCategoryList();

        // Set the add button Listener to open the dialog for adding a new category
        addButton.setOnClickListener(v -> openAddCategoryDialog());

        // Set the delete button Listener to remove selected categories
        deleteButton.setOnClickListener(v -> deleteSelectedCategories());

        // Set the back button Listener to return to the welcome activity
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(ListOfCategory.this, WelcomeActivity.class);
            startActivity(intent);
            finish();
        });

        // Set visibility for the back button based on user role (e.g., admin)
        boolean isAdmin = checkIfUserIsAdmin();
        if (isAdmin) {
            backButton.setVisibility(View.VISIBLE);
        } else {
            backButton.setVisibility(View.GONE);
        }
    }

    // Mock method to check if the user is an admin
    private boolean checkIfUserIsAdmin() {
        // Replace with actual logic for checking if the user is an admin
        return true; // This is just a placeholder
    }

    // Method to refresh the category list in the table
    private void refreshCategoryList() {
        // Clear the table except for the header row
        categoryTable.removeViews(1, categoryTable.getChildCount() - 1);

        // Fetch categories from the database
        List<Category> categoryList = _dbContext.Categories.GetAll();

        // Loop through the list and add each category to the table
        for (Category category : categoryList) {
            addCategoryRow(category);
        }
    }

    // Method to add a category row to the table
    private void addCategoryRow(Category category) {
        TableRow row = new TableRow(this);
        //row.setBackgroundColor(Color.parseColor("#D3D3D3"));


        // Add CheckBox for selecting the category
        CheckBox checkBox = new CheckBox(this);
        row.addView(checkBox);

        // Add Name and Description TextViews
        TextView nameTextView = new TextView(this);
        nameTextView.setText(category.Name);
        row.addView(nameTextView);

        TextView descriptionTextView = new TextView(this);
        descriptionTextView.setText(category.Description);
        row.addView(descriptionTextView);

        // Add Edit button
        Button editButton = new Button(this);
        editButton.setText("Edit");
        editButton.setOnClickListener(v -> openEditCategoryDialog(category));
        row.addView(editButton);

        categoryTable.addView(row);
    }

    // Method to handle adding a new category
    private void openAddCategoryDialog() {
        // Inflate the dialog view
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_category, null);

        // Get reference to EditTexts and Button
        EditText categoryNameInput = dialogView.findViewById(R.id.edit_category_name);
        EditText categoryDescriptionInput = dialogView.findViewById(R.id.edit_category_description);
        Button saveButton = dialogView.findViewById(R.id.btn_save_category);

        // Create the dialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(dialogView);
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        // Handle save button click
        saveButton.setOnClickListener(v -> {
            String categoryName = categoryNameInput.getText().toString();
            String categoryDescription = categoryDescriptionInput.getText().toString();

            if (!TextUtils.isEmpty(categoryName) && !TextUtils.isEmpty(categoryDescription)) {
                // Check if the category with the same name already exists
                if (_dbContext.Categories.Exists(CollectionHelper.WithFilters(
                        new Pair(TableColumns.Category.Name, categoryName)))) {
                    // Show a message that the category already exists
                    Toast.makeText(this, "Category with this name already exists", Toast.LENGTH_SHORT).show();
                } else {
                    // Create a new category object
                    Category newCategory = new Category(categoryName, categoryDescription);

                    // Save the category to the database
                    _dbContext.Categories.Insert(newCategory);

                    // Refresh the table
                    refreshCategoryList();

                    // Dismiss the dialog
                    dialog.dismiss();
                }
            } else {
                Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openEditCategoryDialog(Category category) {
        // Inflate the dialog view
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_category, null);

        // Get reference to EditTexts and Button
        EditText categoryNameInput = dialogView.findViewById(R.id.edit_category_name);
        EditText categoryDescriptionInput = dialogView.findViewById(R.id.edit_category_description);
        Button saveButton = dialogView.findViewById(R.id.btn_save_category);

        // Set the initial values in the dialog
        categoryNameInput.setText(category.Name);
        categoryDescriptionInput.setText(category.Description);

        // Create the dialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(dialogView);
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        // Handle save button click
        saveButton.setOnClickListener(v -> {
            String updatedCategoryName = categoryNameInput.getText().toString();
            String updatedCategoryDescription = categoryDescriptionInput.getText().toString();

            if (!TextUtils.isEmpty(updatedCategoryName) && !TextUtils.isEmpty(updatedCategoryDescription)) {
                // Update the category object
                category.Name = updatedCategoryName;
                category.Description = updatedCategoryDescription;

                // Update the category in the database
                _dbContext.Categories.Update(category);

                // Refresh the table
                refreshCategoryList();

                // Dismiss the dialog
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to delete selected categories
    private void deleteSelectedCategories() {
        int rowCount = categoryTable.getChildCount();
        boolean atLeastOneChecked = false;
        for (int i = 1; i < rowCount; i++) { // Start from 1 to skip the header row
            TableRow row = (TableRow) categoryTable.getChildAt(i);
            CheckBox checkBox = (CheckBox) row.getChildAt(0); // The CheckBox is the first child

            if (checkBox.isChecked()) {
                atLeastOneChecked = true; // Mark that at least one box is checked
                TextView nameTextView = (TextView) row.getChildAt(1); // The second child is the Name TextView
                String categoryName = nameTextView.getText().toString();

                var category = _dbContext.Categories
                        .Where(CollectionHelper.WithFilter(TableColumns.Category.Name, categoryName))
                        .stream()
                        .findFirst()
                        .orElseThrow();

                _dbContext.Categories.DeleteById(category.Id);
            }
        }
        if (atLeastOneChecked) {
            // Refresh the table after deletion
            refreshCategoryList();
        } else {
            Toast.makeText(this, "Please select at least one category", Toast.LENGTH_SHORT).show();
        }
    }
}
