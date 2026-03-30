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
     * Dieu kien loc: con hang + tuy chon hang / khoang gia / tu khoa (ten hoac hang).
     */
    private void appendAvailableFilterConditions(StringBuilder sql, List<Object> params,
                                               String brand, Double minPrice, Double maxPrice,
                                               String keyword) {
        sql.append(" WHERE stock > 0");
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
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (name LIKE ? OR brand LIKE ?)");
            String k = "%" + keyword.trim() + "%";
            params.add(k);
            params.add(k);
        }
    }

    /** 0 = ma SP; 1 = gia tang; 2 = gia giam */
    private void appendOrderBySort(StringBuilder sql, int sortMode) {
        switch (sortMode) {
            case 1:
                sql.append(" ORDER BY price ASC");
                break;
            case 2:
                sql.append(" ORDER BY price DESC");
                break;
            default:
                sql.append(" ORDER BY id ASC");
        }
    }

    /**
     * Loc san pham (admin): khong bat buoc con hang.
     */
    private void appendAdminProductConditions(StringBuilder sql, List<Object> params,
                                              String keyword, String brand, Double minPrice, Double maxPrice) {
        sql.append(" WHERE 1=1");
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (name LIKE ? OR brand LIKE ?)");
            String k = "%" + keyword.trim() + "%";
            params.add(k);
            params.add(k);
        }
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
    }

    public int countAdminProductsWithFilters(String keyword, String brand, Double minPrice, Double maxPrice) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM products");
        List<Object> params = new ArrayList<>();
        appendAdminProductConditions(sql, params, keyword, brand, minPrice, maxPrice);
        try {
            PreparedStatement ps = conn.prepareStatement(sql.toString());
            setParams(ps, params);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Loi khi dem san pham (admin): " + e.getMessage());
        }
        return 0;
    }

    public List<Product> findAdminProductsWithFiltersPaged(String keyword, String brand, Double minPrice,
                                                           Double maxPrice, int sortMode, int offset, int limit) {
        List<Product> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM products");
        List<Object> params = new ArrayList<>();
        appendAdminProductConditions(sql, params, keyword, brand, minPrice, maxPrice);
        appendOrderBySort(sql, sortMode);
        sql.append(" LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);
        try {
            PreparedStatement ps = conn.prepareStatement(sql.toString());
            setParams(ps, params);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Loi khi loc san pham (admin): " + e.getMessage());
        }
        return list;
    }

    private void setParams(PreparedStatement ps, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            Object p = params.get(i);
            if (p instanceof Double) {
                ps.setDouble(i + 1, (Double) p);
            } else if (p instanceof Integer) {
                ps.setInt(i + 1, (Integer) p);
            } else {
                ps.setString(i + 1, p.toString());
            }
        }
    }

    /** Tong so san pham thoa dieu kien loc (dung tinh so trang). */
    public int countAvailableWithFilters(String brand, Double minPrice, Double maxPrice, String keyword) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM products");
        List<Object> params = new ArrayList<>();
        appendAvailableFilterConditions(sql, params, brand, minPrice, maxPrice, keyword);
        try {
            PreparedStatement ps = conn.prepareStatement(sql.toString());
            setParams(ps, params);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Loi khi dem san pham: " + e.getMessage());
        }
        return 0;
    }

    /**
     * San pham con hang, loc + phan trang (offset bat dau tu 0).
     */
    public List<Product> findAvailableWithFiltersPaged(String brand, Double minPrice, Double maxPrice,
                                                       String keyword, int sortMode,
                                                       int offset, int limit) {
        List<Product> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM products");
        List<Object> params = new ArrayList<>();
        appendAvailableFilterConditions(sql, params, brand, minPrice, maxPrice, keyword);
        appendOrderBySort(sql, sortMode);
        sql.append(" LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);

        try {
            PreparedStatement ps = conn.prepareStatement(sql.toString());
            setParams(ps, params);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Loi khi loc san pham: " + e.getMessage());
        }
        return list;
    }

    /**
     * San pham con hang, loc day du (khong LIMIT) — dung khi can tat ca ket qua loc.
     */
    public List<Product> findAvailableWithFilters(String brand, Double minPrice, Double maxPrice,
                                                  String keyword, int sortMode) {
        List<Product> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM products");
        List<Object> params = new ArrayList<>();
        appendAvailableFilterConditions(sql, params, brand, minPrice, maxPrice, keyword);
        appendOrderBySort(sql, sortMode);
        try {
            PreparedStatement ps = conn.prepareStatement(sql.toString());
            setParams(ps, params);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Loi khi loc san pham: " + e.getMessage());
        }
        return list;
    }

    /** Tong so san pham (admin). */
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM products";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Loi khi dem san pham: " + e.getMessage());
        }
        return 0;
    }

    public List<Product> getAllPaged(int offset, int limit) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products ORDER BY id LIMIT ? OFFSET ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Loi khi lay san pham: " + e.getMessage());
        }
        return list;
    }

    public int countSearchByName(String keyword) {
        if (keyword == null) {
            return 0;
        }
        String sql = "SELECT COUNT(*) FROM products WHERE name LIKE ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + keyword.trim() + "%");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Loi khi dem ket qua tim kiem: " + e.getMessage());
        }
        return 0;
    }

    public List<Product> searchByNamePaged(String keyword, int offset, int limit) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE name LIKE ? ORDER BY id LIMIT ? OFFSET ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + keyword.trim() + "%");
            ps.setInt(2, limit);
            ps.setInt(3, offset);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Loi khi tim san pham: " + e.getMessage());
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