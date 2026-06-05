package me.naama.yeshlatet;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

public class SignupActivity extends AppCompatActivity {

    private static final String BASE_URL = "http://10.0.2.2:5000";

    private EditText usernameInput;
    private EditText emailInput;
    private EditText passwordInput;
    private RadioGroup typeRadioGroup;

    private MaterialButton signupButton;
    private MaterialButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        usernameInput = findViewById(R.id.usernameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        typeRadioGroup = findViewById(R.id.typeRadioGroup);

        signupButton = findViewById(R.id.signupButton);
        backButton = findViewById(R.id.backButton);

        signupButton.setOnClickListener(v -> signup());
        backButton.setOnClickListener(v -> finish());
    }

    private void signup() {
        String username = usernameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        int checkedId = typeRadioGroup.getCheckedRadioButtonId();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || checkedId == -1) {
            Toast.makeText(this, "נא למלא את כל השדות", Toast.LENGTH_SHORT).show();
            return;
        }

        String type;

        if (checkedId == R.id.businessRadio) {
            type = "business";
        } else if (checkedId == R.id.charityRadio) {
            type = "charity";
        } else {
            type = "volunteer";
        }

        JSONObject body = new JSONObject();

        try {
            body.put("username", username);
            body.put("email", email);
            body.put("password", password);
            body.put("type", type);
        } catch (JSONException e) {
            Toast.makeText(this, "שגיאה ביצירת בקשה", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                BASE_URL + "/signup",
                body,
                response -> {
                    try {
                        String status = response.getString("status");

                        if (status.equals("success")) {
                            Toast.makeText(this, "החשבון נוצר בהצלחה", Toast.LENGTH_SHORT).show();
                            SessionManager sessionManager = new SessionManager(this);
                            sessionManager.saveLogin(username, type);
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
                            Toast.makeText(this, "ההרשמה נכשלה", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        Toast.makeText(this, "שגיאה בקריאת תשובת השרת", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    String message = "שגיאה בחיבור לשרת";

                    if (error.networkResponse != null && error.networkResponse.statusCode == 409) {
                        message = "שם המשתמש או האימייל כבר קיימים";
                    }

                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                }
        );

        Volley.newRequestQueue(this).add(request);
    }
}