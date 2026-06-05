from flask import Blueprint, request, jsonify
import pymysql

from database.db import get_db

food_bp = Blueprint("food", __name__)


@food_bp.route("/getFood", methods=["GET"])
def get_food():
    conn = None
    cur = None

    try:
        conn = get_db()
        cur = conn.cursor()

        cur.execute("SELECT * FROM food")
        food_items = cur.fetchall()

        return jsonify({"status": "success", "data": food_items})

    except pymysql.err.Error:
        return jsonify({"status": "error", "message": "Database error"}), 500

    finally:
        if cur:
            cur.close()
        if conn:
            conn.close()


@food_bp.route("/submitFood", methods=["POST"])
def submit_food():
    data = request.get_json()

    amount = data.get("amount")
    food_type = data.get("type")
    created_at = data.get("created_at")
    username = data.get("username")

    if not amount or not food_type or not created_at or not username:
        return jsonify({"status": "error", "message": "Missing fields"}), 400

    conn = None
    cur = None

    try:
        conn = get_db()
        cur = conn.cursor()

        cur.execute(
            "INSERT INTO food (amount, type, created_at, username) VALUES (%s, %s, %s, %s)",
            (amount, food_type, created_at, username)
        )

        conn.commit()
        return jsonify({"status": "success", "message": "Food submitted"})

    except pymysql.err.Error as e:
        return jsonify({"status": "error", "message": str(e)}), 500

    finally:
        if cur:
            cur.close()
        if conn:
            conn.close()


@food_bp.route("/deleteFood", methods=["POST"])
def delete_food():
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

        return jsonify({"status": "success", "message": "Food deleted"})

    except pymysql.err.Error as e:
        return jsonify({"status": "error", "message": str(e)}), 500

    finally:
        if cur:
            cur.close()
        if conn:
            conn.close()