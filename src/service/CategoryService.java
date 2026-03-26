package service;

import dao.CategoryDAO;
import model.Category;

import java.util.List;

public class CategoryService {

    private CategoryDAO categoryDAO = new CategoryDAO();

    // Lấy danh sách tất cả danh mục
    public List<Category> getAll() {
        return categoryDAO.getAll();
    }

    // Thêm danh mục — validate tên không được trống
    public boolean add(String name) {
        if (name == null || name.trim().isEmpty()) {
            System.out.println(" Tên danh mục không được để trống!");
            return false;
        }
        return categoryDAO.add(new Category(name.trim()));
    }

    // Cập nhật danh mục — kiểm tra tồn tại trước
    public boolean update(int id, String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            System.out.println(" Tên danh mục không được để trống!");
            return false;
        }
        Category existing = categoryDAO.findById(id);
        if (existing == null) {
            System.out.println("️ Không tìm thấy danh mục với ID: " + id);
            return false;
        }
        existing.setName(newName.trim());
        return categoryDAO.update(existing);
    }

    // Xóa danh mục — kiểm tra tồn tại trước
    public boolean delete(int id) {
        Category existing = categoryDAO.findById(id);
        if (existing == null) {
            System.out.println(" Không tìm thấy danh mục với ID: " + id);
            return false;
        }
        return categoryDAO.delete(id);
    }
}