// kế thừa lớp User
// thêm một thuộc tính riêng là adđress để lưu địa chỉ của khách hàng

package model;

public class Customer extends User {
    private String address;

    public Customer(int id, String username, String password,
                    String email, String phone, String address) {
        super(id, username, password, email, phone, "CUSTOMER");
        this.address = address;
    }

    public String getAddress()              { return address; }
    public void setAddress(String address)  { this.address = address; }
}