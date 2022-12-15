package filter;

import java.io.IOException;

import server.dispatcher.FilterChain;
import server.http.servlet.HttpFilter;
import server.http.servlet.HttpServletRequest;
import server.http.servlet.HttpServletResponse;
import model.AuthToken;
import service.AuthenticationService;
import servlet.Utils;

import static server.http.servlet.HttpServletResponse.SC_UNAUTHORIZED;

public class AuthenticationFilter extends HttpFilter {

    private static AuthenticationService authService;

    @Override
    public void init()  {
        authService = AuthenticationService.getInstance();
    }

    @Override
    public void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException {

        String authHeader = req.getHeader("Authorization");
        if(!authService.isValid(authHeader)) {
            Utils.writeErrorAsJson(res, SC_UNAUTHORIZED, "User not logged in.");
            return;
        }

        chain.doFilter(req, res);
    }
}
