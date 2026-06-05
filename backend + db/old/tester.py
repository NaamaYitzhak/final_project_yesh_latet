import requests
import json

def main():
    # First input: path (URL)
    path = input("Enter backend path (URL): ").strip()

    # Second input: JSON (optional)
    json_input = input("Enter JSON body (leave empty for GET request): ").strip()

    try:
        if json_input == "":
            # GET request
            response = requests.get(path)
        else:
            # Parse JSON input
            data = json.loads(json_input)

            # POST request
            response = requests.post(path, json=data)

        print("\nStatus Code:", response.status_code)

        try:
            print("Response JSON:")
            print(response.json())
        except ValueError:
            print("Response Text:")
            print(response.text)

    except requests.exceptions.RequestException as e:
        print("Request failed:", e)
    except json.JSONDecodeError:
        print("Invalid JSON format")


if __name__ == "__main__":
    main()
