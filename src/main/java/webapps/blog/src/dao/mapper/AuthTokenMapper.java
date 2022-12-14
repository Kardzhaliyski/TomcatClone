package webapps.blog.src.dao.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import webapps.blog.src.model.AuthToken;

public interface AuthTokenMapper {
    @Select("SELECT * FROM tokens WHERE token = #{token}")
    public AuthToken getBy(String token);

    @Insert("INSERT INTO tokens(token, username, created_date, expiration_date) VALUES (#{token}, #{uname}, #{createdDate}, #{expirationDate})")
    public void addToken(AuthToken token);
}
