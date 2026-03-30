package presentation.admin;

import model.Product;
import service.ProductService;

import java.util.List;
import java.util.Scanner;
import java.util.function.IntFunction;

public class ProductMenu {

    private ProductService productService = new ProductService();
    private Scanner scanner = new Scanner(System.in);

    public void show() {
        int choice;
        do {
            System.out.println("\n===== QUAN LY SAN PHAM =====");
            System.out.println("1. Xem danh sach san pham");
            System.out.println("2. Them san pham");
            System.out.println("3. Sua san pham");
            System.out.println("4. Xoa san pham");
            System.out.println("5. Tim kiem san pham theo ten");
            System.out.println("6. Tim kiem & loc nang cao (tu khoa + hang + gia + sap xep)");
            System.out.println("0. Quay lai");
            System.out.print("Chon: ");

            choice = Integer.parseInt(scanner.nextLine().trim());

            if (choice == 1) {
                viewAll();
            } else if (choice == 2) {
                add();
            } else if (choice == 3) {
                update();
            } else if (choice == 4) {
                delete();
            } else if (choice == 5) {
                search();
            } else if (choice == 6) {
                searchAdvanced();
            } else if (choice == 0) {
                System.out.println("Quay lai menu Admin...");
            } else {
                System.out.println("Lua chon khong hop le!");
            }

        } while (choice != 0);
    }

    private void viewAll() {
        int total = productService.countAllProducts();
        if (total == 0) {
            System.out.println("Chua co san pham nao!");
            return;
        }
        runPaginationLoop("DANH SACH SAN PHAM", total, page -> productService.getAllPage(page));
    }

    private void add() {
        System.out.println("\n--- THEM SAN PHAM MOI ---");
        System.out.print("ID danh muc: ");
        int categoryId = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Ten san pham: ");
        String name = scanner.nextLine();
        System.out.print("Hang san xuat: ");
        String brand = scanner.nextLine();
        System.out.print("Dung luong (VD: 128GB): ");
        String storage = scanner.nextLine();
        System.out.print("Mau sac: ");
        String color = scanner.nextLine();
        System.out.print("Gia ban (VND): ");
        double price = Double.parseDouble(scanner.nextLine().trim());
        System.out.print("So luong ton kho: ");
        int stock = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Mo ta: ");
        String description = scanner.nextLine();

        if (productService.add(categoryId, name, brand, storage, color, price, stock, description)) {
            System.out.println("Them san pham thanh cong!");
        } else {
            System.out.println("Them san pham that bai!");
        }
    }

