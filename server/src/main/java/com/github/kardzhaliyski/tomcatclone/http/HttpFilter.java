package com.github.kardzhaliyski.tomcatclone.http;

import com.github.kardzhaliyski.tomcatclone.server.ServletContext;
import com.github.kardzhaliyski.tomcatclone.dispatcher.FilterChain;

import java.io.IOException;

public class HttpFilter {
    private ServletContext servletContext = null;

    public HttpFilter() {
        init();
    }

    public void init()  {}
    public void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException {

    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}