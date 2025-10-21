package ro.dalbe.myapplication;
// =====================================================================
// 3. MODEL CLASS - OOP EXAMPLE
// =====================================================================
/**
 * The Post class demonstrates encapsulation.
 * It contains private fields and exposes data through a clean constructor and toString().
 * Each instance represents one post retrieved or created from the API.
 */
public class Post {
    private int id;
    private String title;
    private String body;

    public Post(int id, String title, String body) {
        this.id = id;
        this.title = title;
        this.body = body;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    // Converts object data to a readable string
    @Override
    public String toString() {
        return "Post #" + id + "\nTitle: " + title + "\nBody: " + body;
    }
}