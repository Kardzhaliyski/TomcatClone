package servlet;

import static com.github.kardzhaliyski.tomcatclone.http.HttpServletResponse.*;
import static servlet.Utils.*;

import com.github.kardzhaliyski.tomcatclone.http.HttpServlet;
import com.github.kardzhaliyski.tomcatclone.http.HttpServletRequest;
import com.github.kardzhaliyski.tomcatclone.http.HttpServletResponse;
import org.apache.commons.codec.digest.DigestUtils;
import dao.UsersDao;
import model.User;
import model.dto.RegisterUserDTO;

import java.io.IOException;
import java.util.Random;

public class RegisterServlet extends HttpServlet {

    private final UsersDao dao = new UsersDao();
    private final Random random = new Random();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws  IOException {
        resp.sendRedirect("/register/");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        RegisterUserDTO dto = gson.fromJson(req.getReader(), RegisterUserDTO.class);
        System.out.println();

        if (!dto.isValid()) {
            writeErrorAsJson(resp, SC_BAD_REQUEST, "Invalid input");

            return;
        }

        User userFromDB = dao.getUserByUsername(dto.uname);
        if(userFromDB != null) {
            writeErrorAsJson(resp, SC_BAD_REQUEST, "User with this username already exists!");
            return;
        }

        String salt = String.valueOf(random.nextInt(Integer.MAX_VALUE));
        String hashedPassword = DigestUtils.sha1Hex(salt + dto.psw);
        User user = new User(dto.uname, dto.fName, dto.lName, dto.email, hashedPassword, salt);
        dao.addUser(user);
        writeAsJson(resp, SC_CREATED, "User created");
    }
}
