package service;

import dao.OrderDAO;
import model.CartItem;
import model.Order;
import model.OrderDetail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrderService {

    private OrderDAO orderDAO = new OrderDAO();

    public boolean placeOrderFromCart(int customerId, Map<Integer, Integer> cart) {
        if (cart == null || cart.isEmpty()) {
            System.out.println("Gio hang trong!");
            return false;
        }

        List<CartItem> lines = new ArrayList<>();
        for (Map.Entry<Integer, Integer> e : cart.entrySet()) {
            int qty = e.getValue() == null ? 0 : e.getValue();
            if (qty > 0) {
                lines.add(new CartItem(e.getKey(), qty));
            }
        }

        if (lines.isEmpty()) {
            System.out.println("Gio hang khong co so luong hop le!");
            return false;
        }

        return orderDAO.placeOrder(customerId, lines);
    }

    public List<Order> getAllOrders() {
        return orderDAO.findAllOrders();
    }

    public List<Order> getOrdersByCustomer(int customerId) {
        return orderDAO.findOrdersByCustomer(customerId);
    }

    public Order findOrderById(int orderId) {
        return orderDAO.findOrderById(orderId);
    }

    public List<OrderDetail> getOrderDetails(int orderId) {
        return orderDAO.findDetailsByOrderId(orderId);
    }

    public boolean updateOrderStatus(int orderId, String newStatus) {
        return orderDAO.updateOrderStatus(orderId, newStatus);
    }
}
