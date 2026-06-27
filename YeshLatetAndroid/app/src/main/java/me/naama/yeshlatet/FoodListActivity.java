package me.naama.yeshlatet;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FoodListActivity extends AppCompatActivity {

    private static final String BASE_URL = "http://10.0.2.2:5000";

    private String currentUsername;
    private LinearLayout foodContainer;
    private MaterialButton refreshButton;
    private MaterialButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        currentUsername = getIntent().getStringExtra("username");
        foodContainer = findViewById(R.id.foodContainer);
        refreshButton = findViewById(R.id.refreshButton);
        backButton = findViewById(R.id.backButton);

        refreshButton.setOnClickListener(v -> loadFood());
        backButton.setOnClickListener(v -> finish());

        loadFood();
    }

    private void loadFood() { //
        foodContainer.removeAllViews();

        TextView loadingText = new TextView(this);
        loadingText.setText("טוען תרומות...");
        loadingText.setTextSize(18);
        loadingText.setTextColor(0xFF8A5A5A);
        loadingText.setGravity(android.view.Gravity.CENTER);
        foodContainer.addView(loadingText);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, // get request because we get ALL the food.
                BASE_URL + "/getFood",
                null,
                response -> {
                    foodContainer.removeAllViews();

                    try {
                        String status = response.getString("status");

                        if (!status.equals("success")) {
                            Toast.makeText(this, "שגיאה בטעינת התרומות", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        JSONArray data = response.getJSONArray("data");

                        if (data.length() == 0) {
                            addEmptyText();
                            return;
                        }

                        for (int i = 0; i < data.length(); i++) {
                            JSONObject food = data.getJSONObject(i);

                            addFoodCard(food);
                        }

                    } catch (JSONException e) {
                        Toast.makeText(this, "שגיאה בקריאת נתוני השרת", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    foodContainer.removeAllViews();
                    Toast.makeText(this, "שגיאה בחיבור לשרת", Toast.LENGTH_SHORT).show();
                    addEmptyText();
                }
        );

        Volley.newRequestQueue(this).add(request);
    }

    private void addEmptyText() {
        TextView emptyText = new TextView(this);
        emptyText.setText("אין תרומות זמינות כרגע");
        emptyText.setTextSize(18);
        emptyText.setTextColor(0xFF8A5A5A);
        emptyText.setGravity(android.view.Gravity.CENTER);
        emptyText.setPadding(0, 40, 0, 40);

        foodContainer.addView(emptyText);
    }
// adding the food card to the container, the container is within the scroll in the food_list.xml
    private void addFoodCard(JSONObject food) {
        String id = food.optString("id", "");
        String type = food.optString("type", "לא צוין");
        String amount = food.optString("amount", "לא צוין");
        String username = food.optString("username", "לא צוין");
        String createdAt = food.optString("created_at", "לא צוין");
        String businessUsername = username;

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundResource(R.drawable.card_background);
        card.setPadding(28, 24, 28, 24);

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, 18);
        card.setLayoutParams(cardParams);

        TextView title = new TextView(this);
        title.setText("🍽️ " + type);
        title.setTextSize(22);
        title.setTextColor(0xFF7A1E2C);
        title.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView details = new TextView(this);
        details.setText(
                "כמות: " + amount + "\n" +
                        "פורסם על ידי: " + username + "\n" +
                        "תאריך: " + createdAt
        );
        details.setTextSize(16);
        details.setTextColor(0xFF5E2A2A);
        details.setPadding(0, 12, 0, 0);
        details.setLineSpacing(4, 1);

        card.addView(title);
        card.addView(details);
        // if the food wasnt created by me
        if (currentUsername != null && !currentUsername.equals(businessUsername)) {
            MaterialButton chatButton = new MaterialButton(this);
            chatButton.setText("פתח צ׳אט עם העסק");
            chatButton.setTextSize(15);
            chatButton.setTextColor(0xFFFFFFFF);
            chatButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFE96F88));
            chatButton.setCornerRadius(dp(22));

            LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    dp(48)
            );
            buttonParams.setMargins(0, 18, 0, 0);
            chatButton.setLayoutParams(buttonParams);

            chatButton.setOnClickListener(v -> {
                android.content.Intent intent = new android.content.Intent(this, ChatActivity.class);
                intent.putExtra("currentUsername", currentUsername);
                intent.putExtra("otherUsername", businessUsername);
                startActivity(intent);
            });

            card.addView(chatButton);
        }

        if (!id.isEmpty()) {
            TextView idText = new TextView(this);
            idText.setText("מספר תרומה: " + id);
            idText.setTextSize(13);
            idText.setTextColor(0xFF8A5A5A);
            idText.setPadding(0, 10, 0, 0);
            card.addView(idText);
        }

        foodContainer.addView(card);
    }
    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }
}