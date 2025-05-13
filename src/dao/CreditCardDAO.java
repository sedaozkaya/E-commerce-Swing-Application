/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import model.CreditCard;
import model.User;
import java.sql.*;

public class CreditCardDAO {
    private Connection connection;

    public CreditCardDAO(Connection connection) {
        this.connection = connection;
    }

    // Kredi kartı ekle
    public boolean addCreditCard(CreditCard card) {
        String query = "INSERT INTO credit_cards(user_id, card_number, security_code, exp_date) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, card.getUser().getUserId()); // User ID burada kullanılacak
            stmt.setString(2, card.getCardNumber());
            stmt.setString(3, card.getSecurityCode());
            stmt.setDate(4, new java.sql.Date(card.getExpDate().getTime()));
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Kredi kartını ID ile bul
    public CreditCard getCreditCardById(int cardId) {
        String query = "SELECT * FROM credit_cards WHERE card_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, cardId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new CreditCard(
                        rs.getString("card_number"),
                        null,  // User bilgisi başka bir işlemde alınabilir
                        rs.getString("security_code"),
                        rs.getDate("exp_date")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Kredi kartı sil
    public boolean deleteCreditCard(int cardId) {
        String query = "DELETE FROM credit_cards WHERE card_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, cardId);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
