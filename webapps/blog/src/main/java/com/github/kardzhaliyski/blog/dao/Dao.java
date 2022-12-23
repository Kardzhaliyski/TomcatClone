package com.github.kardzhaliyski.blog.dao;

import com.github.kardzhaliyski.blog.model.Comment;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import com.github.kardzhaliyski.blog.dao.mapper.CommentMapper;
import com.github.kardzhaliyski.blog.dao.mapper.PostMapper;
import com.github.kardzhaliyski.blog.model.Post;

import java.io.IOException;

public class Dao {

    private SqlSessionFactory sessionFactory;

    public Dao() {
        try {
            sessionFactory = SessionFactoryInstance.getInstance();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Post getPostById(int id) {
        try (SqlSession sqlSession = sessionFactory.openSession()) {
            PostMapper mapper = sqlSession.getMapper(PostMapper.class);
            Post post = mapper.getPostById(id);
            sqlSession.commit();
            return post;
        }
    }

    public int addPost(Post post) {
        try (SqlSession sqlSession = sessionFactory.openSession()) {
            PostMapper mapper = sqlSession.getMapper(PostMapper.class);
            int id = mapper.addPost(post);
            sqlSession.commit();
            return id;
        }
    }

    public Post[] getAllPosts() {
        try (SqlSession sqlSession = sessionFactory.openSession()) {
            PostMapper mapper = sqlSession.getMapper(PostMapper.class);
            Post[] posts = mapper.getAllPosts();
            sqlSession.commit();
            return posts;
        }
    }

    public Comment[] getAllCommentsForPost(int id) {
        try (SqlSession sqlSession = sessionFactory.openSession()) {
            CommentMapper mapper = sqlSession.getMapper(CommentMapper.class);
            Comment[] comments = mapper.getAllCommentsForPost(id);
            sqlSession.commit();
            return comments;
        }
    }

    public void addComment(Comment comment) {
        try (SqlSession sqlSession = sessionFactory.openSession()) {
            CommentMapper mapper = sqlSession.getMapper(CommentMapper.class);
            mapper.addComment(comment);
            sqlSession.commit();
        }
    }

    public Comment[] getAllComments() {
        try (SqlSession sqlSession = sessionFactory.openSession()) {
            CommentMapper mapper = sqlSession.getMapper(CommentMapper.class);
            Comment[] comments = mapper.getAllComments();
            sqlSession.commit();
            return comments;
        }
    }

    public int deletePostById(int id) {
        try (SqlSession sqlSession = sessionFactory.openSession(true)) {
            PostMapper mapper = sqlSession.getMapper(PostMapper.class);
            int comments = mapper.deleteById(id);
            return comments;
        }
    }

    public boolean containsPost(int id) {
        try (SqlSession sqlSession = sessionFactory.openSession(true)) {
            PostMapper mapper = sqlSession.getMapper(PostMapper.class);
            return mapper.contains(id);
        }
    }

    public void updatePost(Post post) {
        try (SqlSession sqlSession = sessionFactory.openSession(true)) {
            PostMapper mapper = sqlSession.getMapper(PostMapper.class);
            mapper.updatePost(post);
        }
    }
}
