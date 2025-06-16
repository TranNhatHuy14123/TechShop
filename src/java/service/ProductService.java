package service;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import DAO.ProductDAO;
import model.Product;

public class ProductService {
    private static final Logger LOGGER = Logger.getLogger(ProductService.class.getName());
    private ProductDAO productDAO;
    
    public ProductService() {
        this.productDAO = new ProductDAO();
    }
    
    // Constructor for dependency injection (useful for testing)
    public ProductService(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }
    
    /**
     * Create a new product with validation
     */
    public boolean createProduct(Product product) {
        try {
            // Validate product data
            if (!isValidProduct(product)) {
                LOGGER.log(Level.WARNING, "Invalid product data provided");
                return false;
            }
            
            // Business logic: Set default values if needed
            if (product.getRating() == 0) {
                product.setRating(0.0);
            }
            
            if (product.getStockStatus() == null || product.getStockStatus().isEmpty()) {
                product.setStockStatus("In Stock");
            }
            
            productDAO.insertProduct(product);
            LOGGER.log(Level.INFO, "Product created successfully: " + product.getName());
            return true;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating product: " + e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Get product by ID
     */
    public Product getProductById(int productId) {
        if (productId <= 0) {
            LOGGER.log(Level.WARNING, "Invalid product ID: " + productId);
            return null;
        }
        
        try {
            Product product = productDAO.selectProduct(productId);
            if (product == null) {
                LOGGER.log(Level.INFO, "Product not found with ID: " + productId);
            }
            return product;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting product by ID: " + e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Get all products
     */
    public List<Product> getAllProducts() {
        try {
            List<Product> products = productDAO.selectAllProducts();
            LOGGER.log(Level.INFO, "Retrieved " + products.size() + " products");
            return products;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting all products: " + e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Update product with validation
     */
    public boolean updateProduct(Product product) {
        try {
            // Validate product data
            if (!isValidProduct(product) || product.getProductId() <= 0) {
                LOGGER.log(Level.WARNING, "Invalid product data for update");
                return false;
            }
            
            // Check if product exists
            Product existingProduct = productDAO.selectProduct(product.getProductId());
            if (existingProduct == null) {
                LOGGER.log(Level.WARNING, "Product not found for update: " + product.getProductId());
                return false;
            }
            
            boolean updated = productDAO.updateProduct(product);
            if (updated) {
                LOGGER.log(Level.INFO, "Product updated successfully: " + product.getName());
            }
            return updated;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating product: " + e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Delete product with validation
     */
    public boolean deleteProduct(int productId) {
        if (productId <= 0) {
            LOGGER.log(Level.WARNING, "Invalid product ID for deletion: " + productId);
            return false;
        }
        
        try {
            // Check if product exists
            Product existingProduct = productDAO.selectProduct(productId);
            if (existingProduct == null) {
                LOGGER.log(Level.WARNING, "Product not found for deletion: " + productId);
                return false;
            }
            
            boolean deleted = productDAO.deleteProduct(productId);
            if (deleted) {
                LOGGER.log(Level.INFO, "Product deleted successfully: " + productId);
            }
            return deleted;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting product: " + e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Get products with pagination
     */
    public List<Product> getProductsByPage(int page, int size) {
        if (page <= 0 || size <= 0) {
            LOGGER.log(Level.WARNING, "Invalid pagination parameters: page=" + page + ", size=" + size);
            return null;
        }
        
        try {
            List<Product> products = productDAO.selectProductsByPage(page, size);
            LOGGER.log(Level.INFO, "Retrieved " + products.size() + " products for page " + page);
            return products;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting products by page: " + e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Search products by keyword and category
     */
    public List<Product> searchProducts(String keyword, Integer categoryId) {
        if (keyword == null || keyword.trim().isEmpty()) {
            LOGGER.log(Level.WARNING, "Empty search keyword provided");
            return getAllProducts(); // Return all products if no keyword
        }
        
        try {
            List<Product> products = productDAO.searchProducts(keyword.trim(), categoryId);
            LOGGER.log(Level.INFO, "Found " + products.size() + " products for keyword: " + keyword);
            return products;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error searching products: " + e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Get featured products (new or discounted)
     */
    public List<Product> getFeaturedProducts() {
        try {
            List<Product> products = productDAO.getFeaturedProducts();
            LOGGER.log(Level.INFO, "Retrieved " + products.size() + " featured products");
            return products;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting featured products: " + e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Get products by category
     */
    public List<Product> getProductsByCategory(int categoryId) {
        if (categoryId <= 0) {
            LOGGER.log(Level.WARNING, "Invalid category ID: " + categoryId);
            return null;
        }
        
        try {
            List<Product> products = productDAO.searchProducts("", categoryId);
            LOGGER.log(Level.INFO, "Retrieved " + products.size() + " products for category: " + categoryId);
            return products;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting products by category: " + e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Calculate discounted price
     */
    public double calculateDiscountedPrice(Product product) {
        if (product == null || product.getPrice() <= 0) {
            return 0.0;
        }
        
        if (product.getDiscount() != null && product.getDiscount() > 0) {
            double discountAmount = product.getPrice() * product.getDiscount() / 100.0;
            return product.getPrice() - discountAmount;
        }
        
        return product.getPrice();
    }
    
    /**
     * Check if product is in stock
     */
    public boolean isProductInStock(int productId) {
        Product product = getProductById(productId);
        return product != null && "In Stock".equalsIgnoreCase(product.getStockStatus());
    }
    
    /**
     * Get products with discount
     */
    public List<Product> getDiscountedProducts() {
        try {
            List<Product> allProducts = productDAO.selectAllProducts();
            return allProducts.stream()
                    .filter(p -> p.getDiscount() != null && p.getDiscount() > 0)
                    .toList();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting discounted products: " + e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Get new products
     */
    public List<Product> getNewProducts() {
        try {
            List<Product> allProducts = productDAO.selectAllProducts();
            return allProducts.stream()
                    .filter(Product::isNew)
                    .toList();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting new products: " + e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Validate product data
     */
    private boolean isValidProduct(Product product) {
        if (product == null) {
            return false;
        }
        
        // Check required fields
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            LOGGER.log(Level.WARNING, "Product name is required");
            return false;
        }
        
        if (product.getCategoryId() <= 0) {
            LOGGER.log(Level.WARNING, "Valid category ID is required");
            return false;
        }
        
        if (product.getBrandId() <= 0) {
            LOGGER.log(Level.WARNING, "Valid brand ID is required");
            return false;
        }
        
        if (product.getPrice() <= 0) {
            LOGGER.log(Level.WARNING, "Product price must be greater than 0");
            return false;
        }
        
        // Validate discount range
        if (product.getDiscount() != null && (product.getDiscount() < 0 || product.getDiscount() > 100)) {
            LOGGER.log(Level.WARNING, "Discount must be between 0 and 100");
            return false;
        }
        
        // Validate rating range
        if (product.getRating() < 0 || product.getRating() > 5) {
            LOGGER.log(Level.WARNING, "Rating must be between 0 and 5");
            return false;
        }
        
        return true;
    }
}