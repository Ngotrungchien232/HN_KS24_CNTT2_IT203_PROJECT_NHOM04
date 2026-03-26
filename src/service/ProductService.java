package service;

import dao.ProductDAO;
import model.Product;

import java.util.List;

public class ProductService {

    private ProductDAO productDAO = new ProductDAO();

    // Lấy tất cả sản phẩm
    public List<Product> getAll() {
        return productDAO.getAll();
    }

    // Tìm kiếm theo tên
    public List<Product> searchByName(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            System.out.println("️ Từ khóa tìm kiếm không được để trống!");
            return List.of();
        }
        return productDAO.searchByName(keyword.trim());
    }

    // Thêm sản phẩm — validate đầu vào
    public boolean add(int categoryId, String name, String brand,
                       String storage, String color,
                       double price, int stock, String description) {
        if (name == null || name.trim().isEmpty()) {
            System.out.println("️ Tên sản phẩm không được để trống!");
            return false;
        }
        if (price <= 0) {
            System.out.println("️ Giá sản phẩm phải lớn hơn 0!");
            return false;
        }
        if (stock < 0) {
            System.out.println("️ Số lượng tồn kho không được âm!");
            return false;
        }
        Product product = new Product(
                categoryId, name.trim(), brand.trim(),
                storage.trim(), color.trim(),
                price, stock, description.trim()
        );
        return productDAO.add(product);
    }

    // Cập nhật sản phẩm — validate đầu vào
    public boolean update(int id, int categoryId, String name, String brand,
                          String storage, String color,
                          double price, int stock, String description) {
        if (productDAO.findById(id) == null) {
            System.out.println("️ Không tìm thấy sản phẩm với ID: " + id);
            return false;
        }
        if (price <= 0) {
            System.out.println("️ Giá sản phẩm phải lớn hơn 0!");
            return false;
        }
        if (stock < 0) {
            System.out.println(" Số lượng tồn kho không được âm!");
            return false;
        }
        Product product = new Product(
                id, categoryId, name.trim(), brand.trim(),
                storage.trim(), color.trim(),
                price, stock, description.trim()
        );
        return productDAO.update(product);
    }

    // Xóa sản phẩm
    public boolean delete(int id) {
        if (productDAO.findById(id) == null) {
            System.out.println("️ Không tìm thấy sản phẩm với ID: " + id);
            return false;
        }
        return productDAO.delete(id);
    }
}