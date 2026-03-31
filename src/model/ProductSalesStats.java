package model;
// thong tin thong ke san pham ban chay trong thang
public class ProductSalesStats {

    private int productId;
    private String productName;
    private String brand;
    private int totalQuantity;
    private double totalRevenue;

    public ProductSalesStats(int productId, String productName, String brand,
                             int totalQuantity, double totalRevenue) {
        this.productId = productId;
        this.productName = productName;
        this.brand = brand;
        this.totalQuantity = totalQuantity;
        this.totalRevenue = totalRevenue;
    }

    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getBrand() {
        return brand;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }
}

