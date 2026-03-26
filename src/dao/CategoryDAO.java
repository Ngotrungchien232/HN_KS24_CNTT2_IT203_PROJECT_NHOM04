package dao;

import model.Category;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {

    private Connection conn = DBConnection.getConnection();

    // Lấy toàn bộ danh mục
    public List<Category> getAll() {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM categories";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Category(
                        rs.getInt("id"),
                        rs.getString("name")
                ));
            }
        } catch (SQLException e) {
            System.out.println(" Lỗi khi lấy danh mục: " + e.getMessage());
        }
        return list;
    }

    // Thêm danh mục mới
    public boolean add(Category category) {
        String sql = "INSERT INTO categories (name) VALUES (?)";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, category.getName());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println(" Lỗi khi thêm danh mục: " + e.getMessage());
            return false;
        }
    }

    // Cập nhật danh mục
    public boolean update(Category category) {
        String sql = "UPDATE categories SET name = ? WHERE id = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, category.getName());
            ps.setInt(2, category.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println(" Lỗi khi cập nhật danh mục: " + e.getMessage());
            return false;
        }
    }

    // Xóa danh mục
    public boolean delete(int id) {
        String sql = "DELETE FROM categories WHERE id = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println(" Lỗi khi xóa danh mục: " + e.getMessage());
            return false;
        }
    }

    // Tìm danh mục theo id
    public Category findById(int id) {
        String sql = "SELECT * FROM categories WHERE id = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Category(rs.getInt("id"), rs.getString("name"));
            }
        } catch (SQLException e) {
            System.out.println(" Lỗi khi tìm danh mục: " + e.getMessage());
        }
        return null;
    }
}