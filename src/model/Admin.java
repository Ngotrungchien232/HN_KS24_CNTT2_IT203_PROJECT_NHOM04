// kế thừa lớp User nhưng đại diện cho quản trị viên

package model;

import org.mindrot.jbcrypt.BCrypt;

public class Admin extends User {

    public Admin(int id, String username, String password,
                 String email, String phone) {
        super(id, username, password, email, phone, "ADMIN");
    }

    // tạo một tài khoản admin mặc định
    public static Admin getDefaultAdmin() {
        return new Admin(1, "admin","admin123" , "admin123@gmail.com", "0123456789");
    }



}
