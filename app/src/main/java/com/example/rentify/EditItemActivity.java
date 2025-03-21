package com.example.rentify;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import adapters.ItemItemAdapter;
import database.context.DataContext;
import database.entities.Category;
import database.entities.Item;
import helpers.ImageHelper;
import models.ItemRowInfoContainer;

public class EditItemActivity extends AppCompatActivity {

    private EditText itemName, itemDescription, itemFee, startDate, endDate;
    private DataContext db;
    private Spinner categorySpinner;
    private ImageView itemImageView;
    private Button changeImageButton, saveChangesButton, backButton;
    private Uri imageUri;
    private static final int PICK_IMAGE_REQUEST = 1;
    private int selectedCategoryId = -1;
    private String itemId;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    ListView itemsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        itemName = findViewById(R.id.item_name);
        itemDescription = findViewById(R.id.item_description);
        itemFee = findViewById(R.id.item_fee);
        startDate = findViewById(R.id.start_date);
        endDate = findViewById(R.id.end_date);
        categorySpinner = findViewById(R.id.category_spinner);
        itemImageView = findViewById(R.id.item_image);
        changeImageButton = findViewById(R.id.change_image_button);
        saveChangesButton = findViewById(R.id.save_changes_button);
        db = new DataContext(this);

        loadCategories();

        // Assume itemId is passed via Intent when editing
        itemId = getIntent().getStringExtra("ITEM_ID");

        // Load the existing item and category data
        loadItemDetails();


        // Date picker for Start Date
        startDate.setOnClickListener(v -> showDatePickerDialog(startDate));

        // Date picker for End Date
        endDate.setOnClickListener(v -> showDatePickerDialog(endDate));

        // Change Image button click listener
        changeImageButton.setOnClickListener(v -> showImagePickerDialog());

        // Save Changes button click listener
        saveChangesButton.setOnClickListener(v -> saveEditedItemDetails());
    }


    private void loadItemDetails() {
        var item = db.Items.GetById(Integer.parseInt(itemId));

        // Set fetched details into the fields
        itemName.setText(item.Name);
        itemDescription.setText(item.Description);
        itemFee.setText(Double.toString(item.Fee));
        startDate.setText(item.AbleFromDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        endDate.setText(item.AbleToDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));


        // Decode and display the image
        if (item.ImageBinary != null && !item.ImageBinary.isEmpty()) {
            byte[] imageBytes = Base64.decode(item.ImageBinary, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            itemImageView.setImageBitmap(bitmap);
        } else {
            Toast.makeText(this, "No image available", Toast.LENGTH_SHORT).show();
        }
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


    private void showDatePickerDialog(EditText dateField) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String formattedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear);
                    dateField.setText(formattedDate);
                }, year, month, day);
        datePickerDialog.show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
                // Handle gallery image
                imageUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    itemImageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getExtras() != null) {
                // Handle camera image
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                itemImageView.setImageBitmap(bitmap);
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void saveEditedItemDetails() {
        String name = itemName.getText().toString().trim();
        String description = itemDescription.getText().toString().trim();
        String feeText = itemFee.getText().toString().trim();
        String start = startDate.getText().toString().trim();
        String end = endDate.getText().toString().trim();
        int catID = selectedCategoryId;


        if (name.isEmpty() || description.isEmpty() || feeText.isEmpty() || start.isEmpty() || end.isEmpty()) {
            Toast.makeText(this, "Please fill in all the details", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Parse the fee text to a double
            double fee;
            try {
                fee = Double.parseDouble(feeText);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid fee format", Toast.LENGTH_LONG).show();
                return;
            }

            // Parse the dates to LocalDateTime
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate ableFromDate;
            LocalDate ableToDate;

            try {
                ableFromDate = LocalDate.parse(start, formatter);
            } catch (DateTimeParseException e) {
                Toast.makeText(this, "Invalid start date format. Please use dd/MM/yyyy", Toast.LENGTH_LONG).show();
                return;
            }

            try {
                ableToDate = LocalDate.parse(end, formatter);
            } catch (DateTimeParseException e) {
                Toast.makeText(this, "Invalid end date format. Please use dd/MM/yyyy", Toast.LENGTH_LONG).show();
                return;
            }

            // Validation: Check if `to date` is greater than `from date`
            if (!ableToDate.isAfter(ableFromDate)) {
                Toast.makeText(this, "End date must be after the start date.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Retrieve the item by ID and check if it exists
            Item item;
            try {
                item = db.Items.GetById(Integer.parseInt(itemId));
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid item ID format", Toast.LENGTH_LONG).show();
                return;
            }

            if (item != null) {
                // Set the item fields and attempt to update the database
                item.Name = name;
                item.Description = description;
                item.Fee = fee;
                item.AbleFromDate = ableFromDate.atStartOfDay(); // Convert LocalDate to LocalDateTime
                item.AbleToDate = ableToDate.atStartOfDay();
                item.CategoryId = catID;

                BitmapDrawable drawable = (BitmapDrawable) itemImageView.getDrawable();
                if (drawable != null) {
                    Bitmap bitmap = drawable.getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] imageBytes = stream.toByteArray();
                    String base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                    item.ImageBinary = base64Image;
                }

                try {
                    db.Items.Update(item);
                    Toast.makeText(this, "Changes saved successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EditItemActivity.this, ViewItemsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    Toast.makeText(this, "Failed to update item in the database", Toast.LENGTH_LONG).show();
                    e.printStackTrace(); // Log the error for debugging
                }
            } else {
                Toast.makeText(this, "Error: Item not found" + itemId, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "An unexpected error occurred", Toast.LENGTH_LONG).show();
            e.printStackTrace(); // Log the error for debugging
        }
    }


}