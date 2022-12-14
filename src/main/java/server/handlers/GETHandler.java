package server.handlers;

import server.Server;
import server.http.HttpRequest;
import server.http.HttpResponse;
import server.http.HttpResponseFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Collectors;

public class GETHandler {
    public static HttpResponse handle(Server server, HttpRequest request) throws IOException {
        Path root = server.root;
        Path path = root.resolve(request.path);
        File dest = path.toFile();

        if (!dest.exists()) {
            return HttpResponseFactory.notFound(request.protocol);
        }

        if (dest.isDirectory()) {
            return handleDirectory(server, request, dest);
        }

        try {
            return HttpResponseFactory.file(request.protocol, dest);
        } catch (FileNotFoundException ignored) {
        }

        return null;
    }

    private static HttpResponse handleDirectory(Server server, HttpRequest request, File dest) throws IOException {
        if (!request.path.isBlank() && !request.path.endsWith("/")) {
            return HttpResponseFactory.redirect(request.protocol, "/" + request.path + "/");
        }

        File[] files = dest.listFiles();
        for (File file : files) {
            if (file.getName().equals("index.html")) {
                return HttpResponseFactory.file(request.protocol, file);
            }
        }

        if (server.showDirectoryContent) {
            String content = Arrays.stream(files)
                    .map(f -> String.format("<a href=\"%s/\">%s</a>",
                            f.getName(), f.getName()))
                    .collect(Collectors.joining("<br/>"));
            return HttpResponseFactory.stringResponse(request.protocol, content);
        } else {
            return HttpResponseFactory.notFound(request.protocol);
        }
    }
}
