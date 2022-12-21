package server.http;

import server.ServletContext;
import server.dispatcher.FilterChain;

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
