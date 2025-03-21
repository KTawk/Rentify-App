package com.example.rentify;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import database.context.DataContext;
import helpers.CollectionHelper;
import helpers.PasswordHelper;
import helpers.models.Pair;
import models.TableColumns;
import models.UserInfo;

public class LoginActivity extends AppCompatActivity {
    private DataContext _dbContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        this._dbContext = new DataContext(this);

        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button loginButton = findViewById(R.id.loginButton_Login);

        Button backButton = findViewById(R.id.backlBtn_login);

        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                startActivity(intent);
            }
        });


        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                var userName = ((EditText) findViewById(R.id.usernameInput_Login)).getText().toString();
                var password = ((EditText) findViewById(R.id.passwordInput_Login)).getText().toString();

                var user = _dbContext.Users
                        .Where(CollectionHelper.WithFilters(new Pair(TableColumns.User.UserName, userName), new Pair(TableColumns.User.PasswordHash, PasswordHelper.hashPassword(password))))
                        .stream()
                        .findFirst()
                        .orElse(null);

                if (user == null) {
                    Toast.makeText(getApplicationContext(), "Username or Password is wrong", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(LoginActivity.this, WelcomeActivity.class);
                intent.putExtra("userId", user.Id);

                var userRole = _dbContext.UserRoles.Where(CollectionHelper.WithFilter(TableColumns.UserRole.UserId, String.valueOf(user.Id))).stream().findFirst().orElseThrow();
                var role = _dbContext.Roles.GetById(userRole.RoleId);


                UserInfo.User = user;
                UserInfo.Role = role;


                startActivity(intent);
            }
        });

    }
}