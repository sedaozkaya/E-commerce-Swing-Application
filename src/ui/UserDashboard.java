package ui;

import dao.*;
import model.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Date;

public class UserDashboard extends JFrame {
    private String username;
    private User currentUser;
    private JTabbedPane tabbedPane;
    private ProductDAO productDAO = new ProductDAO();
    private OrderDAO orderDAO = new OrderDAO();
    private CreditCardDAO creditCardDAO = new CreditCardDAO();
    private FavoriteDAO favoriteDAO = new FavoriteDAO();
    
    public UserDashboard(String username) {
        this.username = username;
        this.currentUser = new UserDAO().getUserByUsername(username);
        
        setTitle("Kullanıcı Paneli - " + username);
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Menü çubuğu
        JMenuBar menuBar = new JMenuBar();
        JMenu userMenu = new JMenu("Hesap");
        JMenuItem logoutItem = new JMenuItem("Çıkış Yap");
        logoutItem.addActionListener(e -> logout());
        userMenu.add(logoutItem);
        menuBar.add(userMenu);
        setJMenuBar(menuBar);
        
        tabbedPane = new JTabbedPane();
        
        // Ürünler sekmesi
        JPanel productsPanel = createProductsPanel();
        tabbedPane.addTab("Ürünler", productsPanel);
        
        // Favoriler sekmesi
        JPanel favoritesPanel = createFavoritesPanel();
        tabbedPane.addTab("Favoriler", favoritesPanel);
        
        // Siparişler sekmesi
        JPanel ordersPanel = createOrdersPanel();
        tabbedPane.addTab("Siparişler", ordersPanel);
        
        // Kredi kartları sekmesi
        JPanel cardsPanel = createCreditCardsPanel();
        tabbedPane.addTab("Kredi Kartları", cardsPanel);
        
        add(tabbedPane);
    }
    
    private JPanel createProductsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Filtreleme
        JComboBox<String> categoryCombo = new JComboBox<>(new String[]{"Tümü", "Elektronik", "Giyim", "Kitap", "Spor"});
        JButton filterButton = new JButton("Filtrele");
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Kategori:"));
        filterPanel.add(categoryCombo);
        filterPanel.add(filterButton);
        panel.add(filterPanel, BorderLayout.NORTH);
        
        // Ürün listesi
        DefaultListModel<Product> productListModel = new DefaultListModel<>();
        JList<Product> productList = new JList<>(productListModel);
        productList.setCellRenderer(new ProductListRenderer());
        JScrollPane scrollPane = new JScrollPane(productList);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Butonlar
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton orderButton = new JButton("Sipariş Et");
        JButton addFavoriteButton = new JButton("Favorilere Ekle");
        JButton logoutButton = new JButton("Çıkış Yap");
        
