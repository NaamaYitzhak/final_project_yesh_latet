package me.naama.yeshlatet;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class VolunteerRequestsActivity extends AppCompatActivity {

    private static final String BASE_URL = "http://10.0.2.2:5000";

    private String currentUsername;

    private LinearLayout requestsContainer;
    private MaterialButton refreshButton;
    private MaterialButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_requests);

        currentUsername = getIntent().getStringExtra("username");

        requestsContainer = findViewById(R.id.requestsContainer);
        refreshButton = findViewById(R.id.refreshButton);
        backButton = findViewById(R.id.backButton);

        refreshButton.setOnClickListener(v -> loadRequests());
        backButton.setOnClickListener(v -> finish());

        loadRequests();
        // loading all requests on first load
    }

    private void loadRequests() {
        requestsContainer.removeAllViews();
        addEmptyText("טוען בקשות...");

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                BASE_URL + "/getFood",
                null,
                response -> {
                    requestsContainer.removeAllViews();

                    try {
                        String status = response.optString("status");

                        if (!status.equals("success")) {
                            addEmptyText("שגיאה בטעינת הבקשות");
                            return;
                        }

                        JSONArray data = response.getJSONArray("data");

                        int count = 0;

                        for (int i = 0; i < data.length(); i++) {
                            JSONObject item = data.getJSONObject(i);

                            String type = item.optString("type", "");
                            // if the food type is a volunteer request.
                            if (type.equals("VOLUNTEER_REQUEST")) {
                                addRequestCard(item);
                                count++;
                            }
                        }

                        if (count == 0) {
                            addEmptyText("אין בקשות מתנדבים כרגע");
                        }

                    } catch (JSONException e) {
                        addEmptyText("שגיאה בקריאת נתוני השרת");
                    }
                },
                error -> {
                    requestsContainer.removeAllViews();
                    addEmptyText("שגיאה בחיבור לשרת");
                }
        );

        Volley.newRequestQueue(this).add(request);
    }

    private void addRequestCard(JSONObject request) {
        String charityUsername = request.optString("username", "לא צוין");
        String content = request.optString("amount", "לא צוין");
        String createdAt = request.optString("created_at", "לא צוין");

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundResource(R.drawable.card_background);
        card.setPadding(dp(24), dp(20), dp(24), dp(20));

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, dp(16));
        card.setLayoutParams(cardParams);

        TextView title = new TextView(this);
        title.setText("🙋 בקשת עזרה מעמותה");
        title.setTextSize(21);
        title.setTextColor(0xFF7A1E2C);
        title.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView details = new TextView(this);
        details.setText(
                "עמותה: " + charityUsername + "\n" +
                        "פירוט: " + content + "\n" +
                        "תאריך: " + createdAt
        );
        details.setTextSize(16);
        details.setTextColor(0xFF5E2A2A);
        details.setPadding(0, dp(12), 0, 0);
        details.setLineSpacing(4, 1);

        MaterialButton chatButton = new MaterialButton(this);
        chatButton.setText("פתח צ׳אט עם העמותה");
        chatButton.setTextSize(15);
        chatButton.setTextColor(0xFFFFFFFF);
        chatButton.setBackgroundTintList(ColorStateList.valueOf(0xFFE96F88));
        chatButton.setCornerRadius(dp(22));

        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(48)
        );
        buttonParams.setMargins(0, dp(18), 0, 0);
        chatButton.setLayoutParams(buttonParams);

        chatButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra("currentUsername", currentUsername);
            intent.putExtra("otherUsername", charityUsername);
            startActivity(intent);
        });

        card.addView(title);
        card.addView(details);
        card.addView(chatButton);

        requestsContainer.addView(card);
    }

    private void addEmptyText(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextSize(18);
        textView.setTextColor(0xFF8A5A5A);
        textView.setGravity(android.view.Gravity.CENTER);
        textView.setPadding(0, dp(40), 0, dp(40));

        requestsContainer.addView(textView);
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }
}