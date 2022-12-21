package service;


import org.apache.commons.codec.digest.DigestUtils;
import dao.TokenDao;
import model.AuthToken;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AuthenticationService {

    public static AuthenticationService instance = null;
    private final TokenDao dao;
    private final Random random;
    private Map<String, AuthToken> tokenMap = new HashMap<>(); //todo remove if expired

    public static AuthenticationService getInstance() {
        if(instance == null) {
            instance = new AuthenticationService();
        }

        return instance;
    }

    private AuthenticationService() {
        this.tokenMap = new HashMap<>();
        this.dao = new TokenDao();
        this.random = new Random();
    }

    public boolean isValid(String tokenHeader) {
        if(tokenHeader == null) {
            return false;
        }

        boolean correctSchema = tokenHeader.startsWith("Bearer ");
        if (!correctSchema) {
            return false;
        }

        String token = tokenHeader.substring(7);
        if (tokenMap.containsKey(token)) {
            return true;
        }

        AuthToken authToken = dao.getToken(token);
        if (authToken == null) {
            return false;
        }

        tokenMap.put(token, authToken);
        return true;
    }

    public String createNewToken(String username) {
        int salt = random.nextInt();
        String token;
        do {
            token = DigestUtils.sha1Hex(username + "$" + salt);
        } while (dao.containsToken(token));
        AuthToken authToken = new AuthToken(username, token);
        dao.addToken(authToken);
        tokenMap.put(token, authToken);
        return token;
    }

    public AuthToken getToken(String token) {
        AuthToken authToken = tokenMap.get(token);
        if(authToken == null) {
            throw new IllegalStateException("Token hasn't been cached. Try login again.");
        }

        return authToken;
    }

    public AuthToken getAuthToken(String authHeader) {
        if (!isValid(authHeader)) {
            return null;
        }

        String token = authHeader.substring(7);
        return getToken(token);
    }
}
