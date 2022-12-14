package server.dispatcher;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import server.http.servlet.HttpFilter;
import server.http.servlet.HttpServlet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class ServletDispatcher {
    private class FilterMapping {
        String filterName;
        String urlPath;
        String servletName;

        public FilterMapping(String filterName, String urlPath, String servletName) {
            this.filterName = filterName;
            this.urlPath = urlPath;
            this.servletName = servletName;
        }
    }

    private final Map<String, HttpFilter> filters = new HashMap<>();
    private final Map<String, HttpServlet> servlets = new HashMap<>();
    private final Set<FilterMapping> filterMappingSet = new LinkedHashSet<>();
    private final Map<String, String> servletMapping = new LinkedHashMap<>();

    public ServletDispatcher(String webXmlPath) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = documentBuilder.parse(webXmlPath);

        NodeList filterNodes = doc.getElementsByTagName("filter");
        parseFilters(filterNodes);
        NodeList filterMappingNotes = doc.getElementsByTagName("filter-mapping");
        parseFilterMappings(filterMappingNotes);
        NodeList servletNodes = doc.getElementsByTagName("servlet");
        parseServlets(servletNodes);
        NodeList servletMappingNotes = doc.getElementsByTagName("servlet-mapping");
        parseServletMappings(servletMappingNotes);
    }

    private void parseServlets(NodeList nodes) {
        int count = nodes.getLength();
        for (int i = 0; i < count; i++) {
            Element elem = (Element) nodes.item(i);

            Node nn = elem.getElementsByTagName("servlet-name").item(0);
            String servletName = nn == null ? null : nn.getTextContent();
            Node cn = elem.getElementsByTagName("servlet-class").item(0);
            String servletClass = cn == null ? null : cn.getTextContent();

            if (servletName == null || servletClass == null) {
                throw new IllegalArgumentException(
                        "Null value in servlet in web.xml! For servlet-name " + servletName + " servlet-class " + servletClass);
            }

            try {
                Class<?> clazz = Class.forName(servletClass);
                Constructor<?> constructor = clazz.getConstructor();
                HttpServlet servlet = (HttpServlet) constructor.newInstance();
                servlets.put(servletName, servlet);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Class not found with class-name: " + servletClass, e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("No public empty constructor for servlet: " + servletName, e);
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                throw new RuntimeException("Couldn't make instance of class: " + servletName, e);
            }
        }
    }

    private void parseFilters(NodeList nodes) {
        int count = nodes.getLength();
        for (int i = 0; i < count; i++) {
            Element elem = (Element) nodes.item(i);

            Node nn = elem.getElementsByTagName("filter-name").item(0);
            String filterName = nn == null ? null : nn.getTextContent();
            Node cn = elem.getElementsByTagName("filter-class").item(0);
            String filterClass = cn == null ? null : cn.getTextContent();

            if (filterName == null || filterClass == null) {
                throw new IllegalArgumentException(
                        "Null value in filter in web.xml! For filter-name " + filterName + " filter-class " + filterClass);
            }

            try {
                Class<?> clazz = Class.forName(filterClass);
                Constructor<?> constructor = clazz.getConstructor();
                HttpFilter filter = (HttpFilter) constructor.newInstance();
                filters.put(filterName, filter);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Class not found with class-name: " + filterClass, e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("No public empty constructor for filter: " + filterName, e);
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                throw new RuntimeException("Couldn't make instance of class: " + filterName, e);
            }
        }
    }

    private void parseFilterMappings(NodeList nodes) {
        int count = nodes.getLength();
        for (int i = 0; i < count; i++) {
            Element elem =(Element) nodes.item(i);

            Node nn = elem.getElementsByTagName("filter-name").item(0);
            String filterName = nn == null ? null : nn.getTextContent();
            if(filterName == null) {
                throw new IllegalArgumentException("Filter-name not found or null in filter-mapper in web.xml!");
            }

            Node un = elem.getElementsByTagName("url-pattern").item(0);
            String urlPattern = un == null ? null : un.getTextContent();
            Node sn = elem.getElementsByTagName("servlet-name").item(0);
            String servletName = sn == null ? null : sn.getTextContent();

            FilterMapping fm = new FilterMapping(filterName, urlPattern, servletName);
            filterMappingSet.add(fm);
        }
    }

    private void parseServletMappings(NodeList nodes) {
        int count = nodes.getLength();
        for (int i = 0; i < count; i++) {
            Element elem =(Element) nodes.item(i);

            Node nn = elem.getElementsByTagName("servlet-name").item(0);
            String servletName = nn == null ? null : nn.getTextContent();
            if(servletName == null) {
                throw new IllegalArgumentException("Servlet-name not found or null in servlet-mapping in web.xml!");
            }

            Node un = elem.getElementsByTagName("url-pattern").item(0);
            String urlPattern = un == null ? null : un.getTextContent();

            servletMapping.put(urlPattern, servletName);
        }
    }
}
