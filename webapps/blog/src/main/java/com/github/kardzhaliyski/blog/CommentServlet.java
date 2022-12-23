package com.github.kardzhaliyski.blog;

import static com.github.kardzhaliyski.blog.Utils.*;

import com.github.kardzhaliyski.blog.model.Comment;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.github.kardzhaliyski.tomcatclone.http.HttpServlet;
import com.github.kardzhaliyski.tomcatclone.http.HttpServletRequest;
import com.github.kardzhaliyski.tomcatclone.http.HttpServletResponse;
import com.github.kardzhaliyski.blog.dao.Dao;

import java.io.IOException;

public class CommentServlet extends HttpServlet {

    Dao dao;
    Gson gson;

    @Override
    public void init() {
        dao = new Dao();
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String postId = req.getParameter("postId");
        if (postId == null) {
            Comment[] comments = dao.getAllComments();
            writeAsJson(resp, comments);
            return;
        }

        if (!isPositiveNumber(postId)) {
            writeAsJson(resp, new Object[0]);
            return;
        }

        int id = Integer.parseInt(postId);
        Comment[] comments = dao.getAllCommentsForPost(id);
        writeAsJson(resp, comments);

    }

    private boolean isPositiveNumber(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }

        return true;
    }

//    private void writeAsJson(HttpServletResponse resp, Object obj) throws IOException {
//        String json = gson.toJson(obj);
//        resp.setContentType("application/json");
//        PrintWriter writer = resp.getWriter();
//        writer.println(json);
//        writer.flush();
//    }
}
