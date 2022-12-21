package model.dto;

public class RegisterUserDTO {
    public String uname;
    public String fName;
    public String lName;
    public String email;
    public String psw;
    public String pswRepeat;

    public boolean isValid() {
        if (uname == null || fName == null || lName == null ||
                email == null || psw == null || pswRepeat == null) {

            return false;
        }

        return psw.equals(pswRepeat);
    }
}
