package com.github.kardzhaliyski.tomcatclone.dispatcher;

import com.github.kardzhaliyski.tomcatclone.utils.PathParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import com.github.kardzhaliyski.tomcatclone.server.ServletContext;
import com.github.kardzhaliyski.tomcatclone.http.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.*;

public class ServletDispatcher {

    private ServletContext servletContext;

    private class ServletRequestDispatcher implements RequestDispatcher {
        private HttpServlet servlet;
        private ServletData servletData;
        private PathData pathData;

        public ServletRequestDispatcher( HttpServlet servlet, ServletData servletData, PathData pathData) {
            this.servlet = servlet;
            this.servletData = servletData;
            this.pathData = pathData;
        }

        public void forward(HttpServletRequest req, HttpServletResponse resp) {
            HttpServletRequest newRequest = new HttpServletRequest(req);
            newRequest.setPathInfo(servletData.pathInfo);
            newRequest.setServletPath(servletData.servletPath);
            newRequest.setPath(pathData.path);
            newRequest.setParams(pathData.params);

            //todo clear outputStream
            try {
                servlet.service(newRequest, resp);
            } catch (IOException e) {
                //todo log error
            }
        }
    }

    private static class NameClassPair {
        String name;
        String className;
    }

    private static class ServletData {
        String servletName = null;
        String servletPath = null;
        String pathInfo = null;
    }

    private static class FilterMapping {
        String name;
        String pattern;

        public FilterMapping(String name, String urlPattern) {
            this.name = name;
            this.pattern = urlPattern;
        }
    }

    private final Map<String, HttpFilter> filters = new HashMap<>();
    private final Map<String, HttpServlet> servlets = new HashMap<>();
    private final List<FilterMapping> filterMappings = new ArrayList<>();
    private final Map<String, String> servletMapping = new LinkedHashMap<>();

    public ServletDispatcher(ClassLoader cl, Document doc) throws ParserConfigurationException, IOException, SAXException {
//        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
//        Document doc = documentBuilder.parse(webXmlDir.resolve("web.xml").toFile());
//        documentBuilder.
        NodeList fn = doc.getElementsByTagName("filter");
        List<NameClassPair> filtersData = parseFilters(fn);
        initObjects(cl, filtersData);

        NodeList sn = doc.getElementsByTagName("servlet");
        List<NameClassPair> servletsData = parseServlets(sn);
        initObjects(cl, servletsData);

        NodeList smn = doc.getElementsByTagName("servlet-mapping");
        parseServletMappings(smn);

        NodeList fmn = doc.getElementsByTagName("filter-mapping");
        parseFilterMappings(fmn);
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public void dispatch(HttpServletRequest request, Socket socket) throws IOException {
        FilterChain chain = getFilterChain(request.path);
        ServletData data = getServletData(request.path);

        if (data.servletName == null) {
            chain.setServlet(servletContext.getStaticContentServlet());
        } else {
            HttpServlet httpServlet = servlets.get(data.servletName);
            if (httpServlet != null) {
                chain.setServlet(httpServlet);
            } else {
                // return 404
                //todo log error
            }
        }

        request.setServletPath(data.servletPath);
        request.setPathInfo(data.pathInfo);
        request.setContext(servletContext);
        HttpServletResponse res = new HttpServletResponse(request, socket.getOutputStream());
        chain.doFilter(request, res);
    }

    public RequestDispatcher getRequestDispatcher(String path) {
        PathData pathData = PathParser.parse(path);
        ServletData servletData = getServletData(pathData.path);

        if (servletData.servletName == null) {
            return null;
        }

        HttpServlet httpServlet = servlets.get(servletData.servletName);
        assert httpServlet != null;
        return new ServletRequestDispatcher(httpServlet, servletData, pathData);
    }


    private FilterChain getFilterChain(String path) {
        FilterChain chain = new FilterChain();
        for (FilterMapping mapping : filterMappings) {
            String urlPattern = mapping.pattern;
            int i = urlPattern.indexOf("*");
            if (i == -1) {
                if (!path.equals(urlPattern)) {
                    continue;
                }

                String filterName = mapping.name;
                HttpFilter filter = filters.get(filterName);
                if (filter != null) {
                    chain.addFilter(filter);
                } else {
                    //todo log error
                }

                continue;
            }

            if (i > 0 && urlPattern.charAt(i - 1) == '/') {
                String fPath = urlPattern.substring(0, i - 1);
                if (path.startsWith(fPath)) {
                    String filterName = mapping.name;
                    HttpFilter filter = filters.get(filterName);
                    if (filter != null) {
                        chain.addFilter(filter);
                    } else {
                        //todo log error
                    }
                }
            } else {
                //todo maybe throw exception
            }
        }

        return chain;
    }

    private ServletData getServletData(String path) {
        ServletData data = new ServletData();
        for (Map.Entry<String, String> entry : servletMapping.entrySet()) {
            String urlPattern = entry.getValue();
            int i = urlPattern.indexOf("*");
            if (i == -1) {
                if (!path.equals(urlPattern)) {
                    continue;
                }

                data.servletName = entry.getKey();
                data.servletPath = urlPattern;
                break;
            }


            if (i > 0 && urlPattern.charAt(i - 1) == '/') {
                String sp = urlPattern.substring(0, i - 1);
                if (path.startsWith(sp)) {
                    data.servletName = entry.getKey();
                    data.servletPath = sp;
                    data.pathInfo = path.substring(i - 1);
                    if (data.pathInfo.isEmpty()) {
                        data.pathInfo = null;
                    }

                    break;
                }
            } else {
                //todo maybe throw exception
            }
        }

        if (data.servletName == null) {
            data.pathInfo = path;
        }

        return data;
    }

    private void initObjects(ClassLoader cl, List<NameClassPair> list) {
        for (NameClassPair pair : list) {
            String name = pair.name;
            String className = pair.className;

            if (name == null || className == null) {
                throw new IllegalArgumentException(
                        "Null value in web.xml! For name " + name + " class " + className);
                //todo log instead of throw
            }


                try {
                    Class<?> clazz = cl.loadClass(className);
                    Constructor<?> constructor = clazz.getConstructor();
                    Object instance = constructor.newInstance();

                    if (instance instanceof HttpServlet servlet) {
                        servlets.put(name, servlet);
                        servlet.setServletContext(this.servletContext);
                    } else if (instance instanceof HttpFilter filter) {
                        filters.put(name, filter);
                        filter.setServletContext(this.servletContext);
                    }
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException("Class not found with name: " + className, e);
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException("No public empty constructor for: " + name, e);
                } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                    throw new RuntimeException("Couldn't make instance of class: " + name, e);
                }

        }
    }

