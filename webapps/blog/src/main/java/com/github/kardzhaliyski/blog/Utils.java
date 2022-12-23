package com.github.kardzhaliyski.blog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.github.kardzhaliyski.tomcatclone.http.HttpServletResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

public class Utils {
    public static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final Logger log = LogManager.getLogger("FileLogger");

    public static void writeErrorAsJson(HttpServletResponse resp, int statusCode, String msg) throws IOException {
        String json = gson.toJson(msg);
        resp.setContentType("application/json");
        resp.setStatus(statusCode);
        PrintWriter writer = resp.getWriter();
        writer.println(json);
        writer.flush();
        log.info("{} : {}", statusCode, msg);
    }

    public static void writeAsJson(HttpServletResponse resp, int status, Object obj) throws IOException {
        resp.setStatus(status);
        writeAsJson(resp, obj);
    }

    public static void writeAsJson(HttpServletResponse resp, Object obj) throws IOException {
        String json = gson.toJson(obj);
        int byteCount = json.length();
        resp.setContentType("application/json");
        resp.addHeader("Content-Length", String.valueOf(byteCount));
        PrintWriter writer = resp.getWriter();
        writer.println(json);
        writer.flush();
    }

    public static void writeHttpResponseAsJson(OutputStream outputStream, int status, String obj) {
        PrintWriter printWriter = new PrintWriter(outputStream);
        printWriter.println("HTTP/1.1 " + status);
        printWriter.println("");
        printWriter.println(gson.toJson(obj));
        printWriter.flush();
    }
}
