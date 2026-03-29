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
            System.out.println("Loi khi lay san pham: " + e.getMessage());
        }
        return list;
    }

    // Lấy sản phẩm còn hàng — dùng cho Customer
    public List<Product> getAvailable() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE stock > 0";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Loi khi lay san pham con hang: " + e.getMessage());
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
            System.out.println("Loi khi tim san pham: " + e.getMessage());
        }
        return list;
    }

    // Lọc sản phẩm theo hãng
    public List<Product> filterByBrand(String brand) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE brand = ? AND stock > 0";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, brand);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Loi khi loc san pham theo hang: " + e.getMessage());
        }
        return list;
    }

    /**
     * Danh sach hang (brand) co san pham con hang — dung de hien thi goi y loc.
     */
    public List<String> getDistinctBrands() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT DISTINCT brand FROM products WHERE stock > 0 ORDER BY brand";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(rs.getString("brand"));
            }
        } catch (SQLException e) {
            System.out.println("Loi khi lay danh sach hang: " + e.getMessage());
        }
        return list;
    }

    /**
     * San pham con hang, loc theo hang va/hoac khoang gia.
     * brand == null hoac rong: khong loc hang.
     * minPrice / maxPrice == null: khong gioi han phia do.
     */
    public List<Product> findAvailableWithFilters(String brand, Double minPrice, Double maxPrice) {
        List<Product> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM products WHERE stock > 0");
        List<Object> params = new ArrayList<>();

        if (brand != null && !brand.trim().isEmpty()) {
            sql.append(" AND brand = ?");
            params.add(brand.trim());
        }
        if (minPrice != null) {
            sql.append(" AND price >= ?");
            params.add(minPrice);
        }
        if (maxPrice != null) {
            sql.append(" AND price <= ?");
            params.add(maxPrice);
        }
        sql.append(" ORDER BY id");

        try {
            PreparedStatement ps = conn.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p instanceof Double) {
                    ps.setDouble(i + 1, (Double) p);
                } else {
                    ps.setString(i + 1, p.toString());
                }
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Loi khi loc san pham: " + e.getMessage());
        }
        return list;
    }

    // Lọc sản phẩm theo khoảng giá
    public List<Product> filterByPrice(double minPrice, double maxPrice) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE price BETWEEN ? AND ? AND stock > 0";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setDouble(1, minPrice);
            ps.setDouble(2, maxPrice);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Loi khi loc san pham theo gia: " + e.getMessage());
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
            System.out.println("Loi khi tim san pham: " + e.getMessage());
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
            System.out.println("Loi khi them san pham: " + e.getMessage());
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
            System.out.println("Loi khi cap nhat san pham: " + e.getMessage());
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
            System.out.println("Loi khi xoa san pham: " + e.getMessage());
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