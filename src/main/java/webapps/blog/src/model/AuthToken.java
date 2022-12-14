package webapps.blog.src.model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

public class AuthToken {
    public static final long DEFAULT_EXPIRATION_TIME = 30;
    public static final TemporalUnit DEFAULT_EXPIRATION_UNIT = ChronoUnit.DAYS;
    public String uname;
    public String token;
    public LocalDateTime createdDate;
    public LocalDateTime expirationDate;

    public AuthToken() {
    }

    public AuthToken(String uname, String token) {
        this.uname = uname;
        this.token = token;
        this.createdDate = LocalDateTime.now();
        this.expirationDate = LocalDateTime.now().plus(DEFAULT_EXPIRATION_TIME, DEFAULT_EXPIRATION_UNIT);
    }
}
