package me.naama.yeshlatet;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private static final String BASE_URL = "http://10.0.2.2:5000";

    private EditText usernameInput;
    private EditText passwordInput;
    private MaterialButton loginButton;
    private MaterialButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        backButton = findViewById(R.id.backButton);

        loginButton.setOnClickListener(v -> login());
        backButton.setOnClickListener(v -> finish());
    }

    private void login() {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "נא למלא שם משתמש וסיסמה", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject body = new JSONObject();

        try {
            body.put("username", username);
            body.put("password", password);
        } catch (JSONException e) {
            Toast.makeText(this, "שגיאה ביצירת בקשה", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                BASE_URL + "/login",
                body,
                response -> {
                    try {
                        String status = response.getString("status");

                        if (status.equals("success")) {
                            JSONObject user = response.getJSONObject("user");
                            String type = user.getString("type");

                            SessionManager sessionManager = new SessionManager(this);
                            sessionManager.saveLogin(username, type);

                            Toast.makeText(this, "התחברת בהצלחה", Toast.LENGTH_SHORT).show();

                            Intent intent;

                            if (type.equals("business")) {
                                intent = new Intent(this, BusinessHomeActivity.class);
                            } else if (type.equals("charity")) {
                                intent = new Intent(this, CharityHomeActivity.class);
                            } else {
                                intent = new Intent(this, VolunteerHomeActivity.class);
                            }

                            intent.putExtra("username", username);
                            intent.putExtra("type", type);

                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this, "פרטי התחברות לא נכונים", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        Toast.makeText(this, "שגיאה בקריאת תשובת השרת", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "שגיאה בחיבור לשרת", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(request);
    }
}