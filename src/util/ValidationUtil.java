package util;

public class ValidationUtil {

    public static boolean isValidPhone(String phone) {
        if (phone == null) {
            return false;
        }
        return phone.matches("^0[0-9]{9}$");
    }

    public static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        return email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    }
}
