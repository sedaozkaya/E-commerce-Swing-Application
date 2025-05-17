package ui;

import dao.ProductDAO;
import model.Product;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SellerDashboard extends JFrame {
    private JTextField nameField, colorField, categoryField, stockField, weightField;
    private JTextArea descriptionArea;
    private JButton addProductButton;
    
    public SellerDashboard() {
        setTitle("Satıcı Paneli");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Menü çubuğu ekleme
        JMenuBar menuBar = new JMenuBar();
        JMenu sellerMenu = new JMenu("Hesap");
        JMenuItem logoutItem = new JMenuItem("Çıkış Yap");
        
        logoutItem.addActionListener(e -> logout());
        sellerMenu.add(logoutItem);
        menuBar.add(sellerMenu);
        setJMenuBar(menuBar);
        
        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        panel.add(new JLabel("Ürün Adı:"));
        nameField = new JTextField();
        panel.add(nameField);
        
        panel.add(new JLabel("Renk:"));
        colorField = new JTextField();
        panel.add(colorField);
        
        panel.add(new JLabel("Kategori:"));
        categoryField = new JTextField();
        panel.add(categoryField);
        
        panel.add(new JLabel("Stok:"));
        stockField = new JTextField();
        panel.add(stockField);
        
        panel.add(new JLabel("Ağırlık (kg):"));
        weightField = new JTextField();
        panel.add(weightField);
        
        panel.add(new JLabel("Açıklama:"));
        descriptionArea = new JTextArea(3, 20);
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        panel.add(scrollPane);
        
        addProductButton = new JButton("Ürün Ekle");
        panel.add(addProductButton);
        
        // Çıkış butonunu ekleyelim
        JButton logoutButton = new JButton("Çıkış Yap");
        logoutButton.addActionListener(e -> logout());
        panel.add(logoutButton);
        
        addProductButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String name = nameField.getText();
                    String color = colorField.getText();
                    String category = categoryField.getText();
                    int stock = Integer.parseInt(stockField.getText());
                    double weight = Double.parseDouble(weightField.getText());
                    String description = descriptionArea.getText();
                    
                    Product product = new Product(name, color, category, stock, weight, description);
                    
                    ProductDAO productDAO = new ProductDAO();
                    if (productDAO.addProduct(product)) {
                        JOptionPane.showMessageDialog(SellerDashboard.this, "Ürün başarıyla eklendi!");
                        clearFields();
                    } else {
                        JOptionPane.showMessageDialog(SellerDashboard.this, "Ürün eklenirken bir hata oluştu!", "Hata", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(SellerDashboard.this, "Geçersiz sayı formatı!", "Hata", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        add(panel);
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Çıkış yapmak istediğinize emin misiniz?",
            "Çıkış Onayı",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            dispose(); // Mevcut pencereyi kapat
            new LoginFrame().setVisible(true); // Giriş ekranını aç
        }
    }
    
    private void clearFields() {
        nameField.setText("");
        colorField.setText("");
        categoryField.setText("");
        stockField.setText("");
        weightField.setText("");
        descriptionArea.setText("");
    }
}