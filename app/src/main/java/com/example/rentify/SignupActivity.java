package com.example.rentify;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

import database.context.DataContext;
import database.entities.User;
import database.entities.UserRole;
import helpers.CollectionHelper;
import helpers.PasswordHelper;
import helpers.ValidationHelper;
import models.TableColumns;
import models.UserInfo;

public class SignupActivity extends AppCompatActivity {

    private DataContext _dbContext;

    // Declare UI elements
    private EditText firstNameInput;
    private EditText lastNameInput;
    private EditText usernameInput;
    private EditText passwordInput;
    private RadioGroup roleRadioGroup;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        this._dbContext = new DataContext(this);

        // Initialize elements
        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        roleRadioGroup = findViewById(R.id.roleRadioGroup);
        Button createAccountButton = findViewById(R.id.createAccountButton);

        Button backButton = findViewById(R.id.backBtn_signup);

        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, MainActivity.class);

                startActivity(intent);
            }
        });


        createAccountButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String firstName = firstNameInput.getText().toString();
                String lastName = lastNameInput.getText().toString();
                String username = usernameInput.getText().toString();
                String password = passwordInput.getText().toString();

                if (!inputsAreValid(password, firstName, lastName, username))
                    return;


                int selectedRoleId = roleRadioGroup.getCheckedRadioButtonId();
                if (selectedRoleId == -1) {
                    Toast.makeText(getApplicationContext(), "Please select your role", Toast.LENGTH_SHORT).show();
                    return;
                }

                var selectedRole = ((RadioButton) findViewById(selectedRoleId)).getText().toString();        //either renter or lesser is stored in selectedRole depending on what user chose

                var role = _dbContext.Roles.Where(CollectionHelper.WithFilter(TableColumns.Role.Name, selectedRole))
                        .stream()
                        .findFirst()
                        .orElseThrow();

                if (userNameIsDuplicate(username)) {
                    Toast.makeText(getApplicationContext(), "Username exists! please pick another one.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (role.Name.equals("Admin")) {
                    var adminUserRoles = _dbContext.UserRoles.Where(CollectionHelper.WithFilter(TableColumns.UserRole.RoleId, Integer.toString(role.Id)));
                    if (adminUserRoles != null && !adminUserRoles.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Admin has been created previously !", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                var user = new User(firstName, lastName, username, PasswordHelper.hashPassword(password));
                var userId = _dbContext.Users.Insert(user);

                _dbContext.UserRoles.Insert(new UserRole((int) userId, role.Id));

                Intent intent = new Intent(SignupActivity.this, WelcomeActivity.class);
                intent.putExtra("userId", (int) userId);

                user.Id = (int) userId;

                UserInfo.User = user;
                UserInfo.Role = role;

                startActivity(intent);
            }

        });
    }

    private boolean userNameIsDuplicate(String userName) {
        return !_dbContext.Users.Where(CollectionHelper.WithFilter("UserName", userName)).isEmpty();
    }

    private boolean inputsAreValid(String password, String firstName, String lastName, String username) {
        if (!ValidationHelper.isValidPassword(password)) {
            Toast.makeText(getApplicationContext(), "Password is either empty or too short !", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!ValidationHelper.isValidName(firstName) || !ValidationHelper.isValidName(lastName)) {
            Toast.makeText(getApplicationContext(), "First/Last name can just be alphanumeric !", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!ValidationHelper.isValidUsername(username)) {
            Toast.makeText(getApplicationContext(), "Username can just be alphanumeric !", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
