package com.github.kardzhaliyski.tomcatclone.http;

import com.github.kardzhaliyski.tomcatclone.Server;
import com.github.kardzhaliyski.tomcatclone.utils.StatusCode;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.time.Instant;

public class HttpTask implements Runnable {
    Socket socket;
    Server server;

    public HttpTask(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            InputStream inputStream = socket.getInputStream();
            HttpRequest request = new HttpRequest(inputStream);
            log(request);
            server.dispatch(request, socket);


//            HttpResponse response;
//            if (request.method.equals("GET")) {
//                response = GETHandler.handle(server, request);
//                String ae = request.headers.get("Accept-Encoding");
//                if (ae != null && ae.contains("gzip")) {
//                    response.serverAcceptGzip = true;
//                }
//
//                if (response.statusCode == StatusCode.NOT_FOUND) {
//                    logError(request.method, request.path, response.statusCode);
//                }
//            } else {
//                response = HttpResponseFactory.methodNotAllowed(request.protocol);
//                logError(request.method, request.path, response.statusCode);
//            }
//
//
//            OutputStream outputStream = socket.getOutputStream();
//            response.send(outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void logError(String method, String path, StatusCode errorCode) {
        System.err.printf("[%s] \"%s /%s\" Error (%s): \"%s\"%n",
                Instant.now().toString(),
                method,
                path,
                errorCode.getCode(),
                errorCode.getMessage());
    }

    private void log(HttpRequest request) {
        System.out.printf("[%s] \"%s %s\" \"%s\"%n",
                Instant.now().toString(),
                request.method,
                request.path,
                request.headers.get("User-Agent"));
    }

}
