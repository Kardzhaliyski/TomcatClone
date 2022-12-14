package webapps.blog.src.filter;

import java.io.IOException;

import server.http.servlet.FilterChain;
import server.http.servlet.HttpFilter;
import server.http.servlet.HttpServletRequest;
import server.http.servlet.HttpServletResponse;
import webapps.blog.src.model.AuthToken;
import webapps.blog.src.service.AuthenticationService;
import webapps.blog.src.servlet.Utils;

import static server.http.servlet.HttpServletResponse.SC_UNAUTHORIZED;

public class AuthenticationFilter extends HttpFilter {

    private static AuthenticationService authService;

    @Override
    public void init()  {
        authService = AuthenticationService.getInstance();
    }

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException {

        String authHeader = req.getHeader("Authorization");
        AuthToken authToken = authService.getAuthToken(authHeader);
        if(authToken == null) {
            Utils.writeErrorAsJson(res, SC_UNAUTHORIZED, "User not logged in.");
            return;
        }

        chain.doFilter(req, res);
    }
}
