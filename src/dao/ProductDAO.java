package dao;


import model.Product;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    private Connection conn = DBConnection.getConnection();

    // Lấy toàn bộ sản phẩm
    public List<Product> getAll() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println(" Lỗi khi lấy sản phẩm: " + e.getMessage());
        }
        return list;
    }

    // Tìm kiếm sản phẩm theo tên (tương đối)
    public List<Product> searchByName(String keyword) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE name LIKE ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println(" Lỗi khi tìm sản phẩm: " + e.getMessage());
        }
        return list;
    }

    // Tìm sản phẩm theo id
    public Product findById(int id) {
        String sql = "SELECT * FROM products WHERE id = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            System.out.println(" Lỗi khi tìm sản phẩm: " + e.getMessage());
        }
        return null;
    }

    // Thêm sản phẩm mới
    public boolean add(Product product) {
        String sql = "INSERT INTO products " +
                "(category_id, name, brand, storage, color, price, stock, description) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1,    product.getCategoryId());
            ps.setString(2, product.getName());
            ps.setString(3, product.getBrand());
            ps.setString(4, product.getStorage());
            ps.setString(5, product.getColor());
            ps.setDouble(6, product.getPrice());
            ps.setInt(7,    product.getStock());
            ps.setString(8, product.getDescription());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println(" Lỗi khi thêm sản phẩm: " + e.getMessage());
            return false;
        }
    }

    // Cập nhật sản phẩm
    public boolean update(Product product) {
        String sql = "UPDATE products SET category_id=?, name=?, brand=?, " +
                "storage=?, color=?, price=?, stock=?, description=? " +
                "WHERE id=?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1,    product.getCategoryId());
            ps.setString(2, product.getName());
            ps.setString(3, product.getBrand());
            ps.setString(4, product.getStorage());
            ps.setString(5, product.getColor());
            ps.setDouble(6, product.getPrice());
            ps.setInt(7,    product.getStock());
            ps.setString(8, product.getDescription());
            ps.setInt(9,    product.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println(" Lỗi khi cập nhật sản phẩm: " + e.getMessage());
            return false;
        }
    }

    // Xóa sản phẩm
    public boolean delete(int id) {
        String sql = "DELETE FROM products WHERE id = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println(" Lỗi khi xóa sản phẩm: " + e.getMessage());
            return false;
        }
    }

    // Map ResultSet sang Product object — tránh lặp code
    private Product mapRow(ResultSet rs) throws SQLException {
        return new Product(
                rs.getInt("id"),
                rs.getInt("category_id"),
                rs.getString("name"),
                rs.getString("brand"),
                rs.getString("storage"),
                rs.getString("color"),
                rs.getDouble("price"),
                rs.getInt("stock"),
                rs.getString("description")
        );
    }
}
