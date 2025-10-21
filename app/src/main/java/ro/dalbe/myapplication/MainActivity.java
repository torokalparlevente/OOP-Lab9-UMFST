package ro.dalbe.myapplication;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * -----------------------------------------------------------
 *  LAB OBJECTIVES
 *  -----------------------------------------------------------
 *  1. Demonstrate OOP concepts (encapsulation with Post class)
 *  2. Explain AsyncTask and UI thread rules
 *  3. Show how to perform GET and POST HTTP requests
 *  4. Parse JSON into Java objects
 *  5. Update UI safely from background threads
 * -----------------------------------------------------------
 */
public class MainActivity extends AppCompatActivity {

    // UI elements
    private TextView textView;
    private Button buttonPost;
    private Button buttonGet;
    private Button buttonPostSave;
    private PostDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Make layout edges compatible with newer Android versions
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Connect UI elements with layout IDs
        textView = findViewById(R.id.textview1);
        buttonPost = findViewById(R.id.buttonPost);
        buttonGet = findViewById(R.id.buttonGet);

        // When user clicks "Send POST Request"
        buttonPost.setOnClickListener(v -> {
            // Execute AsyncTask for POST request
            new PostDataTask().execute("https://jsonplaceholder.typicode.com/posts");
        });

        // When user clicks "Get List"
        buttonGet.setOnClickListener(v -> {
            // Execute AsyncTask for GET request
            new GetDataTask().execute("https://jsonplaceholder.typicode.com/posts");
        });

        buttonPostSave = findViewById(R.id.buttonPostSave);
        dbHelper = new PostDatabaseHelper(this);

        // When clicked, executes new AsyncTask version that also saves to DB
        buttonPostSave.setOnClickListener(v ->
                new PostDataTaskSaveToDB().execute("https://jsonplaceholder.typicode.com/posts")
        );
    }

    // =====================================================================
    // 1. ASYNCTASK FOR POST REQUEST
    // =====================================================================
    private class PostDataTask extends AsyncTask<String, Void, Post> {

        private Exception exception; // used to catch and display errors

        /**
         * Runs on the main (UI) thread before background work begins.
         * Good place to show loading text or a progress bar.
         */
        @Override
        protected void onPreExecute() {
            textView.setText("Sending POST request...");
        }

        /**
         * Runs on a background thread — heavy or slow work goes here.
         * NEVER update UI directly from here.
         */
        @Override
        protected Post doInBackground(String... urls) {
            HttpURLConnection conn = null;
            try {
                // Step 1: Create connection to the given URL
                URL url = new URL(urls[0]);
                conn = (HttpURLConnection) url.openConnection();

                // Step 2: Configure HTTP POST settings
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true); // allows sending a request body

                // Step 3: Create the JSON object we want to send
                JSONObject sendObj = new JSONObject();
                sendObj.put("title", "Hello from Android");
                sendObj.put("body", "This is a test post from our OOP lab");
                sendObj.put("userId", 1);

                // Step 4: Write JSON into the request body
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(sendObj.toString());
                writer.flush();
                writer.close();
                os.close();

                // Step 5: Read API response
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line);
                reader.close();

                // Step 6: Convert response string into JSON object
                JSONObject obj = new JSONObject(sb.toString());

                // Step 7: Map JSON to Java object (OOP concept)
                return new Post(obj.getInt("id"), obj.getString("title"), obj.getString("body"));

            } catch (Exception e) {
                exception = e;
                return null;
            } finally {
                if (conn != null) conn.disconnect();
            }
        }

        /**
         * Runs on the UI thread after background work finishes.
         * This is safe to update UI here.
         */
        @Override
        protected void onPostExecute(Post post) {
            if (exception != null) {
                textView.setText("Error: " + exception.getMessage());
            } else if (post != null) {
                textView.setText("POST response:\n\n" + post.toString());
            } else {
                textView.setText("No response received.");
            }
        }
    }

    // =====================================================================
    // 2. ASYNCTASK FOR GET REQUEST
    // =====================================================================
    private class GetDataTask extends AsyncTask<String, Void, List<Post>> {

        private Exception exception;

        @Override
        protected void onPreExecute() {
            textView.setText("Loading data with GET...");
        }

        @Override
        protected List<Post> doInBackground(String... urls) {
            HttpURLConnection conn = null;
            try {
                // Step 1: Open connection for GET
                URL url = new URL(urls[0]);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                // Step 2: Read JSON response
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line);
                reader.close();

                // Step 3: Convert JSON string to JSONArray
                JSONArray arr = new JSONArray(sb.toString());

                // Step 4: Loop through first 5 posts (to avoid huge output)
                List<Post> list = new ArrayList<>();
                for (int i = 0; i < 5; i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    Post post = new Post(
                            obj.getInt("id"),
                            obj.getString("title"),
                            obj.getString("body")
                    );
                    list.add(post);
                }

                // Step 5: Return list of Post objects
                return list;

            } catch (Exception e) {
                exception = e;
                return null;
            } finally {
                if (conn != null) conn.disconnect();
            }
        }

        @Override
        protected void onPostExecute(List<Post> posts) {
            if (exception != null) {
                textView.setText("Error: " + exception.getMessage());
            } else if (posts != null) {
                // Combine list into one display string
                StringBuilder sb = new StringBuilder("GET response (first 5 posts):\n\n");
                for (Post p : posts) {
                    sb.append(p.toString()).append("\n\n");
                }
                textView.setText(sb.toString());
            } else {
                textView.setText("No data found.");
            }
        }
    }

    // =====================================================================
    // 3. ASYNCTASK FOR POST and SAVE to DB
    // =====================================================================

    /**
     * ---------------------------------------------------------
     *  NEW TASK: PostDataTaskSaveToDB
     *  ---------------------------------------------------------
     *  Sends POST request like before, but also stores the
     *  resulting Post object into the local SQLite database.
     * ---------------------------------------------------------
     */
    private class PostDataTaskSaveToDB extends AsyncTask<String, Void, Post> {

        private Exception exception;

        @Override
        protected void onPreExecute() {
            textView.setText("Sending POST request and saving result locally...");
        }

        @Override
        protected Post doInBackground(String... urls) {
            HttpURLConnection conn = null;
            try {
                // 1. Prepare connection
                URL url = new URL(urls[0]);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);

                // 2. Build JSON payload
                JSONObject sendObj = new JSONObject();
                sendObj.put("title", "New Local Post");
                sendObj.put("body", "This post is stored in SQLite too.");
                sendObj.put("userId", 1);

                // 3. Write to request body
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(sendObj.toString());
                writer.flush();
                writer.close();
                os.close();

                // 4. Read response from API
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line);
                reader.close();

                // 5. Convert response JSON to Post object
                JSONObject obj = new JSONObject(sb.toString());
                return new Post(obj.getInt("id"), obj.getString("title"), obj.getString("body"));

            } catch (Exception e) {
                exception = e;
                return null;
            } finally {
                if (conn != null) conn.disconnect();
            }
        }

        @Override
        protected void onPostExecute(Post post) {
            if (exception != null) {
                textView.setText("Error: " + exception.getMessage());
            } else if (post != null) {
                // 6. Save to local DB
                dbHelper.insertPost(post);

                // 7. Notify user
                textView.setText("POST complete.\nSaved to SQLite DB:\n\n" + post.toString());
            } else {
                textView.setText("No response received.");
            }
        }
    }
}