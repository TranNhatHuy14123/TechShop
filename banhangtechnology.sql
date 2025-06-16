CREATE DATABASE project_PRJ_TechShop1;
USE project_PRJ_TechShop1;

-- Bảng không phụ thuộc
CREATE TABLE Categories (
    CategoryID INT IDENTITY(1,1) PRIMARY KEY,
    Name NVARCHAR(50) NOT NULL UNIQUE,
    Description NVARCHAR(MAX)
);
INSERT INTO Categories (Name, Description) VALUES
('Laptops', 'High-performance laptops'),
('Smartphones', 'Latest smartphones with 5G'),
('Cameras', 'Professional and amateur cameras'),
('Accessories', 'Mouse, keyboard, and other accessories');

CREATE TABLE Brands (
    BrandID INT IDENTITY(1,1) PRIMARY KEY,
    Name NVARCHAR(50) NOT NULL UNIQUE,
    Description NVARCHAR(MAX)
);
INSERT INTO Brands (Name, Description) VALUES
('Samsung', 'Leading electronics brand'),
('LG', 'Innovative home appliances and electronics'),
('Sony', 'Premium audio and imaging products'),
('Dell', 'Computer and laptop manufacturer'),
('Canon', 'Camera and imaging products');

CREATE TABLE PaymentMethods (
    PaymentMethodID INT IDENTITY(1,1) PRIMARY KEY,
    Name NVARCHAR(50) NOT NULL UNIQUE,
    Description NVARCHAR(MAX)
);
INSERT INTO PaymentMethods (Name, Description) VALUES
('PayPal', 'Online payment via PayPal'),
('Visa', 'Credit card payment'),
('Direct Bank Transfer', 'Bank transfer payment');

-- Bảng phụ thuộc vào bảng cha
CREATE TABLE Users (
    UserID INT IDENTITY(1,1) PRIMARY KEY,
    FirstName NVARCHAR(50) NOT NULL,
    LastName NVARCHAR(50) NOT NULL,
    Email NVARCHAR(100) UNIQUE NOT NULL,
    Password NVARCHAR(255) NOT NULL,
    Address NVARCHAR(255),
    City NVARCHAR(50),
    Country NVARCHAR(50),
    ZipCode NVARCHAR(20),
    Phone NVARCHAR(20),
    Role NVARCHAR(20) NOT NULL DEFAULT 'Customer' CHECK (Role IN ('Guest', 'Customer', 'Manager', 'Owner')),
    CreatedAt DATETIME DEFAULT GETDATE(),
    UpdatedAt DATETIME DEFAULT GETDATE()
);
INSERT INTO Users (FirstName, LastName, Email, Password, Role) VALUES
('Nguyen', 'Van A', 'nguyenvana@example.com', 'password123', 'Customer'),
('Tran', 'Thi B', 'tranthib@example.com', 'password456', 'Customer');

CREATE TABLE Products (
    ProductID INT IDENTITY(1,1) PRIMARY KEY,
    Name NVARCHAR(100) NOT NULL,
    CategoryID INT NOT NULL,
    BrandID INT NOT NULL,
    Price DECIMAL(10, 2) NOT NULL,
    OldPrice DECIMAL(10, 2),
    Discount INT CHECK (Discount >= 0 AND Discount <= 100),
    Images NVARCHAR(MAX),
    IsNew BIT DEFAULT 0,
    Rating DECIMAL(3, 1) CHECK (Rating >= 0 AND Rating <= 5),
    StockStatus NVARCHAR(20) CHECK (StockStatus IN ('In Stock', 'Out of Stock')),
    Description NVARCHAR(MAX),
    Size NVARCHAR(10),
    Color NVARCHAR(20),
    CreatedAt DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (CategoryID) REFERENCES Categories(CategoryID),
    FOREIGN KEY (BrandID) REFERENCES Brands(BrandID)
);
INSERT INTO Products (Name, CategoryID, BrandID, Price, OldPrice, Discount, Images, IsNew, Rating, StockStatus, Description, Size, Color) VALUES
('Laptop Dell XPS 13', 1, 4, 1500.00, 1600.00, 6, '["./img/product01.png", "./img/product03.png"]', 1, 4.5, 'In Stock', 'High-performance laptop with 16GB RAM', 'M', 'Silver'),
('Smartphone Samsung Galaxy S21', 2, 1, 800.00, 900.00, 11, '["./img/product02.png"]', 0, 4.8, 'In Stock', 'Latest smartphone with 5G', NULL, 'Black'),
('Canon EOS 5D', 3, 5, 1200.00, NULL, 0, '["./img/product03.png"]', 0, 4.2, 'In Stock', 'Professional DSLR camera', NULL, 'Black'),
('Wireless Mouse', 4, 2, 25.00, 30.00, 17, '["./img/product04.png"]', 1, 4.0, 'In Stock', 'Ergonomic wireless mouse', NULL, 'Black');

