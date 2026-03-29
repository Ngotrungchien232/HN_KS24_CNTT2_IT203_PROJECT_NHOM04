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

    /** @return null neu hop le, nguoc lai thong bao loi */
    public String validateUsernameForRegister(String username) {
        if (username == null || username.trim().isEmpty()) {
            return "Ten dang nhap khong duoc de trong!";
        }
        if (userDAO.findByUsername(username.trim()) != null) {
            return "Ten dang nhap da ton tai!";
        }
        return null;
    }

    /** @return null neu hop le, nguoc lai thong bao loi */
    public String validatePasswordForRegister(String password) {
        if (password == null || password.length() < 6) {
            return "Mat khau phai co it nhat 6 ky tu!";
        }
        return null;
    }

    /** @return null neu hop le, nguoc lai thong bao loi */
    public String validateEmailForRegister(String email) {
        if (email == null || email.trim().isEmpty()) {
            return "Email khong duoc de trong!";
        }
        if (!ValidationUtil.isValidEmail(email.trim())) {
            return "Email khong dung dinh dang!";
        }
        if (userDAO.findByEmail(email.trim()) != null) {
            return "Email da duoc su dung!";
        }
        return null;
    }

    /** @return null neu hop le, nguoc lai thong bao loi */
    public String validatePhoneForRegister(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return "So dien thoai khong duoc de trong!";
        }
        if (!ValidationUtil.isValidPhone(phone.trim())) {
            return "So dien thoai khong dung dinh dang! (10 so, bat dau bang 0)";
        }
        if (userDAO.findByPhone(phone.trim()) != null) {
            return "So dien thoai da duoc su dung!";
        }
        return null;
    }

    /** @return null neu hop le, nguoc lai thong bao loi */
    public String validateAddressForRegister(String address) {
        if (address == null || address.trim().isEmpty()) {
            return "Dia chi giao hang khong duoc de trong!";
        }
        return null;
    }

    public boolean register(String username, String password,
                            String email, String phone, String address) {
        if (validateUsernameForRegister(username) != null) {
            return false;
        }
        if (validatePasswordForRegister(password) != null) {
            return false;
        }
        if (validateEmailForRegister(email) != null) {
            return false;
        }
        if (validatePhoneForRegister(phone) != null) {
            return false;
        }
        if (validateAddressForRegister(address) != null) {
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
