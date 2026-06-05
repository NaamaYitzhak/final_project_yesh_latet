package me.naama.yeshlatet;

import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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

public class ChatActivity extends AppCompatActivity {

    private static final String BASE_URL = "http://10.0.2.2:5000";

    private String currentUsername;
    private String otherUsername;
    private String chatId;

    private TextView titleText;
    private LinearLayout messagesContainer;
    private ScrollView messagesScroll;
    private EditText messageInput;
    private MaterialButton sendButton;
    private MaterialButton refreshButton;
    private MaterialButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        currentUsername = getIntent().getStringExtra("currentUsername");
        otherUsername = getIntent().getStringExtra("otherUsername");
        chatId = getIntent().getStringExtra("chatId");

        titleText = findViewById(R.id.titleText);
        messagesContainer = findViewById(R.id.messagesContainer);
        messagesScroll = findViewById(R.id.messagesScroll);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        refreshButton = findViewById(R.id.refreshButton);
        backButton = findViewById(R.id.backButton);

        if (otherUsername != null) {
            titleText.setText("צ׳אט עם " + otherUsername);
        }

        sendButton.setOnClickListener(v -> sendMessage());
        refreshButton.setOnClickListener(v -> findChatAndLoadMessages());
        backButton.setOnClickListener(v -> finish());

        findChatAndLoadMessages();
    }

    private void findChatAndLoadMessages() {
        if (currentUsername == null || currentUsername.isEmpty()) {
            Toast.makeText(this, "שגיאה: משתמש לא זוהה", Toast.LENGTH_SHORT).show();
            return;
        }

        messagesContainer.removeAllViews();
        addCenterText("טוען הודעות...");

        JSONObject body = new JSONObject();

        try {
            body.put("username", currentUsername);
        } catch (JSONException e) {
            Toast.makeText(this, "שגיאה ביצירת בקשה", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                BASE_URL + "/getChat",
                body,
                response -> {
                    try {
                        JSONArray data = response.getJSONArray("data");
                        chatId = null;

                        for (int i = 0; i < data.length(); i++) {
                            JSONObject chat = data.getJSONObject(i);

                            String user1 = chat.optString("user1");
                            String user2 = chat.optString("user2");

                            boolean isThisChat =
                                    (user1.equals(currentUsername) && user2.equals(otherUsername)) ||
                                            (user1.equals(otherUsername) && user2.equals(currentUsername));

                            if (isThisChat) {
                                chatId = chat.optString("chatId");
                                break;
                            }
                        }

                        if (chatId == null || chatId.isEmpty()) {
                            messagesContainer.removeAllViews();
                            addCenterText("אין עדיין הודעות. שלח הודעה ראשונה :)");
                        } else {
                            loadMessages();
                        }

                    } catch (JSONException e) {
                        messagesContainer.removeAllViews();
                        addCenterText("שגיאה בקריאת צ׳אטים");
                    }
                },
                error -> {
                    messagesContainer.removeAllViews();
                    addCenterText("שגיאה בחיבור לשרת");
                }
        );

        Volley.newRequestQueue(this).add(request);
    }

    private void loadMessages() {
        if (chatId == null || chatId.isEmpty()) {
            return;
        }

        JSONObject body = new JSONObject();

        try {
            body.put("chatId", chatId);
        } catch (JSONException e) {
            Toast.makeText(this, "שגיאה ביצירת בקשה", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                BASE_URL + "/getMessage",
                body,
                response -> {
                    messagesContainer.removeAllViews();

                    try {
                        JSONArray data = response.getJSONArray("data");

                        if (data.length() == 0) {
                            addCenterText("אין הודעות עדיין");
                            return;
                        }

                        for (int i = 0; i < data.length(); i++) {
                            JSONObject msg = data.getJSONObject(i);
                            addMessageBubble(msg);
                        }

                        scrollToBottom();

                    } catch (JSONException e) {
                        addCenterText("שגיאה בקריאת הודעות");
                    }
                },
                error -> {
                    messagesContainer.removeAllViews();
                    addCenterText("שגיאה בטעינת הודעות");
                }
        );

        Volley.newRequestQueue(this).add(request);
    }

    private void sendMessage() {
        String content = messageInput.getText().toString().trim();

        if (content.isEmpty()) {
            Toast.makeText(this, "אי אפשר לשלוח הודעה ריקה", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentUsername == null || otherUsername == null) {
            Toast.makeText(this, "שגיאה: חסרים פרטי משתמשים", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject body = new JSONObject();

        try {
            body.put("user1", currentUsername);
            body.put("user2", otherUsername);
            body.put("message", content);
        } catch (JSONException e) {
            Toast.makeText(this, "שגיאה ביצירת הודעה", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                BASE_URL + "/chat",
                body,
                response -> {
                    messageInput.setText("");
                    findChatAndLoadMessages();
                },
                error -> Toast.makeText(this, "שגיאה בשליחת הודעה", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(request);
    }

    private void addMessageBubble(JSONObject msg) {
        String sender = msg.optString("username", "");
        String content = msg.optString("content", "");

        boolean mine = sender.equals(currentUsername);

        TextView bubble = new TextView(this);
        bubble.setText(content);
        bubble.setTextSize(16);
        bubble.setTextColor(mine ? 0xFFFFFFFF : 0xFF5E2A2A);
        bubble.setPadding(24, 16, 24, 16);
        bubble.setMaxWidth(dp(260));
        bubble.setBackgroundResource(mine ? R.drawable.message_mine_background : R.drawable.message_other_background);

        LinearLayout.LayoutParams bubbleParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        bubbleParams.setMargins(0, 8, 0, 8);
        bubbleParams.gravity = mine ? Gravity.START : Gravity.END;

        bubble.setLayoutParams(bubbleParams);
        messagesContainer.addView(bubble);
    }

    private void addCenterText(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextColor(0xFF8A5A5A);
        textView.setTextSize(17);
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(0, 40, 0, 40);

        messagesContainer.addView(textView);
    }

    private void scrollToBottom() {
        messagesScroll.post(() -> messagesScroll.fullScroll(ScrollView.FOCUS_DOWN));
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }
}