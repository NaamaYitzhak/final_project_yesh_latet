package me.naama.yeshlatet;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button loginButton;
    private Button signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SessionManager sessionManager = new SessionManager(this);

        if (sessionManager.isLoggedIn()) {
            String username = sessionManager.getUsername();
            String type = sessionManager.getType();

            Intent intent;

            if ("business".equals(type)) {
                intent = new Intent(this, BusinessHomeActivity.class);
            } else if ("charity".equals(type)) {
                intent = new Intent(this, CharityHomeActivity.class);
            } else {
                intent = new Intent(this, VolunteerHomeActivity.class);
            }

            intent.putExtra("username", username);
            intent.putExtra("type", type);

            startActivity(intent);
            finish();
            return;
        }

        loginButton = findViewById(R.id.loginButton);
        signupButton = findViewById(R.id.signupButton);

        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });

        signupButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignupActivity.class);
            startActivity(intent);
        });
    }
}