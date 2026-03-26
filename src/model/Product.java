// sản phẩm gồm các thuộc tính như id, name, brand , storage, color, price, quantity, category (liên kết với Category)

package model;

public class Product {
    private int id;
    private int categoryId;
    private String name;
    private String brand;
    private String storage;
    private String color;
    private double price;
    private int stock;
    private String description;

    // Constructor đầy đủ
    public Product(int id, int categoryId, String name, String brand,
                   String storage, String color, double price,
                   int stock, String description) {
        this.id          = id;
        this.categoryId  = categoryId;
        this.name        = name;
        this.brand       = brand;
        this.storage     = storage;
        this.color       = color;
        this.price       = price;
        this.stock       = stock;
        this.description = description;
    }

    // Constructor khi thêm mới (chưa có id)
    public Product(int categoryId, String name, String brand,
                   String storage, String color, double price,
                   int stock, String description) {
        this.categoryId  = categoryId;
        this.name        = name;
        this.brand       = brand;
        this.storage     = storage;
        this.color       = color;
        this.price       = price;
        this.stock       = stock;
        this.description = description;
    }

    // Getters & Setters
    public int getId()                      { return id; }
    public void setId(int id)               { this.id = id; }

    public int getCategoryId()              { return categoryId; }
    public void setCategoryId(int cid)      { this.categoryId = cid; }

    public String getName()                 { return name; }
    public void setName(String name)        { this.name = name; }

    public String getBrand()                { return brand; }
    public void setBrand(String brand)      { this.brand = brand; }

    public String getStorage()              { return storage; }
    public void setStorage(String storage)  { this.storage = storage; }

    public String getColor()                { return color; }
    public void setColor(String color)      { this.color = color; }

    public double getPrice()                { return price; }
    public void setPrice(double price)      { this.price = price; }

    public int getStock()                   { return stock; }
    public void setStock(int stock)         { this.stock = stock; }

    public String getDescription()              { return description; }
    public void setDescription(String desc)     { this.description = desc; }

    @Override
    public String toString() {
        return String.format(
                "Product{id=%d, name='%s', brand='%s', storage='%s', " +
                        "color='%s', price=%.2f, stock=%d}",
                id, name, brand, storage, color, price, stock
        );
    }
}
