package me.naama.yeshlatet;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class VolunteerHomeActivity extends AppCompatActivity {

    private String username;

    private TextView titleText;
    private MaterialButton foodListButton;
    private MaterialButton tasksButton;
    private MaterialButton chatButton;
    private MaterialButton logoutButton;

    private WebView mapCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_home);

        username = getIntent().getStringExtra("username");

        titleText = findViewById(R.id.titleText);
        foodListButton = findViewById(R.id.foodListButton);
        tasksButton = findViewById(R.id.tasksButton);
        chatButton = findViewById(R.id.chatButton);
        logoutButton = findViewById(R.id.logoutButton);
        mapCard = findViewById(R.id.mapCard);
        LocationMapHelper.setupMap(this, mapCard);

        if (username != null && !username.isEmpty()) {
            titleText.setText("שלום " + username);
        }

        foodListButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, FoodListActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });

        tasksButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, VolunteerRequestsActivity.class);
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
            sessionManager.logout(); // logout and clear the information

            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LocationMapHelper.LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                LocationMapHelper.loadDeviceLocation(this, mapCard);
            }
        }
    }
}