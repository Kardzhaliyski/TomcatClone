package server.dispatcher;

import server.http.HttpFilter;
import server.http.HttpServlet;
import server.http.HttpServletRequest;
import server.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayDeque;

public class FilterChain {
    private final ArrayDeque<HttpFilter> filterQueue = new ArrayDeque();
    private HttpServlet servlet = null;
    private boolean servletUsed = false;

    public void doFilter(HttpServletRequest req, HttpServletResponse res) throws IOException {
        //todo test what happend with filterchain
        if (!filterQueue.isEmpty()) {
            HttpFilter filter = filterQueue.pop();
            filter.doFilter(req, res, this);
            return;
        }

        if (servlet != null && !servletUsed) {
            servletUsed = true;
            servlet.service(req, res);
        }
    }

    void addFilter(HttpFilter filter) {
        filterQueue.offer(filter);
    }

    void setServlet(HttpServlet servlet) {
        this.servlet = servlet;
    }

}