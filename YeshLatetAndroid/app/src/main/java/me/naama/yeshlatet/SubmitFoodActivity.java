package me.naama.yeshlatet;

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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SubmitFoodActivity extends AppCompatActivity {

    private static final String BASE_URL = "http://10.0.2.2:5000";

    private String username;

    private EditText foodTypeInput;
    private EditText amountInput;
    private EditText locationInput;
    private EditText areaInput;

    private MaterialButton submitButton;
    private MaterialButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_food);

        username = getIntent().getStringExtra("username");

        foodTypeInput = findViewById(R.id.foodTypeInput);
        amountInput = findViewById(R.id.amountInput);
        locationInput = findViewById(R.id.locationInput);
        areaInput = findViewById(R.id.areaInput);

        submitButton = findViewById(R.id.submitButton);
        backButton = findViewById(R.id.backButton);

        submitButton.setOnClickListener(v -> submitFood());
        backButton.setOnClickListener(v -> finish());
    }

    private void submitFood() {
        String foodType = foodTypeInput.getText().toString().trim();
        String amount = amountInput.getText().toString().trim();
        String location = locationInput.getText().toString().trim();
        String area = areaInput.getText().toString().trim();

        if (foodType.isEmpty() || amount.isEmpty()) {
            Toast.makeText(this, "נא למלא סוג אוכל וכמות", Toast.LENGTH_SHORT).show();
            return;
        }

        if (username == null || username.isEmpty()) {
            Toast.makeText(this, "שגיאה: המשתמש לא זוהה", Toast.LENGTH_SHORT).show();
            return;
        }

        String createdAt = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss",
                Locale.getDefault()
        ).format(new Date());

        JSONObject body = new JSONObject();

        try {
            body.put("username", username);
            body.put("type", foodType);
            body.put("amount", amount);
            body.put("created_at", createdAt);

            // These are for the app side right now.
            // Backend can ignore them unless you added columns for them.
            body.put("location", location);
            body.put("area", area);

        } catch (JSONException e) {
            Toast.makeText(this, "שגיאה ביצירת בקשה", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                BASE_URL + "/submitFood",
                body,
                response -> {
                    Toast.makeText(this, "התרומה פורסמה בהצלחה", Toast.LENGTH_SHORT).show();
                    finish();
                },
                error -> Toast.makeText(this, "שגיאה בשליחת התרומה לשרת", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(request);
    }
}