package presentation;

import model.ProductSalesStats;
import service.OrderService;

import java.time.LocalDate;
import java.util.List;

// menu cho top 5 san pham ban chay
public class ReportMenu {

    private final OrderService orderService = new OrderService();

    public void showTop5BestSellingOfMonth() {
        System.out.println("\n===== BAO CAO: TOP 5 SAN PHAM BAN CHAY TRONG THANG =====");

        LocalDate today = LocalDate.now();
        int month = today.getMonthValue();
        int year = today.getYear();

        List<ProductSalesStats> stats = orderService.getTopSellingProductsByMonth(month, year, 5);
        if (stats.isEmpty()) {
            System.out.println("Khong co du lieu don hang da giao trong thang/nam nay.");
            return;
        }

        System.out.printf("\nTop 5 san pham ban chay trong thang %02d/%d (chi tinh don DA_GIAO):%n", month, year);
        System.out.printf("%-4s %-6s %-25s %-12s %-10s %-15s%n",
                "STT", "ID", "Ten san pham", "Hang", "So luong", "Doanh thu (VND)");
        System.out.println("-------------------------------------------------------------------------------");
        int index = 1;
        for (ProductSalesStats s : stats) {
            System.out.printf("%-4d %-6d %-25s %-12s %-10d %,-15.0f%n",
                    index++,
                    s.getProductId(),
                    truncate(s.getProductName(), 25),
                    s.getBrand(),
                    s.getTotalQuantity(),
                    s.getTotalRevenue());
        }
    }

    private static String truncate(String s, int max) {
        if (s == null) {
            return "";
        }
        return s.length() <= max ? s : s.substring(0, max - 2) + "..";
    }
}

