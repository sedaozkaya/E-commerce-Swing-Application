package dao;

import model.Product;
import java.sql.*;
import java.util.ArrayList;

public class ProductDAO {
    private final Connection connection;

    public ProductDAO(Connection connection) {
        this.connection = connection;
    }

    public boolean addProduct(Product product) {
        String query = "INSERT INTO products(product_name, product_color, category, product_stock, product_weight, description) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, product.getProductName());
            stmt.setString(2, product.getProductColor());
            stmt.setString(3, product.getCategory());
            stmt.setInt(4, product.getProductStock());
            stmt.setDouble(5, product.getProductWeight());
            stmt.setString(6, product.getDescription());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                product.setProductId(rs.getInt(1));
            }
            return true;
        } catch (SQLException e) {
            
            return false;
            
        }
    }

    public ArrayList<Product> getAllProducts() {
        String query = "SELECT * FROM products";
        ArrayList<Product> products = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Product product = new Product(
                        rs.getString("product_name"),
                        rs.getString("product_color"),
                        rs.getString("category"),
                        rs.getInt("product_stock"),
                        rs.getDouble("product_weight"),
                        rs.getString("description")
                );
                product.setProductId(rs.getInt("product_id"));
                products.add(product);
            }
        } catch (SQLException e) {
            
        }
        return products;
    }

    public boolean updateProduct(Product product) {
        String query = "UPDATE products SET product_name = ?, product_color = ?, category = ?, product_stock = ?, product_weight = ?, description = ? WHERE product_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, product.getProductName());
            stmt.setString(2, product.getProductColor());
            stmt.setString(3, product.getCategory());
            stmt.setInt(4, product.getProductStock());
            stmt.setDouble(5, product.getProductWeight());
            stmt.setString(6, product.getDescription());
            stmt.setInt(7, product.getProductId());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
           
            return false;
        }
    }

    public boolean deleteProduct(int productId) {
        String query = "DELETE FROM products WHERE product_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, productId);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            
            return false;
        }
    }
}