    private void update() {
        viewAll();
        System.out.print("Nhap ID san pham can sua: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        System.out.println("\n--- NHAP THONG TIN MOI ---");
        System.out.print("ID danh muc moi: ");
        int categoryId = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Ten san pham moi: ");
        String name = scanner.nextLine();
        System.out.print("Hang san xuat moi: ");
        String brand = scanner.nextLine();
        System.out.print("Dung luong moi: ");
        String storage = scanner.nextLine();
        System.out.print("Mau sac moi: ");
        String color = scanner.nextLine();
        System.out.print("Gia ban moi (VND): ");
        double price = Double.parseDouble(scanner.nextLine().trim());
        System.out.print("So luong ton kho moi: ");
        int stock = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Mo ta moi: ");
        String description = scanner.nextLine();

        if (productService.update(id, categoryId, name, brand, storage, color, price, stock, description)) {
            System.out.println("Cap nhat san pham thanh cong!");
        } else {
            System.out.println("Cap nhat san pham that bai!");
        }
    }

    private void delete() {
        viewAll();
        System.out.print("Nhap ID san pham can xoa: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Ban chac chan muon xoa? (y/n): ");
        String confirm = scanner.nextLine().trim();
        if (confirm.equalsIgnoreCase("y")) {
            if (productService.delete(id)) {
                System.out.println("Xoa san pham thanh cong!");
            } else {
                System.out.println("Xoa san pham that bai!");
            }
        } else {
            System.out.println("Huy xoa!");
        }
    }

    private void search() {
        System.out.print("Nhap ten san pham can tim: ");
        String keyword = scanner.nextLine();
        if (keyword == null || keyword.trim().isEmpty()) {
            System.out.println("Tu khoa khong duoc de trong!");
            return;
        }
        int total = productService.countSearchByNameForPaging(keyword);
        if (total == 0) {
            System.out.println("Khong tim thay san pham nao!");
            return;
        }
        final String kw = keyword.trim();
        runPaginationLoop("KET QUA TIM KIEM", total, page -> productService.searchByNamePage(kw, page));
    }

    /** Loc theo tu khoa (ten/hang), hang, khoang gia; sap xep theo gia hoac ma SP. */
    private void searchAdvanced() {
        System.out.println("\n--- TIM KIEM / LOC NANG CAO ---");
        System.out.print("Tu khoa (trong ten hoac hang, de trong = bo qua): ");
        String keyword = scanner.nextLine().trim();
        if (keyword.isEmpty()) {
            keyword = null;
        }
        System.out.print("Hang (VD: Apple, de trong = bo qua): ");
        String brand = scanner.nextLine().trim();
        if (brand.isEmpty()) {
            brand = null;
        }
        Double minP = readOptionalDoubleAdmin("Gia thap nhat: ");
        Double maxP = readOptionalDoubleAdmin("Gia cao nhat: ");
        System.out.println("Sap xep: 0 = ma SP | 1 = gia tang | 2 = gia giam");
        System.out.print("Chon (Enter = 0): ");
        int sortMode = 0;
        String sm = scanner.nextLine().trim();
        if (!sm.isEmpty()) {
            try {
                sortMode = Integer.parseInt(sm);
                if (sortMode < 0 || sortMode > 2) {
                    sortMode = 0;
                }
            } catch (NumberFormatException e) {
                sortMode = 0;
            }
        }

        int total = productService.countAdminProductsWithFilters(keyword, brand, minP, maxP);
        if (total == 0) {
            System.out.println("Khong co san pham nao phu hop.");
            return;
        }
        final String kw = keyword;
        final String br = brand;
        final Double minF = minP;
        final Double maxF = maxP;
        final int sort = sortMode;
        runPaginationLoop("TIM KIEM NANG CAO", total,
                page -> productService.getAdminProductsWithFiltersPage(kw, br, minF, maxF, sort, page));
    }

    private Double readOptionalDoubleAdmin(String prompt) {
        System.out.print(prompt);
        String line = scanner.nextLine().trim();
        if (line.isEmpty()) {
            return null;
        }
        try {
            return Double.parseDouble(line.replace(",", "."));
        } catch (NumberFormatException e) {
            System.out.println("Bo qua gia khong hop le.");
            return null;
        }
    }

    /**
     * Hien thi san pham theo trang: n / p / so trang / 0 thoat.
     */
    private void runPaginationLoop(String title, int total, IntFunction<List<Product>> loadPage) {
        int totalPages = ProductService.calcTotalPages(total);
        int page = 1;
        while (true) {
            List<Product> list = loadPage.apply(page);
            System.out.println("\n===== " + title + " =====");
            printTable(list);
            System.out.println("--- Trang " + page + "/" + totalPages
                    + " | " + ProductService.PAGE_SIZE + " SP/trang | Tong " + total + " san pham ---");
            System.out.println("n: trang sau | p: trang truoc | go so trang (1-" + totalPages + ") | 0: thoat");
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

    private void printTable(List<Product> list) {
        System.out.println("\n" + "-".repeat(90));
        System.out.printf("%-5s %-20s %-10s %-10s %-10s %-15s %-8s%n",
                "ID", "Ten", "Hang", "Dung luong", "Mau", "Gia", "Ton kho");
        System.out.println("-".repeat(90));
        for (Product p : list) {
            System.out.printf("%-5d %-20s %-10s %-10s %-10s %-15.0f %-8d%n",
                    p.getId(), p.getName(), p.getBrand(),
                    p.getStorage(), p.getColor(),
                    p.getPrice(), p.getStock());
        }
        System.out.println("-".repeat(90));
    }
}