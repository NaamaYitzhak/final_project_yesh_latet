package me.naama.yeshlatet;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // Buttons in the ui
    private Button loginButton;
    private Button signupButton;

    @Override // the main function for every activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SessionManager sessionManager = new SessionManager(this);

        if (sessionManager.isLoggedIn()) { // is the user already logged in?
            String username = sessionManager.getUsername(); // save username
            String type = sessionManager.getType(); // save user type

            Intent intent;
            // which home to send too
            if ("business".equals(type)) {
                intent = new Intent(this, BusinessHomeActivity.class);
            } else if ("charity".equals(type)) {
                intent = new Intent(this, CharityHomeActivity.class);
            } else {
                intent = new Intent(this, VolunteerHomeActivity.class);
            }

            // the gosip (the information needed)
            intent.putExtra("username", username);
            intent.putExtra("type", type);

            // send.
            startActivity(intent);
            finish();
            return;
        }

        loginButton = findViewById(R.id.loginButton);
        signupButton = findViewById(R.id.signupButton);

        // if pressed on login
        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent); // send to login page
        });

        // if pressed on signup
        signupButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignupActivity.class);
            startActivity(intent); // send to signup
        });
    }
}