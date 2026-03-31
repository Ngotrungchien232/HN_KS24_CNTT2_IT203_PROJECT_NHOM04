//Menu chính của Customer: "1. Mua hàng / 2. Đơn hàng / 3. Đăng xuất"

package presentation.customer;

import model.User;
import presentation.ReportMenu;

import java.util.Scanner;

public class CustomerMenu {

    private User currentUser;
    private Scanner scanner = new Scanner(System.in);

    public CustomerMenu(User user) {
        this.currentUser = user;
    }

    public void show() {
        int choice;
        do {
            System.out.println("\n===== MENU KHACH HANG =====");
            System.out.println("Xin chao: " + currentUser.getUsername());
            System.out.println("1. Mua sam");
            System.out.println("2. Lich su don hang");
            System.out.println("3. Xem Top 5 san pham ban chay trong thang");
            System.out.println("0. Dang xuat");
            System.out.print("Chon: ");

            choice = Integer.parseInt(scanner.nextLine().trim());

            if (choice == 1) {
                new ShoppingMenu(currentUser).show();
            } else if (choice == 2) {
                new OrderHistoryMenu(currentUser).show();
            } else if (choice == 3) {
                new ReportMenu().showTop5BestSellingOfMonth();
            } else if (choice == 0) {
                System.out.println("Da dang xuat!");
            } else {
                System.out.println("Lua chon khong hop le!");
            }

        } while (choice != 0);
    }
}
