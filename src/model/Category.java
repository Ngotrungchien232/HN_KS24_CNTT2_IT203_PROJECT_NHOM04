// đại diện cho danh mục sản phẩm
// gồm các thuộc tính như id, name

package model;

public class Category {
    private int id;
    private String name;

    // Constructor
    public Category(int id, String name) {
        this.id   = id;
        this.name = name;
    }

    // Constructor khi thêm mới (chưa có id)
    public Category(String name) {
        this.name = name;
    }

    // Getter & Setter
    public int getId() { return id; }
    public void setId(int id)   { this.id = id; }

    public String getName()             { return name; }
    public void setName(String name)    { this.name = name; }

    @Override
    public String toString() {
        return "Category{id=" + id + ", name='" + name + "'}";
    }
}
