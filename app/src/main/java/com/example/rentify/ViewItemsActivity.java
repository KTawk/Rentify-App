package com.example.rentify;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import adapters.ItemItemAdapter;
import database.context.DataContext;
import database.entities.Category;
import models.ItemRowInfoContainer;
import models.UserInfo;

import android.content.Intent;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

public class ViewItemsActivity extends AppCompatActivity {
    private DataContext _db;
    ListView itemsListView;
    private ActivityResultLauncher<Intent> addItemLauncher;
    private EditText searchBar;
    private List<ItemRowInfoContainer> fullItemList; // Original unfiltered list of items
    private ItemItemAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_items);

        addItemLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        LoadItems();
                    }
                }
        );

        // Set window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        this._db = new DataContext(this);
        itemsListView = findViewById(R.id.itemsListView);
        searchBar = findViewById(R.id.search_bar);

        // Add Button functionality to navigate to AddItemActivity
        ImageButton addButton = findViewById(R.id.add_button_view_items);

        if (UserInfo.Role.Name.equals("Renter"))
            addButton.setVisibility(View.INVISIBLE);

        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(ViewItemsActivity.this, AddItemActivity.class);
            addItemLauncher.launch(intent);
            Toast.makeText(this, "Navigating to Add Item", Toast.LENGTH_SHORT).show();
        });
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            finish(); // Navigate back to the previous activity
        });

        // Load items into the ListView
        LoadItems();
        // Add TextWatcher for search bar
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No need
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterItems(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No need
            }
        });
    }


    public void LoadItems() {
        var allItems = _db.Items.GetAll();
        List<Category> categories = _db.Categories.GetAll();


        var listData = new ArrayList<ItemRowInfoContainer>();

        for (var item : allItems) {
            String categoryName = categories.stream()
                    .filter(category -> category.Id == item.CategoryId)
                    .map(category -> category.Name)
                    .findFirst()
                    .orElse("Uncategorized");
            listData.add(new ItemRowInfoContainer(
                    item.Id,
                    item.LessorId,
                    item.CategoryId,
                    item.Name,
                    item.Description,
                    item.AbleFromDate,
                    item.AbleToDate,
                    item.Fee,
                    item.ImageBinary));
        }

        fullItemList = listData;

        adapter = new ItemItemAdapter(this, listData);
        itemsListView.setAdapter(adapter);
    }

    private void filterItems(String query) {
        List<ItemRowInfoContainer> filteredList;

        if (query == null || query.trim().isEmpty()) {
            // If the search query is empty, show the full list
            filteredList = new ArrayList<>(fullItemList);
        } else {
            String lowerCaseQuery = query.toLowerCase();

            // Filter items by name or category
            filteredList = fullItemList.stream()
                    .filter(item -> item.Name.toLowerCase().contains(lowerCaseQuery) ||
                            _db.Categories.GetById(item.CategoryId).Name.toLowerCase().contains(lowerCaseQuery))
                    .collect(Collectors.toList());
        }

        // Create a new adapter with the filtered list and set it to the ListView
        var filteredAdapter = new ItemItemAdapter(this, filteredList);
        itemsListView.setAdapter(filteredAdapter);
    }


}