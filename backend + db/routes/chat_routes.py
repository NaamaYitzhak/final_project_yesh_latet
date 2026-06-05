from flask import Blueprint, request, jsonify
import pymysql

from database.db import get_db

chat_bp = Blueprint("chat", __name__)


@chat_bp.route("/getChat", methods=["POST"])
def get_chat():
    data = request.get_json()

    username = data.get("username")

    if not username:
        return jsonify({"status": "error", "message": "Missing username"}), 400

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
        return jsonify({"status": "error", "message": "Database error"}), 500

    finally:
        if cur:
            cur.close()
        if conn:
            conn.close()


@chat_bp.route("/getMessage", methods=["POST"])
def get_message():
    data = request.get_json()

    chat_id = data.get("chatId")

    if not chat_id:
        return jsonify({"status": "error", "message": "Missing chatId"}), 400

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
        return jsonify({"status": "error", "message": "Database error"}), 500

    finally:
        if cur:
            cur.close()
        if conn:
            conn.close()


@chat_bp.route("/chat", methods=["POST"])
def create_chat():
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
            """
            SELECT chatId
            FROM chat
            WHERE (user1 = %s AND user2 = %s)
               OR (user1 = %s AND user2 = %s)
            """,
            (user1, user2, user2, user1)
        )

        chat = cur.fetchone()

        if chat:
            chat_id = chat["chatId"]
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

        return jsonify({
            "status": "success",
            "message": "Chat created",
            "chat_id": chat_id
        })

    except pymysql.err.Error as e:
        return jsonify({"status": "error", "message": str(e)}), 500

    finally:
        if cur:
            cur.close()
        if conn:
            conn.close()