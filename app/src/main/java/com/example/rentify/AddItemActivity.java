package com.example.rentify;

import database.context.DataContext;
import database.entities.Category;
import database.entities.Item;
import helpers.ImageHelper;
import models.UserInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Bitmap;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import androidx.annotation.Nullable;


public class AddItemActivity extends AppCompatActivity {

    private EditText itemName, itemDescription, itemFee, startDate, endDate;
    private TextView currencyLabel;
    private Spinner categorySpinner;
    private Button uploadImageButton, saveButton, backButton;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView uploadedImageView;
    private int selectedCategoryId = -1;
    private DataContext _dbContext;
    private static final int CAPTURE_IMAGE_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        itemName = findViewById(R.id.item_name);
        itemDescription = findViewById(R.id.item_description);
        itemFee = findViewById(R.id.item_fee);
        currencyLabel = findViewById(R.id.currency_label);
        startDate = findViewById(R.id.start_date);
        endDate = findViewById(R.id.end_date);
        categorySpinner = findViewById(R.id.category_spinner);
        uploadedImageView = findViewById(R.id.uploadedImageView);
        uploadImageButton = findViewById(R.id.uploadImageButton);
        saveButton = findViewById(R.id.save_button);

        // Date picker for Start Date
        startDate.setOnClickListener(v -> showDatePickerDialog(startDate));
        // Date picker for End Date
        endDate.setOnClickListener(v -> showDatePickerDialog(endDate));
        // Load categories into spinner
        loadCategories();
        // Save button click listener
        saveButton.setOnClickListener(v -> saveItemDetails());
        // Initialize Spinner and DataContext
        categorySpinner = findViewById(R.id.category_spinner);
        _dbContext = new DataContext(this);
        // Retrieve Category Entities and Extract Names
        List<Category> categoryEntities = _dbContext.Categories.GetAll();
        List<String> categoryNames = new ArrayList<>();
        for (Category category : categoryEntities) {
            categoryNames.add(category.Name);
        }
        // Set Up the Spinner with Category Names
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
        uploadImageButton.setOnClickListener(v -> showImagePickerDialog()); // Added to handle image

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            finish(); // Navigate back to the previous activity
        });
    }

    private void showImagePickerDialog() {
        String[] options = {"Choose from Gallery", "Take a Photo"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Upload Photo");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                openGallery();
            } else if (which == 1) {
                openCamera();
            }
        });
        builder.show();
    }

    private void showDatePickerDialog(EditText dateField) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    dateField.setText(selectedDate);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void loadCategories() {
        DataContext dbContext = new DataContext(this);
        List<Category> categories = dbContext.Categories.GetAll();
        List<String> categoryNames = new ArrayList<>();

        for (Category category : categories) {
            categoryNames.add(category.Name);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        // Get selected category ID when item is selected (Optional)
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategoryId = categories.get(position).Id;  // Assuming 'Id' is the primary key in Category
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCategoryId = -1;
            }
        });
    }

    // Open Gallery
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Open Camera
    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAPTURE_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
                // Handle gallery image
                Uri imageUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    uploadedImageView.setImageBitmap(bitmap); // Display the image
                    // Save the image to the database here
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == CAPTURE_IMAGE_REQUEST && data != null && data.getExtras() != null) {
                // Handle camera image
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                uploadedImageView.setImageBitmap(bitmap); // Display the image
            }
        }
    }


    private void saveItemDetails() {
        String name = itemName.getText().toString().trim();
        String description = itemDescription.getText().toString().trim();
        String feeText = itemFee.getText().toString().trim();
        String start = startDate.getText().toString().trim();
        String end = endDate.getText().toString().trim();

        if (name.isEmpty() || description.isEmpty() || feeText.isEmpty() || start.isEmpty() || end.isEmpty() || selectedCategoryId == -1) {
            Toast.makeText(this, "Please fill in all the details", Toast.LENGTH_SHORT).show();
            return;
        }
        // Convert fee to a double
        double fee;
        try {
            fee = Double.parseDouble(feeText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid fee amount", Toast.LENGTH_SHORT).show();
            return;
        }
        // Convert start and end dates to LocalDate
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yyyy"); // Adjust the format as needed
        LocalDate ableFromDate;
        LocalDate ableToDate;

        try {
            ableFromDate = LocalDate.parse(start, formatter);
            ableToDate = LocalDate.parse(end, formatter);
        } catch (DateTimeParseException e) {
            Toast.makeText(this, "Invalid date format. Please use dd/MM/yyyy.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validation: Check if `to date` is greater than `from date`
        if (!ableToDate.isAfter(ableFromDate)) {
            Toast.makeText(this, "End date must be after the start date.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert LocalDate to LocalDateTime with default time
        LocalDateTime ableFromDateTime = ableFromDate.atStartOfDay();
        LocalDateTime ableToDateTime = ableToDate.atStartOfDay();

        // Create a new Item and set its properties
        Item newItem = new Item();
        newItem.Name = name;
        newItem.Description = description;
        newItem.Fee = fee;
        newItem.CategoryId = selectedCategoryId;
        newItem.AbleFromDate = ableFromDateTime;
        newItem.AbleToDate = ableToDateTime;
        newItem.LessorId = UserInfo.User.Id;
        newItem.ImageBinary = ImageHelper.getImageBase64FromImageView(uploadedImageView);

        // Save the item to the database
        long itemId = _dbContext.Items.Insert(newItem);

        if (itemId != -1) {
            Toast.makeText(this, "Item saved successfully!", Toast.LENGTH_SHORT).show();
            setResult(Activity.RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Failed to save item", Toast.LENGTH_SHORT).show();
        }
    }
}
