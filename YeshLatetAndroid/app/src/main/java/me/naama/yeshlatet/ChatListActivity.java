package me.naama.yeshlatet;

import android.content.Intent;
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

public class ChatListActivity extends AppCompatActivity {

    private static final String BASE_URL = "http://10.0.2.2:5000";

    private String currentUsername;

    private LinearLayout chatsContainer;// ui chat list
    private MaterialButton refreshButton;
    private MaterialButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        currentUsername = getIntent().getStringExtra("username");

        chatsContainer = findViewById(R.id.chatsContainer);
        refreshButton = findViewById(R.id.refreshButton);
        backButton = findViewById(R.id.backButton);

        refreshButton.setOnClickListener(v -> loadChats());
        backButton.setOnClickListener(v -> finish());

        loadChats(); // on first load of the page it will always load the chats.
    }

    private void loadChats() {
        chatsContainer.removeAllViews();

        if (currentUsername == null || currentUsername.isEmpty()) {
            addEmptyText("שגיאה: משתמש לא זוהה");
            return;
        }

        addEmptyText("טוען צ׳אטים...");

        JSONObject body = new JSONObject();

        try {
            body.put("username", currentUsername);
        } catch (JSONException e) {
            chatsContainer.removeAllViews();
            addEmptyText("שגיאה ביצירת בקשה");
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                BASE_URL + "/getChat", // asking the server to find all the chats from the db
                body,
                response -> {
                    chatsContainer.removeAllViews();

                    try {
                        String status = response.optString("status");

                        if (!status.equals("success")) {
                            addEmptyText("שגיאה בטעינת הצ׳אטים");
                            return;
                        }

                        JSONArray data = response.getJSONArray("data");

                        if (data.length() == 0) {
                            addEmptyText("אין לך צ׳אטים עדיין");
                            return;
                        }

                        for (int i = 0; i < data.length(); i++) {
                            JSONObject chat = data.getJSONObject(i);
                            addChatCard(chat);
                        }

                    } catch (JSONException e) {
                        addEmptyText("שגיאה בקריאת נתוני השרת");
                    }
                },
                error -> {
                    chatsContainer.removeAllViews();
                    addEmptyText("שגיאה בחיבור לשרת");
                }
        );

        Volley.newRequestQueue(this).add(request);
    }

    private void addChatCard(JSONObject chat) {
        // adding all the chats here because we cant get in the xml
        String chatId = chat.optString("chatId", "");
        String user1 = chat.optString("user1", "");
        String user2 = chat.optString("user2", "");

        String otherUsername;
        // checking with current username to find out who is the other username (not me)
        if (user1.equals(currentUsername)) {
            otherUsername = user2;
        } else {
            otherUsername = user1;
        }

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundResource(R.drawable.card_background);
        card.setPadding(dp(22), dp(18), dp(22), dp(18));

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, dp(14));
        card.setLayoutParams(cardParams);

        TextView title = new TextView(this);
        title.setText("💬 צ׳אט עם " + otherUsername);
        title.setTextSize(20);
        title.setTextColor(0xFF7A1E2C);
        title.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView subtitle = new TextView(this);
        subtitle.setText("לחץ כדי לפתוח את השיחה");
        subtitle.setTextSize(15);
        subtitle.setTextColor(0xFF8A5A5A);
        subtitle.setPadding(0, dp(6), 0, 0);

        card.addView(title);
        card.addView(subtitle);

        card.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra("currentUsername", currentUsername);
            intent.putExtra("otherUsername", otherUsername);
            intent.putExtra("chatId", chatId);
            startActivity(intent);
        });

        chatsContainer.addView(card);
    }

    private void addEmptyText(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextSize(18);
        textView.setTextColor(0xFF8A5A5A);
        textView.setGravity(android.view.Gravity.CENTER);
        textView.setPadding(0, dp(40), 0, dp(40));

        chatsContainer.addView(textView);
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }
}