        buttonPanel.add(orderButton);
        buttonPanel.add(addFavoriteButton);
        buttonPanel.add(logoutButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Filtreleme işlevi
        filterButton.addActionListener(e -> {
            String category = (String) categoryCombo.getSelectedItem();
            List<Product> products = category.equals("Tümü") ? 
                productDAO.getAllProducts() : productDAO.getProductsByCategory(category);
            
            productListModel.clear();
            products.forEach(productListModel::addElement);
        });
        
        // Sipariş işlevi
        orderButton.addActionListener(e -> {
            Product selected = productList.getSelectedValue();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Lütfen bir ürün seçiniz!");
                return;
            }
            
            // Kredi kartı seçimi
            List<CreditCard> cards = creditCardDAO.getCreditCardsByUser(currentUser.getUserId());
            if (cards.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Sipariş için önce bir kredi kartı eklemelisiniz!");
                return;
            }
            
            CreditCard selectedCard = (CreditCard) JOptionPane.showInputDialog(
                this, "Kredi Kartı Seçin:", "Kart Seçimi",
                JOptionPane.QUESTION_MESSAGE, null, 
                cards.toArray(), cards.get(0));
            
            if (selectedCard != null) {
                Order order = new Order(currentUser, selected, selectedCard, 1);
                if (orderDAO.addOrder(order) && productDAO.updateProductStock(selected.getProductId(), 1)) {
                    JOptionPane.showMessageDialog(this, "Sipariş başarıyla oluşturuldu!");
                    filterButton.doClick(); // Listeyi yenile
                } else {
                    JOptionPane.showMessageDialog(this, "Sipariş oluşturulamadı!", "Hata", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        // Favori ekleme işlevi
        addFavoriteButton.addActionListener(e -> {
            Product selected = productList.getSelectedValue();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Lütfen bir ürün seçiniz!");
                return;
            }
            
            if (favoriteDAO.addFavorite(currentUser.getUserId(), selected.getProductId())) {
                JOptionPane.showMessageDialog(this, "Ürün favorilere eklendi!");
            } else {
                JOptionPane.showMessageDialog(this, "Bu ürün zaten favorilerinizde!", "Uyarı", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        // Çıkış işlevi
        logoutButton.addActionListener(e -> logout());
        
        // Başlangıçta ürünleri yükle
        filterButton.doClick();
        
        return panel;
    }
    
    private JPanel createFavoritesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        DefaultListModel<Product> model = new DefaultListModel<>();
        JList<Product> favoritesList = new JList<>(model);
        favoritesList.setCellRenderer(new ProductListRenderer());
        
        // Favorileri yükle
        favoriteDAO.getFavoriteProductIds(currentUser.getUserId()).forEach(id -> {
            Product p = productDAO.getProductById(id);
            if (p != null) model.addElement(p);
        });
        
        JScrollPane scrollPane = new JScrollPane(favoritesList);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Butonlar
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton removeButton = new JButton("Favoriden Çıkar");
        JButton orderButton = new JButton("Sipariş Et");
        
        removeButton.addActionListener(e -> {
            Product selected = favoritesList.getSelectedValue();
            if (selected != null && favoriteDAO.removeFavorite(currentUser.getUserId(), selected.getProductId())) {
                model.removeElement(selected);
                JOptionPane.showMessageDialog(this, "Ürün favorilerden çıkarıldı!");
            }
        });
        
        orderButton.addActionListener(e -> {
            Product selected = favoritesList.getSelectedValue();
            if (selected != null) {
                List<CreditCard> cards = creditCardDAO.getCreditCardsByUser(currentUser.getUserId());
                if (cards.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Sipariş için önce bir kredi kartı eklemelisiniz!");
                    return;
                }
                
                CreditCard selectedCard = (CreditCard) JOptionPane.showInputDialog(
                    this, "Kredi Kartı Seçin:", "Kart Seçimi",
                    JOptionPane.QUESTION_MESSAGE, null, 
                    cards.toArray(), cards.get(0));
                
                if (selectedCard != null) {
                    Order order = new Order(currentUser, selected, selectedCard, 1);
                    if (orderDAO.addOrder(order) && productDAO.updateProductStock(selected.getProductId(), 1)) {
                        JOptionPane.showMessageDialog(this, "Sipariş başarıyla oluşturuldu!");
                        // Favorileri yenile
                        model.clear();
                        favoriteDAO.getFavoriteProductIds(currentUser.getUserId()).forEach(id -> {
                            Product p = productDAO.getProductById(id);
                            if (p != null) model.addElement(p);
                        });
                    } else {
                        JOptionPane.showMessageDialog(this, "Sipariş oluşturulamadı!", "Hata", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        
        buttonPanel.add(removeButton);
        buttonPanel.add(orderButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createOrdersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        DefaultListModel<String> model = new DefaultListModel<>();
        JList<String> ordersList = new JList<>(model);
        
        // Siparişleri yükle
        orderDAO.getOrdersByUser(currentUser.getUserId()).forEach(order -> {
            model.addElement(String.format("Sipariş #%d - %s - %s", 
                order.getOrderId(), 
                order.getProduct().getProductName(),
                order.getOrderDate()));
        });
        
        JScrollPane scrollPane = new JScrollPane(ordersList);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createCreditCardsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        DefaultListModel<CreditCard> model = new DefaultListModel<>();
        JList<CreditCard> cardsList = new JList<>(model);
        
        // Kartları yükle
        creditCardDAO.getCreditCardsByUser(currentUser.getUserId()).forEach(model::addElement);
        
        cardsList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
                                                         boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof CreditCard) {
                    CreditCard card = (CreditCard) value;
                    setText(String.format("•••• •••• •••• %s - %s", 
                        card.getCardNumber().substring(12),
                        card.getExpDate()));
                }
                return this;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(cardsList);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Butonlar
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Kart Ekle");
        
        addButton.addActionListener(e -> {
            JTextField cardNumberField = new JTextField(16);
            JTextField securityCodeField = new JTextField(3);
            JTextField expMonthField = new JTextField(2);
            JTextField expYearField = new JTextField(4);
            
            JPanel inputPanel = new JPanel(new GridLayout(4, 2));
            inputPanel.add(new JLabel("Kart Numarası:"));
            inputPanel.add(cardNumberField);
            inputPanel.add(new JLabel("Güvenlik Kodu:"));
            inputPanel.add(securityCodeField);
            inputPanel.add(new JLabel("Son Kullanma Ay:"));
            inputPanel.add(expMonthField);
            inputPanel.add(new JLabel("Son Kullanma Yıl:"));
            inputPanel.add(expYearField);
            
            int result = JOptionPane.showConfirmDialog(
                this, inputPanel, "Yeni Kredi Kartı", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
            if (result == JOptionPane.OK_OPTION) {
                try {
                    String cardNumber = cardNumberField.getText().replaceAll("\\s+", "");
                    String securityCode = securityCodeField.getText();
                    int month = Integer.parseInt(expMonthField.getText());
                    int year = Integer.parseInt(expYearField.getText());
                    
                    if (cardNumber.length() != 16 || !cardNumber.matches("\\d+")) {
                        JOptionPane.showMessageDialog(this, "Geçersiz kart numarası!", "Hata", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    if (securityCode.length() != 3 || !securityCode.matches("\\d+")) {
                        JOptionPane.showMessageDialog(this, "Geçersiz güvenlik kodu!", "Hata", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    @SuppressWarnings("deprecation")
                    Date expDate = new Date(year - 1900, month - 1, 1);
                    
                    CreditCard card = new CreditCard(cardNumber, currentUser, securityCode, expDate);
                    if (creditCardDAO.addCreditCard(card)) {
                        model.addElement(card);
                        JOptionPane.showMessageDialog(this, "Kart başarıyla eklendi!");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Geçersiz tarih formatı!", "Hata", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        buttonPanel.add(addButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(
            this, "Çıkış yapmak istediğinize emin misiniz?", 
            "Çıkış Onayı", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
    
    private class ProductListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
                                                     boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Product) {
                Product p = (Product) value;
                setText(String.format("%s - %s - %.2f TL - Stok: %d", 
                    p.getProductName(), p.getCategory(), 
                    p.getProductWeight() * 100, p.getProductStock()));
            }
            return this;
        }
    }
}