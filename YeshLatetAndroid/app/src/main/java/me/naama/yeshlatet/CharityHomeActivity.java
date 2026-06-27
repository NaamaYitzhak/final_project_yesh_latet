package me.naama.yeshlatet;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class CharityHomeActivity extends AppCompatActivity {

    private String username;

    private TextView titleText;
    private MaterialButton foodListButton;
    private MaterialButton chatButton;
    private MaterialButton volunteerButton;

    private MaterialButton logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charity_home);

        username = getIntent().getStringExtra("username");

        titleText = findViewById(R.id.titleText);
        foodListButton = findViewById(R.id.foodListButton);
        volunteerButton = findViewById(R.id.volunteerButton);
        chatButton = findViewById(R.id.chatButton);
        logoutButton = findViewById(R.id.logoutButton);

        if (username != null && !username.isEmpty()) {
            titleText.setText("שלום " + username);
        }

        foodListButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, FoodListActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });

        volunteerButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, RequestVolunteerActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });

        chatButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChatListActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });
        logoutButton.setOnClickListener(v -> {
            SessionManager sessionManager = new SessionManager(this);
            sessionManager.logout(); // logout

            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}