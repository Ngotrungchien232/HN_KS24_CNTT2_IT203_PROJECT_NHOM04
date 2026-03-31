package dao;

import model.CartItem;
import model.Order;
import model.OrderDetail;
import model.ProductSalesStats;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Dat hang: dung TRANSACTION de luu orders + order_details + tru ton kho cung luc.
 */
public class OrderDAO {

    private Connection conn = DBConnection.getConnection();

    /** Du lieu tam sau khi khoa dong san pham (FOR UPDATE) */
    private static class LineData {
        final int productId;
        final int quantity;
        final double unitPrice;

        LineData(int productId, int quantity, double unitPrice) {
            this.productId = productId;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }
    }

    /**
     * @return true neu dat hang thanh cong
     */
    public boolean placeOrder(int customerId, List<CartItem> items) {
        if (items == null || items.isEmpty()) {
            System.out.println("Gio hang trong!");
            return false;
        }

        Connection conn = DBConnection.getConnection();

        try {
            conn.setAutoCommit(false);

            // 1) Khoa tung san pham, kiem tra ton kho, tinh tong tien
            List<LineData> lines = new ArrayList<>();
            double total = 0;

            for (CartItem item : items) {
                if (item.getQuantity() <= 0) {
                    continue;
                }

                try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT id, price, stock FROM products WHERE id = ? FOR UPDATE")) {
                    ps.setInt(1, item.getProductId());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            rollbackQuiet(conn);
                            System.out.println("Khong tim thay san pham ID: " + item.getProductId());
                            return false;
                        }
                        double price = rs.getDouble("price");
                        int stock = rs.getInt("stock");
                        if (stock < item.getQuantity()) {
                            rollbackQuiet(conn);
                            System.out.println("Khong du ton kho! San pham ID "
                                    + item.getProductId() + " chi con " + stock + " cai.");
                            return false;
                        }
                        total += price * item.getQuantity();
                        lines.add(new LineData(item.getProductId(), item.getQuantity(), price));
                    }
                }
            }

            if (lines.isEmpty()) {
                rollbackQuiet(conn);
                System.out.println("Khong co dong hang hop le (so luong phai > 0).");
                return false;
            }

            // 2) Luu don hang
            int orderId;
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO orders (customer_id, total_price, status) VALUES (?, ?, 'PENDING')",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, customerId);
                ps.setDouble(2, total);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next()) {
                        rollbackQuiet(conn);
                        System.out.println("Loi: khong lay duoc ma don hang.");
                        return false;
                    }
                    orderId = keys.getInt(1);
                }
            }

            // 3) Chi tiet don + tru kho
            for (LineData ld : lines) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO order_details (order_id, product_id, quantity, price_at_time) "
                                + "VALUES (?, ?, ?, ?)")) {
                    ps.setInt(1, orderId);
                    ps.setInt(2, ld.productId);
                    ps.setInt(3, ld.quantity);
                    ps.setDouble(4, ld.unitPrice);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE products SET stock = stock - ? WHERE id = ? AND stock >= ?")) {
                    ps.setInt(1, ld.quantity);
                    ps.setInt(2, ld.productId);
                    ps.setInt(3, ld.quantity);
                    int updated = ps.executeUpdate();
                    if (updated != 1) {
                        rollbackQuiet(conn);
                        System.out.println("Loi tru ton kho san pham ID " + ld.productId);
                        return false;
                    }
                }
            }

            conn.commit();
            System.out.println("Dat hang thanh cong! Ma don: " + orderId
                    + " | Tong tien: " + String.format("%.0f", total) + " VND");
            return true;

        } catch (SQLException e) {
            rollbackQuiet(conn);
            System.out.println("Loi dat hang (transaction rollback): " + e.getMessage());
            return false;
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ignored) {
            }
        }
    }

    // ----- Doc don hang (admin / khach) -----

   //Tat ca don (admin), moi nhat truoc
    public List<Order> findAllOrders() {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.id, o.customer_id, o.total_price, o.status, o.created_at, u.username "
                + "FROM orders o JOIN users u ON o.customer_id = u.id "
                + "ORDER BY o.created_at ASC"; // thay ASC nếu muốn đổi thứ tự
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapOrderJoinUser(rs));
            }
        } catch (SQLException e) {
            System.out.println("Loi khi lay danh sach don: " + e.getMessage());
        }
        return list;
    }

    /** Don cua mot khach */
    public List<Order> findOrdersByCustomer(int customerId) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE customer_id = ? ORDER BY created_at DESC";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapOrder(rs));
            }
        } catch (SQLException e) {
            System.out.println("Loi khi lay don khach: " + e.getMessage());
        }
        return list;
    }

    public Order findOrderById(int orderId) {
        String sql = "SELECT o.id, o.customer_id, o.total_price, o.status, o.created_at, u.username "
                + "FROM orders o JOIN users u ON o.customer_id = u.id WHERE o.id = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapOrderJoinUser(rs);
            }
        } catch (SQLException e) {
            System.out.println("Loi khi tim don: " + e.getMessage());
        }
        return null;
    }

    public List<OrderDetail> findDetailsByOrderId(int orderId) {
        List<OrderDetail> list = new ArrayList<>();
        String sql = "SELECT od.id, od.order_id, od.product_id, od.quantity, od.price_at_time, p.name AS product_name "
                + "FROM order_details od JOIN products p ON od.product_id = p.id WHERE od.order_id = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                OrderDetail d = new OrderDetail();
                d.setId(rs.getInt("id"));
                d.setOrderId(rs.getInt("order_id"));
                d.setProductId(rs.getInt("product_id"));
                d.setQuantity(rs.getInt("quantity"));
                d.setPriceAtTime(rs.getDouble("price_at_time"));
                d.setProductName(rs.getString("product_name"));
                list.add(d);
            }
        } catch (SQLException e) {
            System.out.println("Loi khi lay chi tiet don: " + e.getMessage());
        }
        return list;
    }

    /**
     * Cap nhat trang thai don. Neu HUY: hoan lai ton kho (PENDING hoac SHIPPING).
     */
    public boolean updateOrderStatus(int orderId, String newStatus) {
        Connection conn = DBConnection.getConnection();
        try {
            conn.setAutoCommit(false);

            String oldStatus;
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT status FROM orders WHERE id = ? FOR UPDATE")) {
                ps.setInt(1, orderId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        rollbackQuiet(conn);
                        System.out.println("Khong tim thay don hang!");
                        return false;
                    }
                    oldStatus = rs.getString("status");
                }
            }

            if ("CANCELLED".equals(oldStatus) || "DELIVERED".equals(oldStatus)) {
                rollbackQuiet(conn);
                System.out.println("Don o trang thai ket thuc, khong doi duoc.");
                return false;
            }

            if ("CANCELLED".equals(newStatus)
                    && ("PENDING".equals(oldStatus) || "SHIPPING".equals(oldStatus))) {
                restoreStockForOrder(conn, orderId);
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE orders SET status = ? WHERE id = ?")) {
                ps.setString(1, newStatus);
                ps.setInt(2, orderId);
                int n = ps.executeUpdate();
                if (n != 1) {
                    rollbackQuiet(conn);
                    return false;
                }
            }

            conn.commit();
            System.out.println("Cap nhat trang thai don thanh cong: " + newStatus);
            return true;
        } catch (SQLException e) {
            rollbackQuiet(conn);
            System.out.println("Loi cap nhat don: " + e.getMessage());
            return false;
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ignored) {
            }
        }
    }

    private void restoreStockForOrder(Connection conn, int orderId) throws SQLException {
        List<OrderDetail> lines = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT product_id, quantity FROM order_details WHERE order_id = ?")) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderDetail d = new OrderDetail();
                    d.setProductId(rs.getInt("product_id"));
                    d.setQuantity(rs.getInt("quantity"));
                    lines.add(d);
                }
            }
        }
        for (OrderDetail d : lines) {
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE products SET stock = stock + ? WHERE id = ?")) {
                ps.setInt(1, d.getQuantity());
                ps.setInt(2, d.getProductId());
                ps.executeUpdate();
            }
        }
    }

    private Order mapOrderJoinUser(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setId(rs.getInt("id"));
        o.setCustomerId(rs.getInt("customer_id"));
        o.setTotalPrice(rs.getDouble("total_price"));
        o.setStatus(rs.getString("status"));
        o.setCustomerUsername(rs.getString("username"));
        Timestamp ts = rs.getTimestamp("created_at");
        o.setCreatedAt(ts != null ? ts.toString() : "");
        return o;
    }

    private Order mapOrder(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setId(rs.getInt("id"));
        o.setCustomerId(rs.getInt("customer_id"));
        o.setTotalPrice(rs.getDouble("total_price"));
        o.setStatus(rs.getString("status"));
        Timestamp ts = rs.getTimestamp("created_at");
        o.setCreatedAt(ts != null ? ts.toString() : "");
        return o;
    }

    /**
     * Top san pham ban chay trong 1 thang, chi tinh don o trang thai DELIVERED.
     *
     * @param month thang (1-12)
     * @param year  nam (VD: 2026)
     * @param limit so san pham muon lay (VD: 5)
     */
    public List<ProductSalesStats> findTopSellingProductsByMonth(int month, int year, int limit) {
        List<ProductSalesStats> list = new ArrayList<>();
        String sql =
                "SELECT p.id, p.name, p.brand, " +
                        "SUM(od.quantity) AS total_qty, " +
                        "SUM(od.quantity * od.price_at_time) AS total_revenue " +
                        "FROM orders o " +
                        "JOIN order_details od ON o.id = od.order_id " +
                        "JOIN products p ON od.product_id = p.id " +
                        "WHERE o.status = 'DELIVERED' " +
                        "AND MONTH(o.created_at) = ? " +
                        "AND YEAR(o.created_at) = ? " +
                        "GROUP BY p.id, p.name, p.brand " +
                        "ORDER BY total_qty DESC " +
                        "LIMIT ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, month);
            ps.setInt(2, year);
            ps.setInt(3, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ProductSalesStats s = new ProductSalesStats(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("brand"),
                        rs.getInt("total_qty"),
                        rs.getDouble("total_revenue")
                );
                list.add(s);
            }
        } catch (SQLException e) {
            System.out.println("Loi khi thong ke san pham ban chay: " + e.getMessage());
        }
        return list;
    }

    private static void rollbackQuiet(Connection conn) {
        try {
            if (conn != null && !conn.getAutoCommit()) {
                conn.rollback();
            }
        } catch (SQLException ignored) {
        }
    }
}