CREATE TABLE Addresses (
    AddressID INT IDENTITY(1,1) PRIMARY KEY,
    UserID INT NOT NULL,
    FirstName NVARCHAR(50) NOT NULL,
    LastName NVARCHAR(50) NOT NULL,
    Address NVARCHAR(255) NOT NULL,
    City NVARCHAR(50),
    Country NVARCHAR(50),
    ZipCode NVARCHAR(20),
    Phone NVARCHAR(20),
    IsDefault BIT DEFAULT 0,
    FOREIGN KEY (UserID) REFERENCES Users(UserID)
);
INSERT INTO Addresses (UserID, FirstName, LastName, Address, City, Country, ZipCode, Phone, IsDefault) VALUES
(1, 'Nguyen', 'Van A', '132-Hoàng Hoa Thám', 'Hà Nội', 'Việt Nam', '10000', '+84925511844', 1),
(2, 'Tran', 'Thi B', '456-Lê Lợi', 'TP. Hồ Chí Minh', 'Việt Nam', '70000', '+84925511845', 1);

CREATE TABLE Carts (
    CartID INT IDENTITY(1,1) PRIMARY KEY,
    UserID INT NOT NULL,
    Subtotal DECIMAL(10, 2) DEFAULT 0.00,
    UpdatedAt DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (UserID) REFERENCES Users(UserID)
);
INSERT INTO Carts (UserID) VALUES (1);

CREATE TABLE Cart_Products (
    CartProductID INT IDENTITY(1,1) PRIMARY KEY,
    CartID INT NOT NULL,
    ProductID INT NOT NULL,
    Quantity INT NOT NULL DEFAULT 1,
    FOREIGN KEY (CartID) REFERENCES Carts(CartID),
    FOREIGN KEY (ProductID) REFERENCES Products(ProductID)
);
INSERT INTO Cart_Products (CartID, ProductID, Quantity) VALUES
(1, 1, 1),
(1, 2, 2);

CREATE TABLE Wishlists (
    WishlistID INT IDENTITY(1,1) PRIMARY KEY,
    UserID INT NOT NULL,
    FOREIGN KEY (UserID) REFERENCES Users(UserID)
);
INSERT INTO Wishlists (UserID) VALUES (1);

CREATE TABLE Wishlist_Products (
    WishlistProductID INT IDENTITY(1,1) PRIMARY KEY,
    WishlistID INT NOT NULL,
    ProductID INT NOT NULL,
    FOREIGN KEY (WishlistID) REFERENCES Wishlists(WishlistID),
    FOREIGN KEY (ProductID) REFERENCES Products(ProductID)
);
INSERT INTO Wishlist_Products (WishlistID, ProductID) VALUES
(1, 1),
(1, 3);

CREATE TABLE Orders (
    OrderID INT IDENTITY(1,1) PRIMARY KEY,
    UserID INT NOT NULL,
    Total DECIMAL(10, 2) NOT NULL,
    BillingAddressID INT NOT NULL,
    ShippingAddressID INT,
    OrderNotes NVARCHAR(MAX),
    PaymentMethodID INT NOT NULL,
    Status NVARCHAR(20) CHECK (Status IN ('Pending', 'Processing', 'Shipped', 'Delivered', 'Cancelled')) DEFAULT 'Pending',
    CreatedAt DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (UserID) REFERENCES Users(UserID),
    FOREIGN KEY (BillingAddressID) REFERENCES Addresses(AddressID),
);
INSERT INTO Orders (UserID, Total, BillingAddressID, ShippingAddressID, PaymentMethodID) VALUES
(1, 3100.00, 1, 1, 1);

CREATE TABLE Order_Products (
    OrderProductID INT IDENTITY(1,1) PRIMARY KEY,
    OrderID INT NOT NULL,
    ProductID INT NOT NULL,
    Quantity INT NOT NULL DEFAULT 1,
    FOREIGN KEY (OrderID) REFERENCES Orders(OrderID),
    FOREIGN KEY (ProductID) REFERENCES Products(ProductID)
);
INSERT INTO Order_Products (OrderID, ProductID, Quantity) VALUES
(1, 1, 1),
(1, 2, 2);

CREATE TABLE Reviews (
    ReviewID INT IDENTITY(1,1) PRIMARY KEY,
    ProductID INT NOT NULL,
    UserID INT NOT NULL,
    Rating INT CHECK (Rating >= 1 AND Rating <= 5),
    Comment NVARCHAR(MAX),
    CreatedAt DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (ProductID) REFERENCES Products(ProductID),
    FOREIGN KEY (UserID) REFERENCES Users(UserID)
);
INSERT INTO Reviews (ProductID, UserID, Rating, Comment) VALUES
(1, 1, 4, N'Great laptop, fast performance!'),
(2, 1, 5, N'Excellent phone, worth the price!');

