package service;

import dao.UserDAO;
import model.User;
import util.PasswordUtil;
import util.ValidationUtil;

public class AuthService {

    private UserDAO userDAO = new UserDAO();

    public User login(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            System.out.println("Ten dang nhap khong duoc de trong!");
            return null;
        }
        if (password == null || password.trim().isEmpty()) {
            System.out.println("Mat khau khong duoc de trong!");
            return null;
        }
        User user = userDAO.findByUsername(username.trim());
        if (user == null) {
            System.out.println("Tai khoan khong ton tai!");
            return null;
        }
        if (!PasswordUtil.checkPassword(password, user.getPassword())) {
            System.out.println("Mat khau khong dung!");
            return null;
        }
        return user;
    }

    public boolean register(String username, String password,
                            String email, String phone, String address) {
        if (username == null || username.trim().isEmpty()) {
            System.out.println("Ten dang nhap khong duoc de trong!");
            return false;
        }
        if (password == null || password.length() < 6) {
            System.out.println("Mat khau phai co it nhat 6 ky tu!");
            return false;
        }
        if (!ValidationUtil.isValidPhone(phone)) {
            System.out.println("So dien thoai khong dung dinh dang!");
            return false;
        }
        if (userDAO.findByUsername(username.trim()) != null) {
            System.out.println("Ten dang nhap da ton tai!");
            return false;
        }
        String hashedPassword = PasswordUtil.hashPassword(password);
        return userDAO.register(
                username.trim(), hashedPassword,
                email.trim(), phone.trim(),
                address.trim(), "CUSTOMER"
        );
    }
}
