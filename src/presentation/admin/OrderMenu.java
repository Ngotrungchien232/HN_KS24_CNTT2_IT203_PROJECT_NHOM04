package presentation.admin;

import model.Order;
import model.OrderDetail;
import service.OrderService;

import java.util.List;
import java.util.Scanner;

public class OrderMenu {

    private final OrderService orderService = new OrderService();
    private final Scanner scanner = new Scanner(System.in);

    public void show() {
        int choice;
        do {
            System.out.println("\n===== QUAN LY DON HANG =====");
            System.out.println("1. Xem danh sach don (toan he thong)");
            System.out.println("2. Xem chi tiet don + cap nhat trang thai");
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
                listAllOrders();
            } else if (choice == 2) {
                viewAndUpdateOrder();
            } else if (choice == 0) {
                System.out.println("Quay lai menu Admin...");
            } else {
                System.out.println("Lua chon khong hop le!");
            }
        } while (choice != 0);
    }

    private void listAllOrders() {
        List<Order> list = orderService.getAllOrders();
        if (list.isEmpty()) {
            System.out.println("Chua co don hang nao.");
            return;
        }
        System.out.println("\n--- DANH SACH DON HANG (tat ca) ---");
        System.out.printf("%-6s %-18s %-14s %-12s %-10s%n",
                "Ma don", "Khach", "Tong (VND)", "Trang thai", "Thoi gian");
        System.out.println("---------------------------------------------------------------------------");
        for (Order o : list) {
            System.out.printf("%-6d %-18s %,.0f %-12s %s%n",
                    o.getId(),
                    truncate(o.getCustomerUsername(), 18),
                    o.getTotalPrice(),
                    statusLabel(o.getStatus()),
                    truncate(o.getCreatedAt(), 19));
        }
        System.out.println("--- Tong " + list.size() + " don ---");
    }

    private void viewAndUpdateOrder() {
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

        printOrderHeader(o);
        List<OrderDetail> details = orderService.getOrderDetails(orderId);
        System.out.println("\n--- Chi tiet ---");
        System.out.printf("%-6s %-25s %-8s %-12s %-12s%n", "Ma SP", "Ten SP", "SL", "Don gia", "Thanh tien");
        System.out.println("------------------------------------------------------------------");
        for (OrderDetail d : details) {
            double line = d.getPriceAtTime() * d.getQuantity();
            System.out.printf("%-6d %-25s %-8d %,.0f %,.0f%n",
                    d.getProductId(),
                    truncate(d.getProductName(), 25),
                    d.getQuantity(),
                    d.getPriceAtTime(),
                    line);
        }

        String st = o.getStatus();
        System.out.println("\nTrang thai hien tai: " + statusLabel(st));
        System.out.println("Chon hanh dong:");
        if ("PENDING".equals(st)) {
            System.out.println("1. Duyet - chuyen sang DANG GIAO (SHIPPING)");
            System.out.println("2. Huy don (CANCELLED) - hoan ton kho");
        } else if ("SHIPPING".equals(st)) {
            System.out.println("1. Da giao hang (DELIVERED)");
            System.out.println("2. Huy don (CANCELLED) - hoan ton kho");
        } else {
            System.out.println("(Khong the doi trang thai don nay.)");
            return;
        }
        System.out.println("0. Quay lai khong luu");
        System.out.print("Chon: ");
        String c = scanner.nextLine().trim();
        String newStatus = null;
        if ("1".equals(c)) {
            if ("PENDING".equals(st)) {
                newStatus = "SHIPPING";
            } else if ("SHIPPING".equals(st)) {
                newStatus = "DELIVERED";
            }
        } else if ("2".equals(c)) {
            newStatus = "CANCELLED";
        } else {
            System.out.println("Huy.");
            return;
        }

        if (newStatus != null) {
            orderService.updateOrderStatus(orderId, newStatus);
        }
    }

    private void printOrderHeader(Order o) {
        System.out.println("\n===== DON #" + o.getId() + " =====");
        System.out.println("Khach: " + o.getCustomerUsername() + " (ID user: " + o.getCustomerId() + ")");
        System.out.println("Tong tien: " + String.format("%,.0f", o.getTotalPrice()) + " VND");
        System.out.println("Dat luc: " + o.getCreatedAt());
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
