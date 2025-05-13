package dao;

import model.Order;
import model.User;
import model.Product;
import model.CreditCard;
import java.sql.*;
import java.util.ArrayList;

public class OrderDAO {
    private Connection connection;
    private UserDAO userDAO;
    private ProductDAO productDAO;
    private CreditCardDAO creditCardDAO;

    public OrderDAO(Connection connection) {
        this.connection = connection;
        this.userDAO = new UserDAO(connection);
        this.productDAO = new ProductDAO(connection);
        this.creditCardDAO = new CreditCardDAO(connection);
    }

    public boolean createOrder(Order order) {
        String query = "INSERT INTO orders(user_id, product_id, card_id, quantity, order_date) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, order.getUser().getUserId());
            stmt.setInt(2, order.getProduct().getProductId());
            stmt.setInt(3, order.getCreditCard().getCardId());
            stmt.setInt(4, order.getQuantity());
            stmt.setTimestamp(5, new Timestamp(order.getOrderDate().getTime()));
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                order.setOrderId(rs.getInt(1));
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<Order> getAllOrders() {
        String query = "SELECT * FROM orders";
        ArrayList<Order> orders = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                int productId = rs.getInt("product_id");
                User user = userDAO.getUserById(rs.getInt("user_id"));
                // Ürün listesini al ve product_id ile eşleşeni bul
                ArrayList<Product> products = productDAO.getAllProducts();
                Product product = products.stream()
                        .filter(p -> p.getProductId() == productId)
                        .findFirst()
                        .orElse(null);
                CreditCard card = creditCardDAO.getCreditCardById(rs.getInt("card_id"));
                if (user == null || product == null || card == null) {
                    System.err.println("Hata: Sipariş için gerekli veri eksik (user_id: " + rs.getInt("user_id") +
                            ", product_id: " + productId + ", card_id: " + rs.getInt("card_id") + ")");
                    continue; // Geçersiz siparişi atla
                }
                Order order = new Order(user, product, card, rs.getInt("quantity"));
                order.setOrderId(rs.getInt("order_id"));
                order.setOrderDate(rs.getTimestamp("order_date"));
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("SQL Hatası: " + e.getMessage());
        }
        return orders;
    }
}