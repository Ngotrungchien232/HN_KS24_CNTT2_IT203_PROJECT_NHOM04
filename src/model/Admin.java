// kế thừa lớp User nhưng đại diện cho quản trị viên

package model;

public class Admin extends User {

    public Admin(int id, String username, String password,
                 String email, String phone) {
        super(id, username, password, email, phone, "ADMIN");
    }
}
