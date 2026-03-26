// Menu CRUD danh mục: thêm, sửa, xóa, xem danh sách → gọi CategoryService

package presentation.admin;

import model.Category;
import service.CategoryService;

import java.util.List;
import java.util.Scanner;

public class CategoryMenu {

    private CategoryService categoryService = new CategoryService();
    private Scanner scanner = new Scanner(System.in);

    public void show() {
        int choice;
        do {
            System.out.println("\n===== QUẢN LÝ DANH MỤC =====");
            System.out.println("1. Xem danh sách danh mục");
            System.out.println("2. Thêm danh mục");
            System.out.println("3. Sửa danh mục");
            System.out.println("4. Xóa danh mục");
            System.out.println("0. Quay lại");
            System.out.print(" Chọn: ");

            choice = Integer.parseInt(scanner.nextLine().trim());

            if (choice == 1) {
                viewAll();
            } else if (choice == 2) {
                add();
            } else if (choice == 3) {
                update();
            } else if (choice == 4) {
                delete();
            } else if (choice == 0) {
                System.out.println(" Quay lại menu Admin...");
            } else {
                System.out.println(" Lựa chọn không hợp lệ!");
            }

        } while (choice != 0);
    }

    // Hiển thị danh sách
    private void viewAll() {
        List<Category> list = categoryService.getAll();
        if (list.isEmpty()) {
            System.out.println("️ Chưa có danh mục nào!");
            return;
        }
        System.out.println("\n-----------------------------");
        System.out.printf("%-5s %-30s%n", "ID", "Tên danh mục");
        System.out.println("-----------------------------");
        for (Category c : list) {
            System.out.printf("%-5d %-30s%n", c.getId(), c.getName());
        }
        System.out.println("-----------------------------");
    }

    // Thêm mới
    private void add() {
        System.out.print(" Nhập tên danh mục mới: ");
        String name = scanner.nextLine();
        if (categoryService.add(name)) {
            System.out.println(" Thêm danh mục thành công!");
        } else {
            System.out.println(" Thêm danh mục thất bại!");
        }
    }

    // Sửa
    private void update() {
        viewAll();
        System.out.print(" Nhập ID danh mục cần sửa: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        System.out.print(" Nhập tên mới: ");
        String newName = scanner.nextLine();
        if (categoryService.update(id, newName)) {
            System.out.println(" Cập nhật danh mục thành công!");
        } else {
            System.out.println(" Cập nhật danh mục thất bại!");
        }
    }

    // Xóa
    private void delete() {
        viewAll();
        System.out.print(" Nhập ID danh mục cần xóa: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("️ Bạn chắc chắn muốn xóa? (y/n): ");
        String confirm = scanner.nextLine().trim();
        if (confirm.equalsIgnoreCase("y")) {
            if (categoryService.delete(id)) {
                System.out.println(" Xóa danh mục thành công!");
            } else {
                System.out.println(" Xóa danh mục thất bại!");
            }
        } else {
            System.out.println(" Hủy xóa!");
        }
    }
}