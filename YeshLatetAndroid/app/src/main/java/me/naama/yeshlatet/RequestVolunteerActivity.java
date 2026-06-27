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

public class RequestVolunteerActivity extends AppCompatActivity {

    private static final String BASE_URL = "http://10.0.2.2:5000";

    private String username;

    private EditText requestInput;
    private MaterialButton sendButton;
    private MaterialButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_volunteer);

        username = getIntent().getStringExtra("username");

        requestInput = findViewById(R.id.requestInput);
        sendButton = findViewById(R.id.sendButton);
        backButton = findViewById(R.id.backButton);

        sendButton.setOnClickListener(v -> sendVolunteerRequest());
        backButton.setOnClickListener(v -> finish());
    }

    private void sendVolunteerRequest() {
        // TODO - fix it :)
        String content = requestInput.getText().toString().trim();

        if (content.isEmpty()) {
            Toast.makeText(this, "נא לכתוב מה צריך מהמתנדב", Toast.LENGTH_SHORT).show();
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
            body.put("type", "VOLUNTEER_REQUEST");
            body.put("amount", content);
            body.put("created_at", createdAt);
        } catch (JSONException e) {
            Toast.makeText(this, "שגיאה ביצירת בקשה", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                BASE_URL + "/submitFood", // in the same route with adding food.
                body,
                response -> {
                    Toast.makeText(this, "בקשת המתנדב פורסמה בהצלחה", Toast.LENGTH_SHORT).show();
                    finish();
                },
                error -> Toast.makeText(this, "שגיאה בשליחת הבקשה לשרת", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(request);
    }
}