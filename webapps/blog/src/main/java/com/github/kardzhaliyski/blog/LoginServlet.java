package com.github.kardzhaliyski.blog;

import static com.github.kardzhaliyski.tomcatclone.http.HttpServletResponse.*;
import static com.github.kardzhaliyski.blog.Utils.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.github.kardzhaliyski.tomcatclone.http.HttpServlet;
import com.github.kardzhaliyski.tomcatclone.http.HttpServletRequest;
import com.github.kardzhaliyski.tomcatclone.http.HttpServletResponse;
import org.apache.commons.codec.digest.DigestUtils;
import com.github.kardzhaliyski.blog.dao.UsersDao;
import com.github.kardzhaliyski.blog.model.User;
import com.github.kardzhaliyski.blog.model.dto.LoginUserDTO;
import com.github.kardzhaliyski.blog.service.AuthenticationService;

import java.io.IOException;

public class LoginServlet extends HttpServlet {

    UsersDao dao;
    Gson gson;
    AuthenticationService authService;

    @Override
    public void init() {
        dao = new UsersDao();
        gson = new GsonBuilder().setPrettyPrinting().create();
        authService = AuthenticationService.getInstance();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        LoginUserDTO dto = gson.fromJson(req.getReader(), LoginUserDTO.class);

        if (dto == null || dto.uname == null || dto.psw == null) {
            writeErrorAsJson(resp, SC_BAD_REQUEST, "Invalid credentials");
            return;
        }

        User user = dao.getUserByUsername(dto.uname);
        String salt = user.salt;
        String hashedPassword = DigestUtils.sha1Hex(salt + dto.psw);
        if(user.password.equals(hashedPassword)) {
            String token = authService.createNewToken(dto.uname);
            resp.addHeader("Authorization", "Bearer " + token);
            writeAsJson(resp, "User logged in");
        } else {
            writeErrorAsJson(resp, SC_UNAUTHORIZED, "Bad credentials");
        }
    }
}
