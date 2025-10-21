# 📘 OOP Android Lab – API & SQLite Example

This project demonstrates how **Object-Oriented Programming (OOP)** concepts integrate with real-world **Android development**, focusing on API communication, JSON handling, and local persistence using SQLite.

---

## 🧩 Key Learning Objectives

1. **Encapsulation** – model API data in a `Post` class.  
2. **Abstraction** – use `AsyncTask` for background operations.  
3. **Persistence** – store API results locally with SQLite.  
4. **Separation of concerns** – network, database, and UI handled in distinct components.  
5. **Clean architecture** – constants in resources, no hardcoded strings.

---

## 📱 Application Features

| Feature | Description |
|----------|-------------|
| **GET request** | Fetches a list of posts from `https://jsonplaceholder.typicode.com/posts` and displays the first 5. |
| **POST request** | Sends a JSON payload to the same endpoint and displays the created post. |
| **POST + Save to DB** | Sends data via POST, then stores the returned object in a local SQLite database. |
| **Show Local DB** | Lists all locally stored posts from the database. |

---

## 🧠 Concepts Covered in Class

- `AsyncTask` – how background threads prevent UI blocking.  
- `HttpURLConnection` – basic networking without third-party libraries.  
- `JSONObject` / `JSONArray` – JSON parsing and object mapping.  
- `SQLiteOpenHelper` – managing local database lifecycle.  
- `ContentValues` and `Cursor` – performing CRUD operations.  
- `getString(R.string.key)` – using string resources instead of hardcoded text.  

---

## 🧱 Project Structure

app/
├── java/ro/dalbe/myapplication/
│    ├── MainActivity.java              # Handles UI and button logic
│    ├── Post.java                      # OOP model class
│    ├── PostDatabaseHelper.java        # SQLite helper with CRUD
│
├── res/layout/
│    └── activity_main.xml              # UI layout with 4 buttons + ScrollView
│
├── res/values/
│    └── strings.xml                    # Centralized text resources
│
└── AndroidManifest.xml                 # Internet permission + app setup

---

## ⚙️ Permissions

Make sure your `AndroidManifest.xml` includes:

<uses-permission android:name="android.permission.INTERNET" />

▶️ How to Run
	1.	Clone the repository in Android Studio.
	2.	Sync Gradle and build the project.
	3.	Run on an emulator or a device with internet access.
	4.	Click buttons in order: POST, GET, POST + Save to DB, then Show Local DB.

⸻

🏁 Exercise Ideas for Students
	•	Add a ProgressBar visible while loading data.
	•	Extend PostDatabaseHelper with an update/delete method.
	•	Display local posts in a RecyclerView instead of TextView.
	•	Add localization: create values-ro/strings.xml and values-hu/strings.xml.
	•	Replace AsyncTask with ExecutorService or Retrofit for modern practice.

⸻

📚 Credits

Developed for OOP Laboratory Course
Faculty of Informatics
Educational example prepared by Török Alpár Levente (Dalbe Digital Agency)
