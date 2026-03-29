package dao;

import model.Admin;
import model.Customer;
import model.User;
import util.DBConnection;

import java.sql.*;

public class UserDAO {

    private Connection conn = DBConnection.getConnection();

    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            System.out.println("Loi khi tim user: " + e.getMessage());
        }
        return null;
    }

    public User findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            System.out.println("Loi khi tim email: " + e.getMessage());
        }
        return null;
    }

    public User findByPhone(String phone) {
        String sql = "SELECT * FROM users WHERE phone = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, phone);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            System.out.println("Loi khi tim so dien thoai: " + e.getMessage());
        }
        return null;
    }

    public boolean register(String username, String password,
                            String email, String phone,
                            String address, String role) {
        String sql = "INSERT INTO users " +
                "(username, password, email, phone, address, role) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, email);
            ps.setString(4, phone);
            ps.setString(5, address);
            ps.setString(6, role);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Loi khi dang ky: " + e.getMessage());
            return false;
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        int    id       = rs.getInt("id");
        String username = rs.getString("username");
        String password = rs.getString("password");
        String email    = rs.getString("email");
        String phone    = rs.getString("phone");
        String address  = rs.getString("address");
        String role     = rs.getString("role");

        if (role.equals("ADMIN")) {
            return new Admin(id, username, password, email, phone);
        } else {
            return new Customer(id, username, password, email, phone, address);
        }
    }
}
