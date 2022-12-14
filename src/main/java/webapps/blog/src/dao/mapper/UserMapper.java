package webapps.blog.src.dao.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import webapps.blog.src.model.User;

public interface UserMapper {

    @Select("SELECT * FROM users WHERE username = #{username}")
    User getByUsername(String username);

    @Insert("INSERT INTO users(username, first_name, last_name, email, password, salt) VALUES (#{username}, #{firstName}, #{lastName}, #{email}, #{password}, #{salt})")
    void insert(User user);
}
