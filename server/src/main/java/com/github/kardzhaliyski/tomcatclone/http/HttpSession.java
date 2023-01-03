package com.github.kardzhaliyski.tomcatclone.http;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class HttpSession {

    public static final String SESSION_COOKIE_NAME = "MYSESSION";
    private static final long DEFAULT_MAX_INACTIVE_INTERVAL = 30 * 60 * 1000;
    private static final long MILLIS_IN_A_SECOUND = 1000;
    private final long creationTime;
    long lastAccessedTime;
    private long maxInactiveInterval;
    boolean isNew = true;
    private ServletContext servletContext;
    final String id;
    private Map<String, Object> attributes = null;
    private boolean isValid = true;

    public HttpSession(ServletContext servletContext, String id) {
        this.servletContext = servletContext;
        this.id = id;
        this.creationTime = System.currentTimeMillis();
        this.lastAccessedTime = this.creationTime;
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    public void setAttribute(String name, Object value) {
        validateSession();
        if (attributes == null) {
            attributes = new HashMap<>();
        }

        if (value == null) {
            removeAttribute(name);
            return;
        }

        attributes.put(name, value);
    }

    public void removeAttribute(String name) {
        validateSession();
        if (attributes == null) {
            return;
        }

        attributes.remove(name);
    }

    public Object getAttribute(String name) {
        validateSession();
        return attributes.get(name);
    }

    public Enumeration<String> getAttributeNames() {
        validateSession();
        return Collections.enumeration(attributes.keySet());
    }

    private void validateSession() {
        if(!isValid) {
            throw new IllegalStateException("Session is not valid.");
        }
    }

    public long getCreationTime() {
        return creationTime;
    }

    public String getId() {
        return id;
    }


    public long getLastAccessedTime() {
        return lastAccessedTime;
    }

    public long getMaxInactiveInterval() {
        return maxInactiveInterval;
    }

    public void setMaxInactiveInterval(long maxInactiveInterval) {
        this.maxInactiveInterval = maxInactiveInterval * MILLIS_IN_A_SECOUND;
    }

    public void invalidate() {
        attributes.clear();
        attributes = null;
        servletContext.removeSession(id);
        isValid = false;
    }
}
