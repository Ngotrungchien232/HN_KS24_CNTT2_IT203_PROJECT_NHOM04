-- tạo bảng 
create database Phone_Store;
use Phone_Store;

-- tạo bảng User
CREATE TABLE users (
    id          INT PRIMARY KEY AUTO_INCREMENT,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,           -- Lưu BCrypt hash
    email       VARCHAR(100) NOT NULL UNIQUE,
    phone       VARCHAR(10)  NOT NULL UNIQUE,
    address     VARCHAR(255),                    -- Chỉ Customer cần
    role        ENUM('ADMIN', 'CUSTOMER') NOT NULL DEFAULT 'CUSTOMER',
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP
);

DELETE FROM users WHERE id = 1;
-- tạo một tài khoản mặc định cho admin
INSERT INTO users (id, username, password, email, phone, role)
VALUES (1, 'admin', 'admin123', 'admin123@gmail.com', '0123456789', 'ADMIN');
UPDATE users
SET password = '$2a$10$zOdfzR/VgYFbeR0t99gURe1wVALcNVzPf5cmx11PQ9FjmG305EC2e'
WHERE username = 'admin';


 
 
-- tạo bảng categories danh mục sản phẩm 
CREATE TABLE categories (
    id      INT PRIMARY KEY AUTO_INCREMENT,
    name    VARCHAR(100) NOT NULL UNIQUE
);

-- tạo bảng product thông tin chi tiết liên kết với categories
CREATE TABLE products (
    id              INT PRIMARY KEY AUTO_INCREMENT,
    category_id     INT          NOT NULL,
    name            VARCHAR(150) NOT NULL,
    brand           VARCHAR(50)  NOT NULL,
    storage         VARCHAR(20)  NOT NULL,       -- VD: 128GB, 256GB
    color           VARCHAR(50)  NOT NULL,
    price           DECIMAL(15,2) NOT NULL,
    stock           INT          NOT NULL DEFAULT 0,
    description     TEXT,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_product_category
        FOREIGN KEY (category_id) REFERENCES categories(id)
        ON DELETE RESTRICT                       -- Không xóa danh mục nếu còn sản phẩm
        ON UPDATE CASCADE
);

-- tạo bảng orders một đơn của khách hàng liên kết với user 
CREATE TABLE orders (
    id              INT PRIMARY KEY AUTO_INCREMENT,
    customer_id     INT            NOT NULL,
    total_price     DECIMAL(15,2)  NOT NULL,
    status          ENUM('PENDING', 'SHIPPING', 'DELIVERED', 'CANCELLED')
                    NOT NULL DEFAULT 'PENDING',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_order_customer
        FOREIGN KEY (customer_id) REFERENCES users(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);

-- tạo bảng order_details Chi tiết từng sản phẩm trong đơn hàng, liên kết với orders và products
CREATE TABLE order_details (
    id              INT PRIMARY KEY AUTO_INCREMENT,
    order_id        INT            NOT NULL,
    product_id      INT            NOT NULL,
    quantity        INT            NOT NULL,
    price_at_time   DECIMAL(15,2)  NOT NULL,     -- Giá tại thời điểm mua, không đổi theo product

    CONSTRAINT fk_detail_order
        FOREIGN KEY (order_id) REFERENCES orders(id)
        ON DELETE CASCADE                        -- Xóa đơn hàng thì xóa luôn chi tiết
        ON UPDATE CASCADE,

    CONSTRAINT fk_detail_product
        FOREIGN KEY (product_id) REFERENCES products(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);


-- một số logic về khóa chính và khóa ngoại 

-- 1. products → categories ON DELETE RESTRICT => Không cho xóa danh mục iPhone nếu còn sản phẩm iPhone trong DB
-- 2. orders → users ON DELETE RESTRICT => không cho xóa tài khoản nếu như tài khoản của khách hàng đó còn đơn hàng 
-- 3. order_details → orders ON DELETE CASCADE => Xóa đơn hàng → tự động xóa toàn bộ chi tiết đơn đó
-- 4. order_details → products ON DELETE RESTRICT => Không cho xóa sản phẩm nếu đã có ai đặt mua rồi
-- 5. role ENUM trong users => Phân quyền Admin/Customer ngay tại tầng database — sạch và an toàn.

-- Quan hệ về các thực thể 
-- 1. users 1 → n orders  => 1 khách hàng có thể có nhiều đơn hàng
-- 2.orders 1 → n order_details => 1 đơn hàng có thể có nhiều sản phẩm
-- 3.products 1 → n order_details => 1 sản phẩm có thể xuất hiện trong nhiều đơn hàng
-- 4. categories 1 → n products => 1 danh mục chứa nhiều sản phẩm





