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

        String username;
        do {
            System.out.print("Ten dang nhap: ");
            username = scanner.nextLine();
            String err = authService.validateUsernameForRegister(username);
            if (err == null) {
                break;
            }
            System.out.println(err);
        } while (true);

        String password;
        do {
            System.out.print("Mat khau (it nhat 6 ky tu): ");
            password = scanner.nextLine();
            String err = authService.validatePasswordForRegister(password);
            if (err == null) {
                break;
            }
            System.out.println(err);
        } while (true);

        String email;
        do {
            System.out.print("Email: ");
            email = scanner.nextLine();
            String err = authService.validateEmailForRegister(email);
            if (err == null) {
                break;
            }
            System.out.println(err);
        } while (true);

        String phone;
        do {
            System.out.print("So dien thoai (10 so): ");
            phone = scanner.nextLine();
            String err = authService.validatePhoneForRegister(phone);
            if (err == null) {
                break;
            }
            System.out.println(err);
        } while (true);

        String address;
        do {
            System.out.print("Dia chi giao hang: ");
            address = scanner.nextLine();
            String err = authService.validateAddressForRegister(address);
            if (err == null) {
                break;
            }
            System.out.println(err);
        } while (true);

        if (authService.register(username, password, email, phone, address)) {
            System.out.println("Dang ky thanh cong! Vui long dang nhap.");
        } else {
            System.out.println("Dang ky that bai!");
        }
    }
}