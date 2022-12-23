package com.github.kardzhaliyski.blog.dao;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import com.github.kardzhaliyski.blog.dao.mapper.AuthTokenMapper;
import com.github.kardzhaliyski.blog.model.AuthToken;

import java.io.IOException;

public class TokenDao {

    private SqlSessionFactory sessionFactory;

    public TokenDao() {
        try {
            sessionFactory = SessionFactoryInstance.getInstance();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public AuthToken getToken(String token) {
        try (SqlSession session = sessionFactory.openSession(true)){
            AuthTokenMapper mapper = session.getMapper(AuthTokenMapper.class);
            return mapper.getBy(token);
        }
    }

    public void addToken(AuthToken token) {
        try (SqlSession session = sessionFactory.openSession(true)){
            AuthTokenMapper mapper = session.getMapper(AuthTokenMapper.class);
            mapper.addToken(token);
        }
    }

    public boolean containsToken(String token) {
        try (SqlSession session = sessionFactory.openSession(true)){
            AuthTokenMapper mapper = session.getMapper(AuthTokenMapper.class);
           return mapper.contains(token);
        }
    }
}
