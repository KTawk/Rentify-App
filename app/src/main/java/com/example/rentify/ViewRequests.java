package com.example.rentify;


import android.os.Build;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.stream.Collectors;

import adapters.RequestedItemAdapter;
import database.context.DataContext;
import helpers.CollectionHelper;
import helpers.models.Pair;
import models.RequestedItemsInfoContainer;
import models.UserInfo;

public class ViewRequests extends AppCompatActivity {

    private DataContext _db;

    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_requests);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        _db = new DataContext(this);

        LoadRequestedItems();
    }

    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private void LoadRequestedItems() {
        var lesserItems = _db.RequestedItems.Where(CollectionHelper
                .WithFilters(new Pair("LesserId", String.valueOf(UserInfo.User.Id))));

        lesserItems = lesserItems.stream().filter(z -> z.HasDecision == false).toList();

        var aaa = _db.RequestedItems.GetAll();
        var lesserItemsId = lesserItems.stream().map(z -> z.ItemId)
                .collect(Collectors.toList());
        var items = _db.Items.GetAll().stream().filter(z -> lesserItemsId.contains(z.Id)).toList();
        var categories = _db.Categories.GetAll();


        var requestList = items
                .stream()
                .map(z -> new RequestedItemsInfoContainer(z.Name, (categories.stream().filter(x -> x.Id == z.CategoryId)
                        .findFirst()
                        .orElseThrow().Name), z.Description, z.Id))
                .toList();

        // Set up the adapter with the data and listener
        RequestedItemAdapter adapter = new RequestedItemAdapter(this, requestList, new RequestedItemAdapter.OnRequestActionListener() {
            @Override
            public void onReject(int position) {
                var item = requestList.get(position);

                var requestedItem = _db.RequestedItems
                        .Where(CollectionHelper.WithFilters(
                                new Pair("LesserId", String.valueOf(UserInfo.User.Id)),
                                new Pair("ItemId", String.valueOf(item.ItemId)))).stream().findFirst().orElseThrow();

                requestedItem.IsApproved = false;
                requestedItem.HasDecision = true;

                _db.RequestedItems.Update(requestedItem);
                Toast.makeText(ViewRequests.this, "Request rejected!", Toast.LENGTH_SHORT).show();

                LoadRequestedItems();
            }

            @Override
            public void onApprove(int position) {
                var item = requestList.get(position);

                var requestedItem = _db.RequestedItems
                        .Where(CollectionHelper.WithFilters(
                                new Pair("LesserId", String.valueOf(UserInfo.User.Id)),
                                new Pair("ItemId", String.valueOf(item.ItemId)))).stream().findFirst().orElseThrow();

                requestedItem.IsApproved = true;
                requestedItem.HasDecision = true;

                _db.RequestedItems.Update(requestedItem);
                Toast.makeText(ViewRequests.this, "Request approved!", Toast.LENGTH_SHORT).show();

                LoadRequestedItems();
            }
        });

        // Link the adapter to the ListView
        ListView listView = findViewById(R.id.requests_list_view);
        listView.setAdapter(adapter);
    }
}