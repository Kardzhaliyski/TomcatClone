package com.github.kardzhaliyski.blog.dao;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

public class SessionFactoryInstance {
    private static SqlSessionFactory instance = null;
    public static SqlSessionFactory getInstance() throws IOException {
        if(instance == null) {
            Properties properties = Resources.getResourceAsProperties("application.properties");
            InputStream in = Resources.getResourceAsStream("mybatis-config.xml");
            instance = new SqlSessionFactoryBuilder().build(in, properties);
        }

        return instance;
    }
}
