from flask import Blueprint, request, jsonify
import pymysql

from database.db import get_db
from utils.password_utils import hash_password
from utils.validation import is_valid_email

auth_bp = Blueprint("auth", __name__)


@auth_bp.route("/signup", methods=["POST"])
def signup():
    data = request.get_json()

    username = data.get("username")
    password = data.get("password")
    email = data.get("email")
    user_type = data.get("type")

    if not username or not password or not email or not user_type:
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
            (username, hash_password(password), email, user_type)
        )

        conn.commit()
        return jsonify({"status": "success", "message": "Account created"})

    except pymysql.err.IntegrityError:
        return jsonify({"status": "error", "message": "Username or email exists"}), 409

    except pymysql.err.Error:
        return jsonify({"status": "error", "message": "Database error"}), 500

    finally:
        if cur:
            cur.close()
        if conn:
            conn.close()

@auth_bp.route("/login", methods=["POST"])
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
            "SELECT username, email, type FROM users WHERE username = %s AND password = %s",
            (username, hash_password(password))
        )

        user = cur.fetchone()

        if not user:
            return jsonify({"status": "error", "message": "Invalid username or password"}), 401

        return jsonify({
            "status": "success",
            "message": "Login successful",
            "user": user
        })

    except pymysql.err.Error:
        return jsonify({"status": "error", "message": "Database error"}), 500

    finally:
        if cur:
            cur.close()
        if conn:
            conn.close()