package service;

import dao.ProductDAO;
import model.Product;

import java.util.List;

public class ProductService {

    /** So san pham moi trang (phan trang) */
    public static final int PAGE_SIZE = 10;

    private ProductDAO productDAO = new ProductDAO();

    public static int calcTotalPages(int totalItems) {
        if (totalItems <= 0) {
            return 0;
        }
        return (totalItems + PAGE_SIZE - 1) / PAGE_SIZE;
    }

    /** San pham con hang (khong loc) */
    public List<Product> getAvailable() {
        return productDAO.getAvailable();
    }

    /**
     * Loc theo hang / gia / tu khoa (ten hoac hang); sortMode: 0 ma SP, 1 gia tang, 2 gia giam.
     */
    public List<Product> getAvailableWithFilters(String brand, Double minPrice, Double maxPrice,
                                                 String keyword, int sortMode) {
        return productDAO.findAvailableWithFilters(brand, minPrice, maxPrice, keyword, sortMode);
    }

    public int countAvailableWithFilters(String brand, Double minPrice, Double maxPrice, String keyword) {
        return productDAO.countAvailableWithFilters(brand, minPrice, maxPrice, keyword);
    }

    /** Trang bat dau tu 1 */
    public List<Product> getAvailableWithFiltersPage(String brand, Double minPrice, Double maxPrice,
                                                     String keyword, int sortMode, int pageOneBased) {
        int p = Math.max(1, pageOneBased);
        int offset = (p - 1) * PAGE_SIZE;
        return productDAO.findAvailableWithFiltersPaged(brand, minPrice, maxPrice, keyword, sortMode, offset, PAGE_SIZE);
    }

    public int countAdminProductsWithFilters(String keyword, String brand, Double minPrice, Double maxPrice) {
        return productDAO.countAdminProductsWithFilters(keyword, brand, minPrice, maxPrice);
    }

    public List<Product> getAdminProductsWithFiltersPage(String keyword, String brand, Double minPrice,
                                                         Double maxPrice, int sortMode, int pageOneBased) {
        int p = Math.max(1, pageOneBased);
        int offset = (p - 1) * PAGE_SIZE;
        return productDAO.findAdminProductsWithFiltersPaged(keyword, brand, minPrice, maxPrice, sortMode, offset, PAGE_SIZE);
    }

    public int countAllProducts() {
        return productDAO.countAll();
    }

    public List<Product> getAllPage(int pageOneBased) {
        int p = Math.max(1, pageOneBased);
        int offset = (p - 1) * PAGE_SIZE;
        return productDAO.getAllPaged(offset, PAGE_SIZE);
    }

    public int countSearchByNameForPaging(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return 0;
        }
        return productDAO.countSearchByName(keyword.trim());
    }

    public List<Product> searchByNamePage(String keyword, int pageOneBased) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }
        int p = Math.max(1, pageOneBased);
        int offset = (p - 1) * PAGE_SIZE;
        return productDAO.searchByNamePaged(keyword.trim(), offset, PAGE_SIZE);
    }

    public List<String> getDistinctBrands() {
        return productDAO.getDistinctBrands();
    }

    public Product findById(int id) {
        return productDAO.findById(id);
    }

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