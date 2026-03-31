// Menu chính của Admin: "1. Sản phẩm / 2. Danh mục / 3. Đơn hàng / 4. Đăng xuất"

package presentation.admin;

import presentation.ReportMenu;

import java.util.Scanner;

public class AdminMenu {

    private Scanner scanner = new Scanner(System.in);

    public void show() {
        int choice;
        do {
            System.out.println("\n===== MENU ADMIN =====");
            System.out.println("1. Quan ly Danh muc");
            System.out.println("2. Quan ly San pham");
            System.out.println("3. Quan ly Don hang");
            System.out.println("4. Bao cao thong ke");
            System.out.println("0. Dang xuat");
            System.out.print("Chon: ");

            choice = Integer.parseInt(scanner.nextLine().trim());

            if (choice == 1) {
                new CategoryMenu().show();
            } else if (choice == 2) {
                new ProductMenu().show();
            } else if (choice == 3) {
                new OrderMenu().show();
            } else if (choice == 4) {
                new ReportMenu().showTop5BestSellingOfMonth();
            } else if (choice == 0) {
                System.out.println("Da dang xuat!");
            } else {
                System.out.println("Lua chon khong hop le!");
            }

        } while (choice != 0);
    }
}