    private List<NameClassPair> parseServlets(NodeList nodes) {
        List<NameClassPair> servletsData = new ArrayList<>();
        int count = nodes.getLength();
        for (int i = 0; i < count; i++) {
            Element elem = (Element) nodes.item(i);

            NameClassPair pair = new NameClassPair();
            pair.name = getTextFromTag(elem, "servlet-name");
            pair.className = getTextFromTag(elem, "servlet-class");
            servletsData.add(pair);
        }

        return servletsData;
    }

    private static String getTextFromTag(Element elem, String tagName) {
        Node cn = elem.getElementsByTagName(tagName).item(0);
        return cn == null ? null : cn.getTextContent();
    }

    private List<NameClassPair> parseFilters(NodeList nodes) {
        List<NameClassPair> filtersData = new ArrayList<>();
        int count = nodes.getLength();
        for (int i = 0; i < count; i++) {
            Element elem = (Element) nodes.item(i);

            NameClassPair pair = new NameClassPair();
            pair.name = getTextFromTag(elem, "filter-name");
            pair.className = getTextFromTag(elem, "filter-class");
            filtersData.add(pair);
        }

        return filtersData;
    }

    private void parseFilterMappings(NodeList nodes) {
        int count = nodes.getLength();
        for (int i = 0; i < count; i++) {
            Element elem = (Element) nodes.item(i);

            String filterName = getTextFromTag(elem, "filter-name");
            if (filterName == null) {
                throw new IllegalArgumentException("Filter-name not found or null in filter-mapper in web.xml!");
            }

            String urlPattern = getTextFromTag(elem, "url-pattern");
            if (urlPattern != null) {
                FilterMapping pair = new FilterMapping(filterName, urlPattern);
                filterMappings.add(pair);
                continue;
            }

            String servletName = getTextFromTag(elem, "servlet-name");
            if (servletName == null) {
                //todo log error
                continue;
            }

            String pattern = servletMapping.get(servletName);
            if (pattern == null) {
                //todo log error
                continue;
            }

            FilterMapping pair = new FilterMapping(filterName, pattern);
            filterMappings.add(pair);
        }
    }

    private void parseServletMappings(NodeList nodes) {
        int count = nodes.getLength();
        for (int i = 0; i < count; i++) {
            Element elem = (Element) nodes.item(i);

            String servletName = getTextFromTag(elem, "servlet-name");
            if (servletName == null) {
                throw new IllegalArgumentException("Servlet-name not found or null in servlet-mapping in web.xml!");
            }

            String urlPattern = getTextFromTag(elem, "url-pattern");
            if (urlPattern == null) {
                throw new IllegalArgumentException("Service url-pattern not found or null in servlet-mapping in web.xml!");
            }

            if (urlPattern.length() > 1 && urlPattern.endsWith("/")) {
                urlPattern = urlPattern.substring(0, urlPattern.length() - 1);
            }

            servletMapping.put(servletName, urlPattern);
        }
    }
}
