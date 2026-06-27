from flask import Flask
from flask_cors import CORS

from routes.auth_routes import auth_bp
from routes.chat_routes import chat_bp
from routes.food_routes import food_bp
from routes.volunteer_routes import volunteer_bp

app = Flask(__name__)
CORS(app)

app.register_blueprint(auth_bp)
app.register_blueprint(chat_bp)
app.register_blueprint(food_bp)
app.register_blueprint(volunteer_bp)

@app.route("/", methods=["GET"])
def home():
    return {
        "status": "success",
        "message": "Yesh Latet backend is running"
    }

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)

print(__name__)