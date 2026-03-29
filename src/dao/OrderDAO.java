package dao;

import model.CartItem;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Dat hang: dung TRANSACTION de luu orders + order_details + tru ton kho cung luc.
 */
public class OrderDAO {

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

    private static void rollbackQuiet(Connection conn) {
        try {
            if (conn != null && !conn.getAutoCommit()) {
                conn.rollback();
            }
        } catch (SQLException ignored) {
        }
    }
}
