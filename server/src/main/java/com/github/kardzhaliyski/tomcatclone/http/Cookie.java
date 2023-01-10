package com.github.kardzhaliyski.tomcatclone.http;

import java.util.*;

public class Cookie {

    private static final String DOMAIN = "Domain"; // ;Domain=VALUE ... domain that sees cookie
    private static final String MAX_AGE = "Max-Age"; // ;Max-Age=VALUE ... cookies auto-expire
    private static final String PATH = "Path"; // ;Path=VALUE ... URLs that see the cookie
    private static final String SECURE = "Secure"; // ;Secure ... e.g. use SSL
    private static final String HTTP_ONLY = "HttpOnly";
    String name;
    String value;
    Map<String, String> attributes;

    public Cookie(String name, String value) {
        this.name = name;
        this.value = value;
        attributes = new HashMap<>();
    }

    public Map<String, String> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

    public String getAttribute(String name) {
        return attributes.get(name);
    }

    public void setAttribute(String name, String value) {
        if(name == null || name.isBlank()) {
            throw new IllegalArgumentException("Cookie attribute name should not be null or empty!");
        }

        if (MAX_AGE.equalsIgnoreCase(name) && value != null) {
            setMaxAge(Integer.parseInt(value));
        } else {
            putAttribute(name, value);
        }
    }

    private void putAttribute(String name, String value) {
        if (value == null) {
            attributes.remove(name);
        } else {
            attributes.put(name, value);
        }
    }

    public int getMaxAge() {
        String maxAge = attributes.get(MAX_AGE);
        return maxAge == null ? -1 : Integer.parseInt(maxAge);
    }

    public void setMaxAge(int seconds) {
        putAttribute(MAX_AGE, seconds < 0 ? null : String.valueOf(seconds));
    }

    public String getDomain() {
        return attributes.get(DOMAIN);
    }

    public void setDomain(String domain) {
        putAttribute(DOMAIN, domain);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return this.value;
    }

    public Cookie setValue(String value) {
        this.value = value;
        return this;
    }

    public String getPath() {
        return attributes.get(PATH);
    }

    public void setPath(String path) {
        putAttribute(PATH, path);
    }

    public boolean getSecure() {
        return Boolean.parseBoolean(attributes.get(SECURE));
    }

    public void setSecure(boolean flag) {
        putAttribute(SECURE, String.valueOf(flag));
    }

    public boolean isHttpOnly() {
        return Boolean.parseBoolean(getAttribute(HTTP_ONLY));
    }

    public void setHttpOnly(boolean httpOnly) {
        putAttribute(HTTP_ONLY, String.valueOf(httpOnly));
    }
}
