from flask import Flask, request, jsonify
import pymysql
import hashlib

app = Flask(__name__)

DB_CONFIG = {
    "host": "orcale2.kokodevs.me",
    "port": 3306,
    "user": "u25_WA9TySFnpp",
    "password": "d+v2he7ooRpn4O1Nu=qgx!lh",
    "database": "s25_yeshlatet",
    "cursorclass": pymysql.cursors.DictCursor
}

def get_db():
    return pymysql.connect(
        **DB_CONFIG,
        autocommit=False,
        charset="utf8mb4"
    )


def hash_password(password: str):
    return hashlib.sha256(password.encode()).hexdigest()

def is_valid_email(email: str) -> bool:
    if email.count("@") != 1:
        return False

    local_part, domain_part = email.split("@")

    if not local_part or not domain_part:
        return False

    if "." not in domain_part:
        return False

    return True


# ---------- GETTERS ----------

@app.route("/getFood", methods=["GET"])
def getFood():
    conn = None
    cur = None
    try:
        conn = get_db()
        cur = conn.cursor()
        cur.execute("SELECT * FROM food")
        food_items = cur.fetchall()
        return jsonify({"status": "success", "data": food_items})
    except pymysql.err.Error:
        return jsonify({"error": "Database error"}), 500
    finally:
        if cur:
            cur.close()
        if conn:
            conn.close()


@app.route("/getChat", methods=["POST"])
def getChat():
    data = request.get_json()
    username = data.get("username")

    conn = None
    cur = None
    try:
        conn = get_db()
        cur = conn.cursor()
        cur.execute(
            "SELECT * FROM chat WHERE user1 = %s OR user2 = %s",
            (username, username)
        )
        chat_items = cur.fetchall()
        return jsonify({"status": "success", "data": chat_items})
    except pymysql.err.Error:
        return jsonify({"error": "Database error"}), 500
    finally:
        if cur:
            cur.close()
        if conn:
            conn.close()


@app.route("/getMessage", methods=["POST"])
def getMessage():
    data = request.get_json()
    chat_id = data.get("chatId")

    conn = None
    cur = None
    try:
        conn = get_db()
        cur = conn.cursor()
        cur.execute(
            "SELECT * FROM message WHERE chatId = %s",
            (chat_id,)
        )
        message_items = cur.fetchall()
        return jsonify({"status": "success", "data": message_items})
    except pymysql.err.Error:
        return jsonify({"error": "Database error"}), 500
    finally:
        if cur:
            cur.close()
        if conn:
            conn.close()


# ---------- CHAT ----------

@app.route("/chat", methods=["POST"])
def createChat():
    data = request.get_json()
    user1 = data.get("user1")
    user2 = data.get("user2")
    initial_message = data.get("message")

    if not user1 or not user2 or not initial_message:
        return jsonify({"status": "error", "message": "Missing fields"}), 400

    conn = None
    cur = None
    try:
        conn = get_db()
        cur = conn.cursor()

        cur.execute(
            "SELECT chatId FROM chat WHERE (user1 = %s AND user2 = %s) OR (user1 = %s AND user2 = %s)",
            (user1, user2, user2, user1)
        )
        chat = cur.fetchone()

        if chat:
            chat_id = chat[0]
        else:
            cur.execute(
                "INSERT INTO chat (user1, user2) VALUES (%s, %s)",
                (user1, user2)
            )
            chat_id = cur.lastrowid

        cur.execute(
            "INSERT INTO message (chatId, username, content) VALUES (%s, %s, %s)",
            (chat_id, user1, initial_message)
        )

        conn.commit()
        return jsonify({"status": "Chat created", "chat_id": chat_id})

    except Exception as e:
        return jsonify({"error": str(e)}), 500
    finally:
        if cur:
            cur.close()
        if conn:
            conn.close()


# ---------- FOOD ----------

@app.route("/submitFood", methods=["POST"])
def submitFood():
    data = request.get_json()
    amount = data.get("amount")
    food_type = data.get("type")
    time = data.get("created_at")
    username = data.get("username")

    if not amount or not food_type or not time or not username:
        return jsonify({"status": "error", "message": "Missing fields"}), 400

    conn = None
    cur = None
    try:
        conn = get_db()
        cur = conn.cursor()

        cur.execute(
            "INSERT INTO food (amount, type, created_at, username) VALUES (%s, %s, %s, %s)",
            (amount, food_type, time, username)
        )

        conn.commit()
        return jsonify({"status": "Food submitted"})
    except Exception as e:
        return jsonify({"error": str(e)}), 500
    finally:
        if cur:
            cur.close()
        if conn:
            conn.close()


@app.route("/deleteFood", methods=["POST"])
def deleteFood():
    data = request.get_json()
    food_id = data.get("food_id")

    if not food_id:
        return jsonify({"status": "error", "message": "Missing food_id"}), 400

    conn = None
    cur = None
    try:
        conn = get_db()
        cur = conn.cursor()
        cur.execute("DELETE FROM food WHERE id = %s", (food_id,))
        conn.commit()
        return jsonify({"status": "Food deleted"})
    except Exception as e:
        return jsonify({"error": str(e)}), 500
    finally:
        if cur:
            cur.close()
        if conn:
            conn.close()


# ---------- AUTH ----------

@app.route("/signup", methods=["POST"])
def signup():
    data = request.get_json()
    username = data.get("username")
    password = data.get("password")
    email = data.get("email")
    userType = data.get("type")

    if not username or not password or not email:
        return jsonify({"status": "error", "message": "Missing fields"}), 400

    if not is_valid_email(email):
        return jsonify({"status": "error", "message": "Not a valid email"}), 400

    conn = None
    cur = None
    try:
        conn = get_db()
        cur = conn.cursor()

        cur.execute(
            "INSERT INTO users (username, password, email, type) VALUES (%s, %s, %s, %s)",
            (username, hash_password(password), email, userType)
        )

        conn.commit()
        return jsonify({"status": "Account created"})
    except pymysql.err.IntegrityError:
        return jsonify({"error": "Username or email exists"}), 409
    finally:
        if cur:
            cur.close()
        if conn:
            conn.close()


@app.route("/login", methods=["POST"])
def login():
    data = request.get_json()
    username = data.get("username")
    password = data.get("password")

    if not username or not password:
        return jsonify({"status": "error", "message": "Missing fields"}), 400

    conn = None
    cur = None
    try:
        conn = get_db()
        cur = conn.cursor()

        cur.execute(
            "SELECT password FROM users WHERE username = %s AND password = %s",
            (username, hash_password(password))
        )
        row = cur.fetchone()

        if not row:
            return jsonify({"status": "error", "message": "Invalid username or password"}), 401

        return jsonify({"status": "success", "message": "Login successful"})
    except pymysql.err.Error:
        return jsonify({"error": "Database error"}), 500
    finally:
        if cur:
            cur.close()
        if conn:
            conn.close()


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)
