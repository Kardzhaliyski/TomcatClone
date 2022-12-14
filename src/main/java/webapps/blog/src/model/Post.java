package model;

public class Post {
    public int id;
    public Integer userId;
    public String title;
    public String body;

    public Post(int user_id, String title, String body) {
        this.userId = user_id;
        this.title = title;
        this.body = body;
    }

    public Post(int id, int user_id, String title, String body) {
        this.id = id;
        this.userId = user_id;
        this.title = title;
        this.body = body;
    }
    public void setId(int id) {
        this.id = id;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }
}
