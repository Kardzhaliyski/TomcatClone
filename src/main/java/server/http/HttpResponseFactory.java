package server.http;

import server.utils.StatusCode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class HttpResponseFactory {
    public static HttpResponse notFound(String protocol) {
        HttpResponse r = new HttpResponse(protocol, StatusCode.NOT_FOUND);
        File file = Path.of("src/main/resources/static/NotFound.html").toFile();
        r.addBody(file);
        return r;
    }

    public static HttpResponse methodNotAllowed(String protocol) {
        HttpResponse r = new HttpResponse(protocol, StatusCode.METHOD_NOT_ALLOWED);
        r.addBody("Method not allowed");
        r.addHeader("Content-Type", "text/html");
        return r;
    }

    public static HttpResponse file(String protocol, File file) throws IOException {
        HttpResponse r = new HttpResponse(protocol, StatusCode.OK);
        r.addBody(file);
        return r;
    }

    public static HttpResponse stringResponse(String protocol, String response) {
        HttpResponse r = new HttpResponse(protocol, StatusCode.OK);
        r.addBody(response);
        r.addHeader("Content-Type", "text/html");
        return r;
    }

    public static HttpResponse redirect(String protocol, String s) {
        HttpResponse r = new HttpResponse(protocol, StatusCode.MOVED_PERMANENTLY);
        r.addHeader("Location", s);
        r.addHeader("Content-Type", "text/html");
        return r;
    }
}