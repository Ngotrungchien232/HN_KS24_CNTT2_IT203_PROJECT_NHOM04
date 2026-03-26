package presentation.admin;

import model.Product;
import service.ProductService;

import java.util.List;
import java.util.Scanner;

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
            } else if (choice == 0) {
                System.out.println("Quay lai menu Admin...");
            } else {
                System.out.println("Lua chon khong hop le!");
            }

        } while (choice != 0);
    }

    private void viewAll() {
        List<Product> list = productService.getAll();
        if (list.isEmpty()) {
            System.out.println("Chua co san pham nao!");
            return;
        }
        printTable(list);
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
        List<Product> list = productService.searchByName(keyword);
        if (list.isEmpty()) {
            System.out.println("Khong tim thay san pham nao!");
            return;
        }
        printTable(list);
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