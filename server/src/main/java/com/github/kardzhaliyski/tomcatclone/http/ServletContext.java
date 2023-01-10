package com.github.kardzhaliyski.tomcatclone.http;

import com.github.kardzhaliyski.tomcatclone.dispatcher.ServletDispatcher;
import com.github.kardzhaliyski.tomcatclone.http.servlet.StaticContentServlet;
import org.apache.commons.codec.digest.DigestUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.*;

public class ServletContext {
    private static final long ONE_HOUR_IN_MILLIS = 1000 * 60 * 60;
    private static final Random random = new Random();
    public ServletDispatcher dispatcher;
    public Path root;
    public boolean showDirectoryContent;
    private HttpServlet staticContentServlet = null;
    private final Map<String, HttpSession> sessions = new HashMap<>();

    public ServletContext(File dir) throws IOException, SAXException, ParserConfigurationException {
        root = dir.toPath();
        Path webInfDir = root.resolve("WEB-INF");
        List<URL> urls = new ArrayList<>();
        urls.add(webInfDir.resolve("classes").toFile().toURI().toURL());
        File lib = webInfDir.resolve("lib").toFile();
        File[] files = lib.listFiles(n -> n.getName().endsWith(".jar"));
        for (File file : files) {
            URL url = file.toURI().toURL();
            urls.add(url);
        }

        URLClassLoader urlClassLoader = URLClassLoader.newInstance(urls.toArray(URL[]::new), this.getClass().getClassLoader());
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document webXml = documentBuilder.parse(webInfDir.resolve("web.xml").toFile());

        this.dispatcher = new ServletDispatcher(urlClassLoader, webXml);
        this.dispatcher.setServletContext(this);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                for (Map.Entry<String, HttpSession> kvp : sessions.entrySet()) {
                    HttpSession session = kvp.getValue();
                    long maxInactiveInterval = session.getMaxInactiveInterval();
                    if (maxInactiveInterval <= 0) {
                        return;
                    }

                    if (maxInactiveInterval + session.lastAccessedTime < System.currentTimeMillis()) {
                        session.invalidate();
                    }
                }
            }
        }, ONE_HOUR_IN_MILLIS, ONE_HOUR_IN_MILLIS);
    }

    public HttpServlet getStaticContentServlet() {
        if (staticContentServlet == null) {
            staticContentServlet = new StaticContentServlet();
            staticContentServlet.setServletContext(this);
        }

        return staticContentServlet;
    }

    public void dispatch(HttpServletRequest request, Socket socket) throws IOException {
        dispatcher.dispatch(request, socket);
    }

    public RequestDispatcher getRequestDispatcher(String path) {
        return dispatcher.getRequestDispatcher(path);
    }

    public HttpSession getSession(String value) {
        HttpSession session = sessions.get(value);
        if (session == null) {
            return null;
        }
        long maxInactiveInterval = session.getMaxInactiveInterval();
        if (maxInactiveInterval <= 0) {
            return session;
        }

        if (session.lastAccessedTime + maxInactiveInterval < System.currentTimeMillis()) {
            session.invalidate();
            return null;
        }

        return session;
    }

    public HttpSession createSession() {
        String id = generateSessionId();
        HttpSession hs = new HttpSession(this, id);
        sessions.put(id, hs);
        return hs;
    }

    private String generateSessionId() {
        int salt = random.nextInt();
        String id;
        do {
            id = DigestUtils.sha1Hex(String.valueOf(salt));
        } while (sessions.containsKey(id));
        return id;
    }

    void removeSession(String id) {
        sessions.remove(id);
    }
}
