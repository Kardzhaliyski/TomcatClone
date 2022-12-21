package dao.mapper;

import org.apache.ibatis.annotations.*;
import model.Post;

public interface PostMapper {

    @Select("SELECT * FROM posts WHERE id = #{id}")
    public Post getPostById(int id);

    @Select("SELECT * FROM posts")
    public Post[] getAllPosts();


    @Insert("INSERT INTO posts(user_id, title, body) VALUES (#{userId}, #{title}, #{body})")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    public int addPost(Post post);

    @Delete("DELETE FROM posts WHERE id = #{id}")
    int deleteById(int id);

    @Select("SELECT count(id) = 1 FROM posts WHERE id = #{arg0}")
    boolean contains(int id);

    @Update("UPDATE posts SET user_id = #{userId}, title = #{title}, body = #{body} WHERE id = #{id}")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    void updatePost(Post post);
}
