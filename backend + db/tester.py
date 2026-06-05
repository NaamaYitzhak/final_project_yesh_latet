import requests
from datetime import datetime

BASE_URL = "http://127.0.0.1:5000"


def print_response(response):
    print("\nStatus Code:", response.status_code)
    try:
        print("Response:")
        print(response.json())
    except Exception:
        print(response.text)
    print("-" * 50)


def signup():
    print("\n--- Signup ---")
    username = input("Username: ")
    email = input("Email: ")
    password = input("Password: ")
    user_type = input("Type (business / charity / volunteer): ")

    data = {
        "username": username,
        "email": email,
        "password": password,
        "type": user_type
    }

    response = requests.post(f"{BASE_URL}/signup", json=data)
    print_response(response)


def login():
    print("\n--- Login ---")
    username = input("Username: ")
    password = input("Password: ")

    data = {
        "username": username,
        "password": password
    }

    response = requests.post(f"{BASE_URL}/login", json=data)
    print_response(response)


def submit_food():
    print("\n--- Submit Food ---")
    username = input("Business username: ")
    food_type = input("Food type: ")
    amount = input("Amount: ")

    data = {
        "username": username,
        "type": food_type,
        "amount": amount,
        "created_at": datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    }

    response = requests.post(f"{BASE_URL}/submitFood", json=data)
    print_response(response)


def get_food():
    print("\n--- Get Food ---")

    response = requests.get(f"{BASE_URL}/getFood")
    print_response(response)


def delete_food():
    print("\n--- Delete Food ---")
    food_id = input("Food ID: ")

    data = {
        "food_id": food_id
    }

    response = requests.post(f"{BASE_URL}/deleteFood", json=data)
    print_response(response)


def create_chat():
    print("\n--- Create / Send Chat Message ---")
    user1 = input("Sender username: ")
    user2 = input("Receiver username: ")
    message = input("Message: ")

    data = {
        "user1": user1,
        "user2": user2,
        "message": message
    }

    response = requests.post(f"{BASE_URL}/chat", json=data)
    print_response(response)


def get_chats():
    print("\n--- Get Chats ---")
    username = input("Username: ")

    data = {
        "username": username
    }

    response = requests.post(f"{BASE_URL}/getChat", json=data)
    print_response(response)


def get_messages():
    print("\n--- Get Messages ---")
    chat_id = input("Chat ID: ")

    data = {
        "chatId": chat_id
    }

    response = requests.post(f"{BASE_URL}/getMessage", json=data)
    print_response(response)


def test_server():
    print("\n--- Test Server ---")

    response = requests.get(f"{BASE_URL}/")
    print_response(response)


def menu():
    while True:
        print("""
========== YESH LATET BACKEND TESTER ==========

1. Test server
2. Signup
3. Login
4. Submit food
5. Get food
6. Delete food
7. Create chat / send first message
8. Get chats
9. Get messages
0. Exit

===============================================
""")

        choice = input("Choose option: ")

        try:
            if choice == "1":
                test_server()
            elif choice == "2":
                signup()
            elif choice == "3":
                login()
            elif choice == "4":
                submit_food()
            elif choice == "5":
                get_food()
            elif choice == "6":
                delete_food()
            elif choice == "7":
                create_chat()
            elif choice == "8":
                get_chats()
            elif choice == "9":
                get_messages()
            elif choice == "0":
                print("Bye :)")
                break
            else:
                print("Invalid option")

        except requests.exceptions.ConnectionError:
            print("\nCould not connect to the server.")
            print("Make sure Flask is running:")
            print("python app.py")
            print("-" * 50)


if __name__ == "__main__":
    menu()