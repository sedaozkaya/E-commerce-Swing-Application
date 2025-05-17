package dao;

import model.Order;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    public boolean addOrder(Order order) {
        String sql = "INSERT INTO orders(user_id, product_id, card_id, quantity) VALUES(?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, order.getUser().getUserId());
            pstmt.setInt(2, order.getProduct().getProductId());
            pstmt.setInt(3, order.getCreditCard().getCardId());
            pstmt.setInt(4, order.getQuantity());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public List<Order> getOrdersByUser(int userId) {
        String sql = "SELECT o.*, p.*, c.* FROM orders o " +
                      "JOIN products p ON o.product_id = p.product_id " +
                      "JOIN credit_cards c ON o.card_id = c.card_id " +
                      "WHERE o.user_id = ?";
        List<Order> orders = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                // Burada User, Product ve CreditCard nesneleri oluşturulmalı
                // Basitlik için şimdilik null bırakıyorum
                Order order = new Order(null, null, null, rs.getInt("quantity"));
                order.setOrderId(rs.getInt("order_id"));
                order.setOrderDate(rs.getTimestamp("order_date"));
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }
}