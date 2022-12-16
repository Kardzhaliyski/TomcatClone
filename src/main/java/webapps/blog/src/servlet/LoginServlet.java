package servlet;

import static server.http.HttpServletResponse.*;
import static servlet.Utils.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import server.http.HttpServlet;
import server.http.HttpServletRequest;
import server.http.HttpServletResponse;
import org.apache.commons.codec.digest.DigestUtils;
import dao.UsersDao;
import model.User;
import model.dto.LoginUserDTO;
import service.AuthenticationService;

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
