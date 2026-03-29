package util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    public static boolean checkPassword(String password, String hash) {
        // BCrypt hash hợp lệ thường bắt đầu bằng $2a$, $2b$ hoặc $2y$
        if (password == null || hash == null) return false;
        if (!hash.matches("^\\$2[aby]\\$\\d\\d\\$[./A-Za-z0-9]{53}$")) return false;

        try {
            return BCrypt.checkpw(password, hash);
        } catch (Exception e) {
            return false;
        }
    }
}