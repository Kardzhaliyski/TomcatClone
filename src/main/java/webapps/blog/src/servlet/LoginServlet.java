package webapps.blog.src.servlet;

import static server.http.servlet.HttpServletResponse.*;
import static webapps.blog.src.servlet.Utils.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import server.http.servlet.HttpServlet;
import server.http.servlet.HttpServletRequest;
import server.http.servlet.HttpServletResponse;
import webapps.blog.src.dao.UsersDao;
import webapps.blog.src.model.User;
import webapps.blog.src.model.dto.LoginUserDTO;
import org.apache.commons.codec.digest.DigestUtils;
import webapps.blog.src.service.AuthenticationService;

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
