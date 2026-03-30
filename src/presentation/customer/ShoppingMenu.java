package presentation.customer;

import model.Product;
import model.User;
import service.OrderService;
import service.ProductService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Mua hang: xem san pham (loc), gio hang, dat hang (transaction o OrderDAO).
 */
public class ShoppingMenu {

    private final User currentUser;
    private final Scanner scanner = new Scanner(System.in);
    private final ProductService productService = new ProductService();
    private final OrderService orderService = new OrderService();

    /** Gio hang: productId -> so luong */
    private final Map<Integer, Integer> cart = new LinkedHashMap<>();

    public ShoppingMenu(User currentUser) {
        this.currentUser = currentUser;
    }

    public void show() {
        int choice;
        do {
            System.out.println("\n----- MUA SAM -----");
            System.out.println("1. Xem san pham (co the loc theo hang / gia)");
            System.out.println("2. Them vao gio hang");
            System.out.println("3. Xem gio hang");
            System.out.println("4. Xoa mot dong trong gio");
            System.out.println("5. Dat hang (xac nhan)");
            System.out.println("0. Quay lai menu khach hang");
            System.out.print("Chon: ");

            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Nhap so hop le!");
                choice = -1;
                continue;
            }

            if (choice == 1) {
                browseProducts();
            } else if (choice == 2) {
                addToCart();
            } else if (choice == 3) {
                viewCart();
            } else if (choice == 4) {
                removeCartLine();
            } else if (choice == 5) {
                checkout();
            } else if (choice == 0) {
                System.out.println("Quay lai.");
            } else {
                System.out.println("Lua chon khong hop le!");
            }
        } while (choice != 0);
    }

    private void browseProducts() {
        System.out.println("\n--- Loc (de trong = bo qua) ---");
        System.out.print("Hang (VD: Samsung, Apple): ");
        String brand = scanner.nextLine().trim();
        if (brand.isEmpty()) {
            brand = null;
        }

        Double minPrice = readOptionalDouble("Gia thap nhat: ");
        Double maxPrice = readOptionalDouble("Gia cao nhat: ");

        System.out.print("Tu khoa (ten hoac hang, de trong = khong tim theo chuoi): ");
        String keyword = scanner.nextLine().trim();
        if (keyword.isEmpty()) {
            keyword = null;
        }

        int sortMode = readSortMode();

        int total = productService.countAvailableWithFilters(brand, minPrice, maxPrice, keyword);
        if (total == 0) {
            System.out.println("Khong co san pham nao phu hop.");
            return;
        }

        int totalPages = ProductService.calcTotalPages(total);
        int page = 1;

        while (true) {
            List<Product> list = productService.getAvailableWithFiltersPage(brand, minPrice, maxPrice, keyword, sortMode, page);

            System.out.println("\n--- Danh sach san pham ---");
            System.out.printf("%-4s %-20s %-12s %-10s %-10s %-10s%n",
                    "ID", "Ten", "Hang", "Dung luong", "Gia", "Ton");
            System.out.println("----------------------------------------------------------------------");
            for (Product p : list) {
                System.out.printf("%-4d %-20s %-12s %-10s %,.0f %10d%n",
                        p.getId(),
                        truncate(p.getName(), 20),
                        p.getBrand(),
                        p.getStorage(),
                        p.getPrice(),
                        p.getStock());
            }

            System.out.println("--- Trang " + page + "/" + totalPages
                    + " | " + ProductService.PAGE_SIZE + " SP/trang | Tong " + total + " san pham ---");
            System.out.println("n: trang sau | p: trang truoc | go so trang (1-" + totalPages + ") | 0: xong xem");
            String cmd = scanner.nextLine().trim().toLowerCase();
            if (cmd.equals("0")) {
                break;
            }
            if (cmd.equals("n")) {
                page = Math.min(totalPages, page + 1);
            } else if (cmd.equals("p")) {
                page = Math.max(1, page - 1);
            } else {
                try {
                    int go = Integer.parseInt(cmd);
                    if (go >= 1 && go <= totalPages) {
                        page = go;
                    } else {
                        System.out.println("Trang khong hop le!");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Nhap n, p, so trang, hoac 0.");
                }
            }
        }
    }

    private void addToCart() {
        System.out.print("Ma san pham (ID): ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("ID khong hop le!");
            return;
        }

        Product p = productService.findById(id);
        if (p == null) {
            System.out.println("Khong tim thay san pham.");
            return;
        }
        if (p.getStock() <= 0) {
            System.out.println("San pham het hang.");
            return;
        }

        System.out.print("So luong: ");
        int qty;
        try {
            qty = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("So luong khong hop le!");
            return;
        }
        if (qty <= 0) {
            System.out.println("So luong phai > 0.");
            return;
        }

        int inCart = cart.getOrDefault(id, 0);
        if (inCart + qty > p.getStock()) {
            System.out.println("Khong du ton kho. Chi con " + p.getStock() + " cai (trong gio da co " + inCart + ").");
            return;
        }

        cart.put(id, inCart + qty);
        System.out.println("Da them vao gio. Tong trong gio: " + (inCart + qty) + " cai.");
    }

    private void viewCart() {
        if (cart.isEmpty()) {
            System.out.println("Gio hang dang trong.");
            return;
        }
        System.out.println("\n--- Gio hang ---");
        double sum = 0;
        for (Map.Entry<Integer, Integer> e : cart.entrySet()) {
            Product p = productService.findById(e.getKey());
            int q = e.getValue();
            if (p == null) {
                System.out.println("ID " + e.getKey() + " x " + q + " (san pham khong ton tai - xoa khoi gio neu can)");
                continue;
            }
            double line = p.getPrice() * q;
            sum += line;
            System.out.printf("ID %-4d | %-25s | x%-3d | %,.0f VND/ cai | Thanh tien: %,.0f%n",
                    p.getId(), truncate(p.getName(), 25), q, p.getPrice(), line);
        }
        System.out.printf(">>> Tam tinh: %,.0f VND%n", sum);
    }

    private void removeCartLine() {
        if (cart.isEmpty()) {
            System.out.println("Gio hang trong.");
            return;
        }
        viewCart();
        System.out.print("Nhap ID san pham can xoa khoi gio: ");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());
            if (cart.remove(id) != null) {
                System.out.println("Da xoa.");
            } else {
                System.out.println("Khong co ID nay trong gio.");
            }
        } catch (NumberFormatException e) {
            System.out.println("ID khong hop le!");
        }
    }

    private void checkout() {
        if (cart.isEmpty()) {
            System.out.println("Gio hang trong, khong the dat hang.");
            return;
        }
        System.out.print("Ban chac chan dat hang? (y/n): ");
        String ok = scanner.nextLine().trim().toLowerCase();
        if (!ok.equals("y") && !ok.equals("yes")) {
            System.out.println("Da huy.");
            return;
        }

        int customerId = currentUser.getId();
        if (orderService.placeOrderFromCart(customerId, cart)) {
            cart.clear();
            System.out.println("Cam on ban da dat hang!");
        }
    }

    /** 0 = ma SP; 1 = gia tang; 2 = gia giam */
    private int readSortMode() {
        System.out.println("Sap xep: 0 = ma san pham | 1 = gia tang dan | 2 = gia giam dan");
        System.out.print("Chon (Enter = 0): ");
        String s = scanner.nextLine().trim();
        if (s.isEmpty()) {
            return 0;
        }
        try {
            int m = Integer.parseInt(s);
            if (m >= 0 && m <= 2) {
                return m;
            }
        } catch (NumberFormatException ignored) {
        }
        System.out.println("Khong hop le, dung mac dinh 0.");
        return 0;
    }

    private Double readOptionalDouble(String prompt) {
        System.out.print(prompt);
        String s = scanner.nextLine().trim();
        if (s.isEmpty()) {
            return null;
        }
        try {
            return Double.parseDouble(s.replace(",", "."));
        } catch (NumberFormatException e) {
            System.out.println("Gia khong hop le, bo qua loc gia phia nay.");
            return null;
        }
    }

    private static String truncate(String s, int max) {
        if (s == null) {
            return "";
        }
        return s.length() <= max ? s : s.substring(0, max - 2) + "..";
    }
}
