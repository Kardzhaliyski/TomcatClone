package com.github.kardzhaliyski.blog.dao.mapper;

import com.github.kardzhaliyski.blog.model.Comment;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

public interface CommentMapper {

    @Select("SELECT * FROM comments WHERE post_id = #{id}")
    Comment[] getAllCommentsForPost(int id);

    @Select("SELECT * FROM comments")
    Comment[] getAllComments();

    @Insert("INSERT INTO comments(post_id, name, email, body) VALUES (#{postId}, #{name}, #{email}, #{body})")
    void addComment(Comment comment);
}
