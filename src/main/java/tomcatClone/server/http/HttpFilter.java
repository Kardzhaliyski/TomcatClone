package server.http;

import server.dispatcher.FilterChain;

import java.io.IOException;

public class HttpFilter {

    public HttpFilter() {
        init();
    }

    public void init()  {}
    public void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException {

    }

}
