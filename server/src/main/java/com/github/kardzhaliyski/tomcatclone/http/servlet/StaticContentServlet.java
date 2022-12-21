package com.github.kardzhaliyski.tomcatclone.http.servlet;

import com.github.kardzhaliyski.tomcatclone.ServletContext;
import com.github.kardzhaliyski.tomcatclone.http.HttpServlet;
import com.github.kardzhaliyski.tomcatclone.http.HttpServletRequest;
import com.github.kardzhaliyski.tomcatclone.http.HttpServletResponse;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

public class StaticContentServlet extends HttpServlet {
    private byte[] body;
    private File bodyFile = null;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        handle(req);
        if(body == null && bodyFile == null) {
            //todo return 404
            return;
        }

        if (bodyFile != null) {
            String contentType = getContentType();
            resp.addHeader("Content-Type", contentType);
//            if (serverAcceptGzip) {
//                File compressedFile = Gzip.getCompressedVersion(bodyFile, contentType);
//                compressed = compressedFile != bodyFile;
//                bodyFile = compressedFile;
//            }
        }

        addHeaders(resp);
        sendBody(resp.getOutputStream());
    }

    public void handle(HttpServletRequest request) throws IOException {

        Path root = getServletContext().root;
        String pathInfo = request.getPathInfo();
        if(pathInfo.startsWith("/")) {
            pathInfo = pathInfo.substring(1);
        }
        Path path = root.resolve(pathInfo);
        File dest = path.toFile();

        if (!dest.exists()) {
            return;
        }

        if (dest.isDirectory()) {
            File indexHtmlFile = path.resolve("index.html").toFile();
            if (indexHtmlFile.exists()) {
                bodyFile = indexHtmlFile;
                return;
            }

            handleDirectory(request, dest);
            return;
        }

        bodyFile = dest;
    }

    private void handleDirectory( HttpServletRequest request, File dest) throws IOException {
//        if (!request.path.isBlank() && !request.path.endsWith("/")) {
//            return HttpResponseFactory.redirect(request.protocol, "/" + request.path + "/");
//        }

        ServletContext context = getServletContext();
        if (context.showDirectoryContent) {
            File[] files = dest.listFiles();
            body = Arrays.stream(files)
                    .map(f -> String.format("<a href=\"%s/\">%s</a>",
                            f.getName(), f.getName()))
                    .collect(Collectors.joining("<br/>")).getBytes();
        }
    }

    private String getContentType() {
        try {
            return Files.probeContentType(bodyFile.toPath());
        } catch (IOException e) {
            System.out.println("Error while getting Content-Type of: " + bodyFile.toPath());
            return "";
        }
    }

    private void addHeaders(HttpServletResponse resp) {
        resp.addHeader("Date", LocalDateTime.now().toString());
//        headers.put("Date", LocalDateTime.now().toString());

        Instant lastMod = bodyFile != null ?
                Instant.ofEpochMilli(bodyFile.lastModified()) :
                Instant.now();
        resp.addHeader("Last-Modified", lastMod.toString());

        long bodyLength = bodyFile != null ?
                bodyFile.length() :
                body.length;
        resp.addHeader("Content-Length", String.valueOf(bodyLength));

//        if (compressed) { //todo
//            headers.put("Content-Encoding", "gzip");
//        }
    }

    private void sendBody(OutputStream out) throws IOException {
        if (bodyFile != null) {
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(bodyFile));
            byte[] buff = new byte[4096];
            int n;
            while ((n = in.read(buff)) != -1) {
                out.write(buff, 0, n);
                out.flush();
            }
        } else {
            out.write(body);
        }

        out.flush();
    }
}
