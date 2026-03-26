package presentation;

import model.Admin;
import model.User;
import presentation.admin.AdminMenu;
import presentation.customer.CustomerMenu;
import service.AuthService;

import java.util.Scanner;

public class MainMenu {

    private AuthService authService = new AuthService();
    private Scanner scanner = new Scanner(System.in);

    public void show() {
        int choice;
        do {
            System.out.println("\n===== SMARTPHONE STORE =====");
            System.out.println("1. Dang nhap");
            System.out.println("2. Dang ky");
            System.out.println("0. Thoat");
            System.out.print("Chon: ");

            choice = Integer.parseInt(scanner.nextLine().trim());

            if (choice == 1) {
                login();
            } else if (choice == 2) {
                register();
            } else if (choice == 0) {
                System.out.println("Tam biet!");
            } else {
                System.out.println("Lua chon khong hop le!");
            }

        } while (choice != 0);
    }

    private void login() {
        System.out.println("\n--- DANG NHAP ---");
        System.out.print("Ten dang nhap: ");
        String username = scanner.nextLine();
        System.out.print("Mat khau: ");
        String password = scanner.nextLine();

        User user = authService.login(username, password);
        if (user != null) {
            System.out.println("Dang nhap thanh cong! Xin chao " + user.getUsername());
            if (user instanceof Admin) {
                new AdminMenu().show();
            } else {
                new CustomerMenu(user).show();
            }
        }
    }

    private void register() {
        System.out.println("\n--- DANG KY ---");
        System.out.print("Ten dang nhap: ");
        String username = scanner.nextLine();
        System.out.print("Mat khau (it nhat 6 ky tu): ");
        String password = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("So dien thoai (10 so): ");
        String phone = scanner.nextLine();
        System.out.print("Dia chi giao hang: ");
        String address = scanner.nextLine();

        if (authService.register(username, password, email, phone, address)) {
            System.out.println("Dang ky thanh cong! Vui long dang nhap.");
        } else {
            System.out.println("Dang ky that bai!");
        }
    }
}