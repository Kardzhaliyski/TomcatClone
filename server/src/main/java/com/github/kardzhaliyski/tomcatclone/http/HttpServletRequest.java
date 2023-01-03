package com.github.kardzhaliyski.tomcatclone.http;

import com.github.kardzhaliyski.tomcatclone.dispatcher.PathData;
import com.github.kardzhaliyski.tomcatclone.server.Server;
import com.github.kardzhaliyski.tomcatclone.utils.PathParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpServletRequest {
    private static final String SESSION_COOKIE_NAME = HttpSession.SESSION_COOKIE_NAME;
    private ServletContext servletContext;
    public String method;
    public String path;
    public String protocol;
    public Map<String, String> headers = new HashMap<>();
    public Map<String, String> params = new HashMap<>();
    private Cookie[] cookies = null;
    private String servletPath;
    private String pathInfo;
    private InputStream inputStream;
    private Reader reader = null;

    private HttpSession session = null;

    public HttpServletRequest(InputStream inputStream) {
        this.inputStream = inputStream;
        setStatusLineParams(inputStream);
        setHeaders(inputStream);
    }

    private void setStatusLineParams(InputStream inputStream) {
        String line = readLine(inputStream);
        String[] info = line.split(" ");
        method = info[0];
        setPath(info[1]);
        protocol = info[2];
    }

    public HttpServletRequest(HttpServletRequest req) {
        this.method = req.method;
        this.path = req.path;
        this.protocol = req.protocol;
        this.headers = req.headers;
        this.params = req.params;
        this.servletPath = req.servletPath;
        this.pathInfo = req.pathInfo;
        this.cookies = req.cookies;
        this.inputStream = req.inputStream;
        this.reader = req.reader;
        this.session = req.session;
        this.servletContext = req.servletContext;
    }

    private void setHeaders(InputStream inputStream) {
        String line;
        while ((line = readLine(inputStream)) != null) {
            if (line.isBlank()) {
                break;
            }

            for (int i = 0; i < line.length(); i++) {
                if (line.charAt(i) != ':') {
                    continue;
                }

                String key = line.substring(0, i).trim();
                String value = line.substring(i + 1).trim();
                if(key.equalsIgnoreCase("Cookie")) {
                    setCookies(value);
                } else {
                    headers.put(key, value);
                }
                break;
            }
        }
    }

    private void setCookies(String cookiesString) {
        List<Cookie> cookieList = new ArrayList<>();
        for (String aCookie : cookiesString.split(";")) {
            String[] kvp = aCookie.split("=");
            if (kvp.length != 2) {
                continue;
            }

            String key = kvp[0];
            String value = kvp[1];
            Cookie cookie = new Cookie(key, value);
            cookieList.add(cookie);

            if(key.equals(SESSION_COOKIE_NAME)) {
                session = servletContext.getSession(value);
                if(session != null) {
                    session.isNew = false;
                    session.lastAccessedTime = System.currentTimeMillis();
                }
            }
        }

        if(cookieList.size() > 0) {
            this.cookies = cookieList.toArray(Cookie[]::new);
        }
    }

    public void setPath(String path) {
        PathData parse = PathParser.parse(path);

        this.path = parse.path;
        this.params = parse.params;
    }

    private String readLine(InputStream in) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int b;
        try {
            while ((b = in.read()) != -1) {
                if ((char) b == '\n') {
                    break;
                }

                buffer.write(b);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String str = buffer.toString(StandardCharsets.UTF_8);
        return str.length() == 0 ? null : str;
    }

    public String getParameter(String name) {
        return params.get(name);
    }

    public Reader getReader() {
        if(reader == null) {
            //todo
        }

        return reader;
    }

    public String getPathInfo() {
        return pathInfo;
    }
    public String getProtocol() {
        return protocol;
    }

    public RequestDispatcher getRequestDispatcher(String path) {
        return Server.getInstance().getRequestDispatcher(path);
    }

    public String getMethod() {
        return method;
    }

    public String getServletPath() {
        return servletPath;
    }

    public String getHeader(String name) {
        return headers.get(name);
    }


    public void setPathInfo(String pathInfo) {
        this.pathInfo = pathInfo;
    }

    public void setServletPath(String servletPath) {
        this.servletPath = servletPath;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public HttpSession getSession() {
        return getSession(true);
    }

    public HttpSession getSession(boolean create) {
        if(session != null) {
            return session;
        }

        if(!create) {
            return null;
        }

        HttpSession hs = servletContext.createSession();
        this.session = hs;
        return hs;
    }

    public void setContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}