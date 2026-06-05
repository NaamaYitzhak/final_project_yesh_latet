package com.example.yeshlatet;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    EditText usernameInput, passwordInput;
    Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameInput = findViewById(R.id.usernameInput);;
        passwordInput = findViewById(R.id.passwordInput);
        loginBtn = findViewById(R.id.loginBtn);

        loginBtn.setOnClickListener(v -> {
            String username = usernameInput.getText().toString();
            String password = passwordInput.getText().toString();

            new Thread(() -> {
                try {
                    URL url = new URL("http://10.0.2.2:5000/login");
                    HttpURLConnection conn =
                            (HttpURLConnection) url.openConnection();

                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);

                    JSONObject json = new JSONObject();
                    json.put("username", username);
                    json.put("password", password);

                    OutputStreamWriter writer =
                            new OutputStreamWriter(conn.getOutputStream());
                    writer.write(json.toString());
                    writer.flush();
                    writer.close();

                    int responseCode = conn.getResponseCode();

                    runOnUiThread(() -> {
                        if (responseCode == 200) {
                            Toast.makeText(this,
                                    "Login success",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this,
                                    "Login failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (Exception e) {
                    runOnUiThread(() ->
                            Toast.makeText(this,
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show()
                    );
                }
            }).start();
        });
    }
}