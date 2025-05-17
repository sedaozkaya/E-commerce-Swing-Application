package ui;

import dao.ProductDAO;
import model.Product;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class UserDashboard extends JFrame {
    private String username;
    private JTabbedPane tabbedPane;
    
    public UserDashboard(String username) {
        this.username = username;
        setTitle("Kullanıcı Paneli - " + username);
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        tabbedPane = new JTabbedPane();
        
        // Ürünler sekmesi
        JPanel productsPanel = new JPanel(new BorderLayout());
        JComboBox<String> categoryCombo = new JComboBox<>(new String[]{"Tümü", "Elektronik", "Giyim", "Kitap", "Spor"});
        JButton filterButton = new JButton("Filtrele");
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Kategori:"));
        filterPanel.add(categoryCombo);
        filterPanel.add(filterButton);
        productsPanel.add(filterPanel, BorderLayout.NORTH);
        
        DefaultListModel<Product> productListModel = new DefaultListModel<>();
        JList<Product> productList = new JList<>(productListModel);
        productList.setCellRenderer(new ProductListRenderer());
        JScrollPane scrollPane = new JScrollPane(productList);
        productsPanel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel productButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addToCartButton = new JButton("Sepete Ekle");
        JButton addToFavoritesButton = new JButton("Favorilere Ekle");
        productButtonPanel.add(addToCartButton);
        productButtonPanel.add(addToFavoritesButton);
        productsPanel.add(productButtonPanel, BorderLayout.SOUTH);
        
        tabbedPane.addTab("Ürünler", productsPanel);
        
        // Favoriler sekmesi
        JPanel favoritesPanel = new JPanel(new BorderLayout());
        DefaultListModel<Product> favoritesListModel = new DefaultListModel<>();
        JList<Product> favoritesList = new JList<>(favoritesListModel);
        favoritesList.setCellRenderer(new ProductListRenderer());
        JScrollPane favoritesScrollPane = new JScrollPane(favoritesList);
        favoritesPanel.add(favoritesScrollPane, BorderLayout.CENTER);
        
        JPanel favoritesButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton removeFavoriteButton = new JButton("Favoriden Çıkar");
        favoritesButtonPanel.add(removeFavoriteButton);
        favoritesPanel.add(favoritesButtonPanel, BorderLayout.SOUTH);
        
        tabbedPane.addTab("Favoriler", favoritesPanel);
        
        // Siparişler sekmesi
        JPanel ordersPanel = new JPanel(new BorderLayout());
        DefaultListModel<String> ordersListModel = new DefaultListModel<>();
        JList<String> ordersList = new JList<>(ordersListModel);
        JScrollPane ordersScrollPane = new JScrollPane(ordersList);
        ordersPanel.add(ordersScrollPane, BorderLayout.CENTER);
        
        tabbedPane.addTab("Siparişler", ordersPanel);
        
        // Kredi kartları sekmesi
        JPanel cardsPanel = new JPanel(new BorderLayout());
        DefaultListModel<String> cardsListModel = new DefaultListModel<>();
        JList<String> cardsList = new JList<>(cardsListModel);
        JScrollPane cardsScrollPane = new JScrollPane(cardsList);
        cardsPanel.add(cardsScrollPane, BorderLayout.CENTER);
        
        JPanel cardsButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addCardButton = new JButton("Kart Ekle");
        cardsButtonPanel.add(addCardButton);
        cardsPanel.add(cardsButtonPanel, BorderLayout.SOUTH);
        
        tabbedPane.addTab("Kredi Kartları", cardsPanel);
        
        add(tabbedPane);
        
        // Filtreleme butonu işlevi
        filterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedCategory = (String) categoryCombo.getSelectedItem();
                ProductDAO productDAO = new ProductDAO();
                List<Product> products;
                
                if ("Tümü".equals(selectedCategory)) {
                    products = productDAO.getAllProducts();
                } else {
                    products = productDAO.getProductsByCategory(selectedCategory);
                }
                
                productListModel.clear();
                for (Product product : products) {
                    productListModel.addElement(product);
                }
            }
        });
        
        // Ürün listesini başlangıçta doldur
        filterButton.doClick();
    }
    
    // Ürünleri güzel göstermek için özel renderer
    private class ProductListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
                                                     boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof Product) {
                Product product = (Product) value;
                setText(String.format("%s - %s - %.2f TL - Stok: %d", 
                    product.getProductName(), 
                    product.getCategory(),
                    product.getProductWeight() * 100, // Fiyatı ağırlığa göre hesaplıyoruz
                    product.getProductStock()));
            }
            
            return this;
        }
    }
}