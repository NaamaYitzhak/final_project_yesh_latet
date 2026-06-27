from flask import Blueprint, request, jsonify
import pymysql

from database.db import get_db

volunteer_bp = Blueprint("volunteer", __name__)


@volunteer_bp.route("/submitVolunteerRequest", methods=["POST"])
def submit_volunteer_request():
    data = request.get_json()

    charity_username = data.get("username")
    content = data.get("content")
    created_at = data.get("created_at")

    if not charity_username or not content or not created_at:
        return jsonify({
            "status": "error",
            "message": "Missing fields"
        }), 400

    conn = None
    cur = None

    try:
        conn = get_db()
        cur = conn.cursor()

        cur.execute(
            """
            INSERT INTO volunteer_requests (charity_username, content, created_at)
            VALUES (%s, %s, %s)
            """,
            (charity_username, content, created_at)
        )

        conn.commit()

        return jsonify({
            "status": "success",
            "message": "Volunteer request submitted"
        })

    except pymysql.err.Error as e:
        return jsonify({
            "status": "error",
            "message": str(e)
        }), 500

    finally:
        if cur:
            cur.close()
        if conn:
            conn.close()


@volunteer_bp.route("/getVolunteerRequests", methods=["GET"])
def get_volunteer_requests():
    conn = None
    cur = None

    try:
        conn = get_db()
        cur = conn.cursor()

        cur.execute(
            """
            SELECT id, charity_username, content, created_at
            FROM volunteer_requests
            ORDER BY created_at DESC
            """
        )

        requests = cur.fetchall()

        return jsonify({
            "status": "success",
            "data": requests
        })

    except pymysql.err.Error as e:
        return jsonify({
            "status": "error",
            "message": str(e)
        }), 500

    finally:
        if cur:
            cur.close()
        if conn:
            conn.close()
