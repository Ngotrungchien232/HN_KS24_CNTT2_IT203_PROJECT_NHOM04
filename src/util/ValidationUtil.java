package util;

public class ValidationUtil {

    public static boolean isValidPhone(String phone) {
        if (phone == null) {
            return false;
        }
        // Kiểm tra format trước
        if (!phone.matches("^0[0-9]{9}$")) {
            return false;
        }
        // Không được tất cả 0
        if (phone.equals("0000000000")) {
            return false;
        }
        return true;
    }

    public static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        return email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    }
}