CREATE TABLE NewsletterSubscriptions (
    SubscriptionID INT IDENTITY(1,1) PRIMARY KEY,
    Email NVARCHAR(100) UNIQUE NOT NULL,
    SubscribedAt DATETIME DEFAULT GETDATE()
);
INSERT INTO NewsletterSubscriptions (Email) VALUES
('test@example.com');

CREATE TABLE CompareLists (
    CompareListID INT IDENTITY(1,1) PRIMARY KEY,
    UserID INT NOT NULL,
    FOREIGN KEY (UserID) REFERENCES Users(UserID)
);
INSERT INTO CompareLists (UserID) VALUES (1);

CREATE TABLE CompareList_Products (
    CompareListProductID INT IDENTITY(1,1) PRIMARY KEY,
    CompareListID INT NOT NULL,
    ProductID INT NOT NULL,
    FOREIGN KEY (CompareListID) REFERENCES CompareLists(CompareListID),
    FOREIGN KEY (ProductID) REFERENCES Products(ProductID)
);
INSERT INTO CompareList_Products (CompareListID, ProductID) VALUES
(1, 1),
(1, 2);

CREATE TABLE Promotions (
    PromotionID INT IDENTITY(1,1) PRIMARY KEY,
    Name NVARCHAR(100) NOT NULL,
    Discount DECIMAL(5, 2) CHECK (Discount >= 0 AND Discount <= 100),
    StartDate DATETIME NOT NULL,
    EndDate DATETIME NOT NULL,
    Description NVARCHAR(MAX)
);
INSERT INTO Promotions (Name, Discount, StartDate, EndDate, Description) VALUES
('Black Friday Sale', 20.00, '2025-11-01 00:00:00', '2025-11-30 23:59:59', '20% off on all products');

CREATE TABLE Promotion_Products (
    PromotionProductID INT IDENTITY(1,1) PRIMARY KEY,
    PromotionID INT NOT NULL,
    ProductID INT NOT NULL,
    FOREIGN KEY (PromotionID) REFERENCES Promotions(PromotionID),
    FOREIGN KEY (ProductID) REFERENCES Products(ProductID)
);
INSERT INTO Promotion_Products (PromotionID, ProductID) VALUES
(1, 1),
(1, 2);

CREATE TABLE Notifications (
    NotificationID INT IDENTITY(1,1) PRIMARY KEY,
    UserID INT NOT NULL,
    Message NVARCHAR(MAX) NOT NULL,
    Type NVARCHAR(20) CHECK (Type IN ('Order', 'Promotion')),
    IsRead BIT DEFAULT 0,
    CreatedAt DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (UserID) REFERENCES Users(UserID)
);
INSERT INTO Notifications (UserID, Message, Type) VALUES
(1, 'Your order #1 has been shipped', 'Order');

CREATE TABLE SupportRequests (
    RequestID INT IDENTITY(1,1) PRIMARY KEY,
    UserID INT NOT NULL,
    Subject NVARCHAR(100) NOT NULL,
    Message NVARCHAR(MAX) NOT NULL,
    Status NVARCHAR(20) CHECK (Status IN ('Open', 'Resolved')) DEFAULT 'Open',
    CreatedAt DATETIME DEFAULT GETDATE(),
    ResolvedAt DATETIME,
    FOREIGN KEY (UserID) REFERENCES Users(UserID)
);
INSERT INTO SupportRequests (UserID, Subject, Message) VALUES
(1, 'Issue with order #1', 'Order not delivered yet');

CREATE TABLE SocialShares (
    ShareID INT IDENTITY(1,1) PRIMARY KEY,
    ProductID INT NOT NULL,
    UserID INT,
    Platform NVARCHAR(20) CHECK (Platform IN ('Facebook', 'Twitter', 'Instagram')),
    SharedAt DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (ProductID) REFERENCES Products(ProductID),
    FOREIGN KEY (UserID) REFERENCES Users(UserID)
);
INSERT INTO SocialShares (ProductID, UserID, Platform) VALUES
(1, 1, 'Facebook');

CREATE TABLE Payments (
    PaymentID INT IDENTITY(1,1) PRIMARY KEY,
    OrderID INT NOT NULL,
    PaymentMethodID INT NOT NULL,
    Amount DECIMAL(10, 2) NOT NULL,
    Status NVARCHAR(20) CHECK (Status IN ('Pending', 'Completed', 'Failed', 'Refunded')) DEFAULT 'Pending',
    TransactionID NVARCHAR(100),
    PaymentDate DATETIME,
    CreatedAt DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (OrderID) REFERENCES Orders(OrderID),
    FOREIGN KEY (PaymentMethodID) REFERENCES PaymentMethods(PaymentMethodID)
);
INSERT INTO Payments (OrderID, PaymentMethodID, Amount, Status, TransactionID, PaymentDate) VALUES
(1, 1, 3100.00, 'Completed', 'TXN123456789', '2025-06-06 14:30:00');
select *from Users
where [Email] = 'nguyenvana@example.com'
and Password = 'password123'