package presentation.customer;

import model.Order;
import model.OrderDetail;
import model.User;
import service.OrderService;

import java.util.List;
import java.util.Scanner;

/**
 * Lich su don hang cua khach dang nhap.
 */
public class OrderHistoryMenu {

    private final User currentUser;
    private final Scanner scanner = new Scanner(System.in);
    private final OrderService orderService = new OrderService();

    public OrderHistoryMenu(User currentUser) {
        this.currentUser = currentUser;
    }

    public void show() {
        int choice;
        do {
            System.out.println("\n----- LICH SU DON HANG -----");
            System.out.println("1. Xem danh sach don cua toi");
            System.out.println("2. Xem chi tiet / tien do mot don");
            System.out.println("0. Quay lai");
            System.out.print("Chon: ");

            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Nhap so hop le!");
                choice = -1;
                continue;
            }

            if (choice == 1) {
                listMyOrders();
            } else if (choice == 2) {
                viewDetail();
            } else if (choice == 0) {
                System.out.println("Quay lai.");
            } else {
                System.out.println("Lua chon khong hop le!");
            }
        } while (choice != 0);
    }

    private void listMyOrders() {
        int cid = currentUser.getId();
        List<Order> list = orderService.getOrdersByCustomer(cid);
        if (list.isEmpty()) {
            System.out.println("Ban chua co don hang nao.");
            return;
        }
        System.out.println("\n--- Don hang cua ban (tat ca) ---");
        System.out.printf("%-6s %-14s %-14s %-12s%n", "Ma don", "Tong (VND)", "Trang thai", "Dat luc");
        System.out.println("--------------------------------------------------------");
        for (Order o : list) {
            System.out.printf("%-6d %,.0f %-14s %s%n",
                    o.getId(),
                    o.getTotalPrice(),
                    statusLabel(o.getStatus()),
                    truncate(o.getCreatedAt(), 19));
        }
        System.out.println("--- Tong " + list.size() + " don ---");
    }

    private void viewDetail() {
        System.out.print("Nhap ma don hang: ");
        int orderId;
        try {
            orderId = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Ma khong hop le!");
            return;
        }

        Order o = orderService.findOrderById(orderId);
        if (o == null) {
            System.out.println("Khong tim thay don.");
            return;
        }
        if (o.getCustomerId() != currentUser.getId()) {
            System.out.println("Day khong phai don cua ban.");
            return;
        }

        System.out.println("\n===== DON #" + o.getId() + " =====");
        System.out.println("Tong: " + String.format("%,.0f", o.getTotalPrice()) + " VND");
        System.out.println("Trang thai: " + statusLabel(o.getStatus()));
        System.out.println("Tien do: " + progressText(o.getStatus()));
        System.out.println("Dat luc: " + o.getCreatedAt());

        List<OrderDetail> details = orderService.getOrderDetails(orderId);
        System.out.println("\n--- San pham ---");
        for (OrderDetail d : details) {
            System.out.printf("  - %s x%d | %,.0f VND/cai | Thanh tien %,.0f%n",
                    d.getProductName(),
                    d.getQuantity(),
                    d.getPriceAtTime(),
                    d.getPriceAtTime() * d.getQuantity());
        }
    }

    private static String progressText(String status) {
        if (status == null) {
            return "";
        }
        switch (status) {
            case "PENDING":
                return "Don da tiep nhan, dang cho cua hang xu ly.";
            case "SHIPPING":
                return "Don dang duoc giao.";
            case "DELIVERED":
                return "Don da giao thanh cong.";
            case "CANCELLED":
                return "Don da huy.";
            default:
                return status;
        }
    }

    private static String statusLabel(String s) {
        if (s == null) {
            return "";
        }
        switch (s) {
            case "PENDING":
                return "CHO_XU_LY";
            case "SHIPPING":
                return "DANG_GIAO";
            case "DELIVERED":
                return "DA_GIAO";
            case "CANCELLED":
                return "DA_HUY";
            default:
                return s;
        }
    }

    private static String truncate(String s, int max) {
        if (s == null) {
            return "";
        }
        return s.length() <= max ? s : s.substring(0, max - 2) + "..";
    }
}
