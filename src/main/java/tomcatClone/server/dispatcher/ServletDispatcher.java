package server.dispatcher;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import server.http.HttpRequest;
import server.http.servlet.HttpFilter;
import server.http.servlet.HttpServlet;
import server.http.servlet.HttpServletRequest;
import server.http.servlet.HttpServletResponse;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class ServletDispatcher {

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        ServletDispatcher servletDispatcher = new ServletDispatcher("src/main/java/webapps/blog/web.xml");
        //todo remove
    }


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

    private static class NameClassPair {
        String name;
        String className;
    }

    private final Map<String, HttpFilter> filters = new HashMap<>();
    private final Map<String, HttpServlet> servlets = new HashMap<>();
    private final Set<FilterMapping> filterMappingSet = new LinkedHashSet<>();
    private final Map<String, String> servletMapping = new LinkedHashMap<>();

    public ServletDispatcher(String webXmlPath) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = documentBuilder.parse(webXmlPath); //todo maybe change

        NodeList filterNodes = doc.getElementsByTagName("filter");
        List<NameClassPair> filtersData = parseFilters(filterNodes);
        initObjects(filtersData, HttpFilter.class);

        NodeList filterMappingNotes = doc.getElementsByTagName("filter-mapping");
        parseFilterMappings(filterMappingNotes);

        NodeList servletNodes = doc.getElementsByTagName("servlet");
        List<NameClassPair> servletsData = parseServlets(servletNodes);
        initObjects(servletsData, HttpServlet.class);

        NodeList servletMappingNotes = doc.getElementsByTagName("servlet-mapping");
        parseServletMappings(servletMappingNotes);
    }

    public void dispatch(HttpRequest request, HttpServletResponse response) throws IOException {
        String path = request.path;
        for (Map.Entry<String, String> kvp : servletMapping.entrySet()) {
            String servletPath = kvp.getKey();
            int i = servletPath.indexOf("*");
            if (i == -1) {
                if (path.equals(servletPath)) {
                    String servletName = kvp.getValue();
                    HttpServlet httpServlet = servlets.get(servletName);
                    httpServlet.service(new HttpServletRequest(request, servletPath), response);
                    return;
                }
            }
        }
    }

    private void initObjects(List<NameClassPair> list, Class<?> type) {
        for (NameClassPair pair : list) {
            String name = pair.name;
            String className = pair.className;

            if (name == null || className == null) {
                throw new IllegalArgumentException(
                        "Null value in web.xml! For name " + name + " class " + className);
                //todo log instead of throw
            }

            try {
                Class<?> clazz = Class.forName(className);
                Constructor<?> constructor = clazz.getConstructor();
                Object instance = constructor.newInstance();

                if (type.equals(HttpServlet.class)) {
                    servlets.put(name, (HttpServlet) instance);
                } else if (type.equals(HttpFilter.class)) {
                    filters.put(name, (HttpFilter) instance);
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
            String servletName = getTextFromTag(elem, "servlet-name");

            FilterMapping fm = new FilterMapping(filterName, urlPattern, servletName);
            filterMappingSet.add(fm);
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

            servletMapping.put(urlPattern, servletName);
        }
    }
}
