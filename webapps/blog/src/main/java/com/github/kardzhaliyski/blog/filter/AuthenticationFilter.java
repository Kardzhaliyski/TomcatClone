package com.github.kardzhaliyski.blog.filter;

import java.io.IOException;

import com.github.kardzhaliyski.tomcatclone.dispatcher.FilterChain;
import com.github.kardzhaliyski.tomcatclone.http.HttpFilter;
import com.github.kardzhaliyski.tomcatclone.http.HttpServletRequest;
import com.github.kardzhaliyski.tomcatclone.http.HttpServletResponse;
import com.github.kardzhaliyski.blog.service.AuthenticationService;
import com.github.kardzhaliyski.blog.Utils;

import static com.github.kardzhaliyski.tomcatclone.http.HttpServletResponse.SC_UNAUTHORIZED;


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
