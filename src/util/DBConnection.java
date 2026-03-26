package util;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    // Thông tin kết nối — chỉnh lại password cho đúng với máy bạn
    private static final String URL      = "jdbc:mysql://localhost:3306/Phone_Store";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "14102005";

    private static Connection connection = null;

    // Singleton: chỉ tạo 1 kết nối duy nhất trong suốt chương trình
    public static Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println(" Kết nối database thành công!");
            } catch (Exception e) {
                System.out.println(" Kết nối database thất bại: " + e.getMessage());
            }
        }
        return connection;
    }

    // Đóng kết nối khi thoát chương trình
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println(" Đã đóng kết nối database!");
            } catch (Exception e) {
                System.out.println(" Lỗi khi đóng kết nối: " + e.getMessage());
            }
        }
    }
}