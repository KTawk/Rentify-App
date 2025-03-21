package com.example.rentify;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.stream.Collectors;

import adapters.UserItemAdapter;
import database.context.DataContext;
import helpers.CollectionHelper;
import models.TableColumns;
import models.UserInfo;
import models.UserRowInfoContainer;

public class WelcomeActivity extends AppCompatActivity {
    private DataContext _db;
    ListView usersListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);
        _db = new DataContext(this);

        // Initialize UI components
        TextView tvWelcomeMessage = findViewById(R.id.tvWelcomeMessage);
        TextView tvUserRole = findViewById(R.id.tvUserRole);
        Button btnProceed = findViewById(R.id.btnProceedCategories);
        Button btnLogout = findViewById(R.id.btnLogout);
        usersListView = findViewById(R.id.usersListView);
        Button btnProcessCategories = findViewById(R.id.btnProceedCategories);
        var intent = getIntent();

        var userId = intent.getIntExtra("userId", 0);

        var user = _db.Users.GetById(userId); // replace id with real id passed by other activity
        var userRole = _db.UserRoles.Where(CollectionHelper.WithFilter(TableColumns.UserRole.UserId, Integer.toString(user.Id))).stream().findFirst().orElseThrow();
        var usersRole = _db.Roles.GetById(userRole.RoleId);

        if (usersRole.Name.equals("Admin")) {
            btnProcessCategories.setVisibility(View.VISIBLE);
            LoadUsers();
        } else {
            btnProcessCategories.setVisibility(View.GONE);
            usersListView.setVisibility(View.GONE);

        }


        // Example username and role, replace with actual data as necessary
        tvWelcomeMessage.setText("Welcome, " + user.FirstName + " " + user.LastName + " !");
        tvUserRole.setText("You are logged in as " + usersRole.Name);

        // Set up button listeners
        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this, AddItemActivity.class);
                startActivity(intent);
            }
        });

        ((Button) findViewById(R.id.btnProceedItems_welcome)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this, ViewItemsActivity.class);
                startActivity(intent);
            }
        });

        ((Button) findViewById(R.id.btnProceedRequests_welcome)).setVisibility(View.INVISIBLE);

        if (UserInfo.Role.Name.equals("Lesser")) {
            ((Button) findViewById(R.id.btnProceedRequests_welcome)).setVisibility(View.VISIBLE);

            ((Button) findViewById(R.id.btnProceedRequests_welcome)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(WelcomeActivity.this, ViewRequests.class);
                    startActivity(intent);
                }
            });
        }

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Implement logout functionality
                finish(); // This line closes the activity
            }
        });
        btnProcessCategories.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this, ListOfCategory.class);
                startActivity(intent);
            }
        });
    }

    public void LoadUsers() {
        var allUsers = _db.Users.GetAll();
        var allUserRoles = _db.UserRoles.GetAll();
        var allRoles = _db.Roles.GetAll();

        var adminRoleId = allRoles.stream().filter(z -> z.Name.equals("Admin")).findFirst().orElseThrow().Id;
        var adminUserId = allUserRoles.stream().filter(z -> z.RoleId == adminRoleId).findFirst().orElseThrow().UserId;

        var listData = new ArrayList<UserRowInfoContainer>();

        for (var item : allUsers) {
            // skip admin
            if (item.Id != adminUserId) {
                var usersRoles = allUserRoles.stream().filter(z -> z.UserId == item.Id);
                var usersRolesRoleId = usersRoles.map(z -> z.RoleId).collect(Collectors.toList());
                var currentUserRoles = allRoles.stream().filter(z -> usersRolesRoleId.contains(z.Id));
                var currentUserRolesName = currentUserRoles.map(z -> z.Name).collect(Collectors.toList());

                listData.add(new UserRowInfoContainer(item.Id, item.FirstName + " " + item.LastName + " (" + String.join(", ", currentUserRolesName) + ")", !item.IsDisabled));
            }
        }

        UserItemAdapter adapter = new UserItemAdapter(this, listData);

        usersListView.setAdapter(adapter);
    }
}
