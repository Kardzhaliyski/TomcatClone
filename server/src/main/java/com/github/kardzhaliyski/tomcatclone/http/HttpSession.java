package com.github.kardzhaliyski.tomcatclone.http;

public class HttpSession {

    public static final String SESSION_COOKIE_NAME = "MYSESSION";
    private final String id;

    public HttpSession(String id) {
        this.id = id;
    }
}
