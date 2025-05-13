package ui;

import dao.UserDAO;
import dao.ProductDAO;
import dao.CreditCardDAO;
import dao.OrderDAO;
import model.User;
import model.Product;
import model.CreditCard;
import model.Order;
import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Main extends JFrame {
    private Connection connection;
    private UserDAO userDAO;
    private ProductDAO productDAO;
    private CreditCardDAO creditCardDAO;
    private OrderDAO orderDAO;

    private JTextField usernameField, nameField, surnameField, emailField, passwordField, homeAddressField, workAddressField;
    private JTextField birthDateField;
    private JTextField productNameField, productColorField, categoryField, stockField, weightField, descriptionField;
    private JTextField cardNumberField, securityCodeField, expDateField;
    private JTextArea productListArea;
    private JTextField orderQuantityField, orderUserIdField, orderProductIdField, orderCardIdField;

    public Main() {
        System.out.println("Main sınıfı başlatılıyor...");
        // Veritabanı bağlantısı
        try {
            Class.forName("org.postgresql.Driver");
            System.out.println("JDBC sürücüsü yüklendi.");
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/ecommerce", "postgres", "oop123");
            System.out.println("Veritabanına bağlanıldı.");
            userDAO = new UserDAO(connection);
            productDAO = new ProductDAO(connection);
            creditCardDAO = new CreditCardDAO(connection);
            orderDAO = new OrderDAO(connection);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("Hata: PostgreSQL JDBC sürücüsü bulunamadı!");
            JOptionPane.showMessageDialog(this, "PostgreSQL JDBC sürücüsü bulunamadı!");
            System.exit(1);
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Hata: Veritabanı bağlantısı başarısız: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Veritabanı bağlantısı başarısız: " + e.getMessage());
            System.exit(1);
        }

        // Arayüz ayarları
        System.out.println("Arayüz ayarları yapılandırılıyor...");
        setTitle("E-Ticaret Uygulaması");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        // Kullanıcı Ekleme Paneli
        JLabel userLabel = new JLabel("Kullanıcı Ekle");
        userLabel.setBounds(20, 10, 100, 25);
        add(userLabel);

        usernameField = new JTextField();
        usernameField.setBounds(20, 40, 150, 25);
        usernameField.setText("Kullanıcı Adı");
        add(usernameField);

        nameField = new JTextField();
        nameField.setBounds(20, 70, 150, 25);
        nameField.setText("Ad");
        add(nameField);

        surnameField = new JTextField();
        surnameField.setBounds(20, 100, 150, 25);
        surnameField.setText("Soyad");
        add(surnameField);

        birthDateField = new JTextField();
        birthDateField.setBounds(20, 130, 150, 25);
        birthDateField.setText("YYYY-MM-DD");
        add(birthDateField);

        emailField = new JTextField();
        emailField.setBounds(20, 160, 150, 25);
        emailField.setText("E-posta");
        add(emailField);

        passwordField = new JTextField();
        passwordField.setBounds(20, 190, 150, 25);
        passwordField.setText("Şifre");
        add(passwordField);

        homeAddressField = new JTextField();
        homeAddressField.setBounds(20, 220, 150, 25);
        homeAddressField.setText("Ev Adresi");
        add(homeAddressField);

        workAddressField = new JTextField();
        workAddressField.setBounds(20, 250, 150, 25);
        workAddressField.setText("İş Adresi");
        add(workAddressField);

        JButton addUserButton = new JButton("Kullanıcı Ekle");
        addUserButton.setBounds(20, 280, 150, 25);
        addUserButton.addActionListener(e -> addUser());
        add(addUserButton);

        // Ürün Ekleme Paneli
        JLabel productLabel = new JLabel("Ürün Ekle");
        productLabel.setBounds(200, 10, 100, 25);
        add(productLabel);

        productNameField = new JTextField();
        productNameField.setBounds(200, 40, 150, 25);
        productNameField.setText("Ürün Adı");
        add(productNameField);

        productColorField = new JTextField();
        productColorField.setBounds(200, 70, 150, 25);
        productColorField.setText("Renk");
        add(productColorField);

        categoryField = new JTextField();
        categoryField.setBounds(200, 100, 150, 25);
        categoryField.setText("Kategori");
        add(categoryField);

        stockField = new JTextField();
        stockField.setBounds(200, 130, 150, 25);
        stockField.setText("Stok");
        add(stockField);

        weightField = new JTextField();
        weightField.setBounds(200, 160, 150, 25);
        weightField.setText("Ağırlık");
        add(weightField);

        descriptionField = new JTextField();
        descriptionField.setBounds(200, 190, 150, 25);
        descriptionField.setText("Açıklama");
        add(descriptionField);

        JButton addProductButton = new JButton("Ürün Ekle");
        addProductButton.setBounds(200, 220, 150, 25);
        addProductButton.addActionListener(e -> addProduct());
        add(addProductButton);

        // Kredi Kartı Ekleme Paneli
        JLabel cardLabel = new JLabel("Kredi Kartı Ekle");
        cardLabel.setBounds(380, 10, 100, 25);
        add(cardLabel);

        cardNumberField = new JTextField();
        cardNumberField.setBounds(380, 40, 150, 25);
        cardNumberField.setText("Kart Numarası");
        add(cardNumberField);

        securityCodeField = new JTextField();
        securityCodeField.setBounds(380, 70, 150, 25);
        securityCodeField.setText("Güvenlik Kodu");
        add(securityCodeField);

        expDateField = new JTextField();
        expDateField.setBounds(380, 100, 150, 25);
        expDateField.setText("YYYY-MM-DD");
        add(expDateField);

        JButton addCardButton = new JButton("Kart Ekle");
        addCardButton.setBounds(380, 130, 150, 25);
        addCardButton.addActionListener(e -> addCreditCard());
        add(addCardButton);

        // Ürün Listeleme Paneli
        JLabel listLabel = new JLabel("Ürünler");
        listLabel.setBounds(20, 310, 100, 25);
        add(listLabel);

        productListArea = new JTextArea();
        productListArea.setBounds(20, 340, 510, 150);
        productListArea.setEditable(false);
        add(productListArea);

        JButton listProductsButton = new JButton("Ürünleri Listele");
        listProductsButton.setBounds(20, 500, 150, 25);
        listProductsButton.addActionListener(e -> listProducts());
        add(listProductsButton);

        // Sipariş Oluşturma Paneli
        JLabel orderLabel = new JLabel("Sipariş Oluştur");
        orderLabel.setBounds(560, 10, 100, 25);
        add(orderLabel);

        orderUserIdField = new JTextField();
        orderUserIdField.setBounds(560, 40, 150, 25);
        orderUserIdField.setText("Kullanıcı ID");
        add(orderUserIdField);

        orderProductIdField = new JTextField();
        orderProductIdField.setBounds(560, 70, 150, 25);
        orderProductIdField.setText("Ürün ID");
        add(orderProductIdField);

        orderCardIdField = new JTextField();
        orderCardIdField.setBounds(560, 100, 150, 25);
        orderCardIdField.setText("Kart ID");
        add(orderCardIdField);

        orderQuantityField = new JTextField();
        orderQuantityField.setBounds(560, 130, 150, 25);
        orderQuantityField.setText("Miktar");
        add(orderQuantityField);

        JButton createOrderButton = new JButton("Sipariş Oluştur");
        createOrderButton.setBounds(560, 160, 150, 25);
        createOrderButton.addActionListener(e -> createOrder());
        add(createOrderButton);

        System.out.println("Arayüz bileşenleri eklendi, görünür yapılıyor...");
        setVisible(true);
    }

    private void addUser() {
        try {
            System.out.println("Kullanıcı ekleme işlemi başlatılıyor...");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            User user = new User(
                    usernameField.getText(),
                    nameField.getText(),
                    surnameField.getText(),
                    sdf.parse(birthDateField.getText()),
                    passwordField.getText(),
                    emailField.getText(),
                    homeAddressField.getText(),
                    workAddressField.getText()
            );
            if (userDAO.addUser(user)) {
                JOptionPane.showMessageDialog(this, "Kullanıcı eklendi!");
                System.out.println("Kullanıcı başarıyla eklendi: " + user.getUsername());
            } else {
                JOptionPane.showMessageDialog(this, "Kullanıcı eklenemedi!");
                System.err.println("Kullanıcı eklenemedi!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Kullanıcı ekleme hatası: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Hata: " + e.getMessage());
        }
    }

    private void addProduct() {
        try {
            System.out.println("Ürün ekleme işlemi başlatılıyor...");
            Product product = new Product(
                    productNameField.getText(),
                    productColorField.getText(),
                    categoryField.getText(),
                    Integer.parseInt(stockField.getText()),
                    Double.parseDouble(weightField.getText()),
                    descriptionField.getText()
            );
            if (productDAO.addProduct(product)) {
                JOptionPane.showMessageDialog(this, "Ürün eklendi!");
                System.out.println("Ürün başarıyla eklendi: " + product.getProductName());
            } else {
                JOptionPane.showMessageDialog(this, "Ürün eklenemedi!");
                System.err.println("Ürün eklenemedi!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Ürün ekleme hatası: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Hata: " + e.getMessage());
        }
    }

    private void addCreditCard() {
        try {
            System.out.println("Kredi kartı ekleme işlemi başlatılıyor...");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            User user = userDAO.getUserByUsername("testuser");
            if (user == null) {
                JOptionPane.showMessageDialog(this, "Hata: Test kullanıcısı bulunamadı!");
                System.err.println("Hata: Test kullanıcısı bulunamadı!");
                return;
            }
            CreditCard card = new CreditCard(
                    cardNumberField.getText(),
                    user,
                    securityCodeField.getText(),
                    sdf.parse(expDateField.getText())
            );
            if (creditCardDAO.addCreditCard(card)) {
                JOptionPane.showMessageDialog(this, "Kredi kartı eklendi!");
                System.out.println("Kredi kartı başarıyla eklendi.");
            } else {
                JOptionPane.showMessageDialog(this, "Kredi kartı eklenemedi!");
                System.err.println("Kredi kartı eklenemedi!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Kredi kartı ekleme hatası: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Hata: " + e.getMessage());
        }
    }

    private void listProducts() {
        try {
            System.out.println("Ürünler listeleniyor...");
            ArrayList<Product> products = productDAO.getAllProducts();
            StringBuilder sb = new StringBuilder();
            for (Product p : products) {
                sb.append("ID: ").append(p.getProductId())
                        .append(", Ad: ").append(p.getProductName())
                        .append(", Renk: ").append(p.getProductColor())
                        .append(", Stok: ").append(p.getProductStock())
                        .append("\n");
            }
            productListArea.setText(sb.toString());
            System.out.println("Ürünler başarıyla listelendi.");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Ürün listeleme hatası: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Hata: Ürünler listelenemedi!");
        }
    }

    private void createOrder() {
        try {
            System.out.println("Sipariş oluşturma işlemi başlatılıyor...");
            int userId = Integer.parseInt(orderUserIdField.getText());
            int productId = Integer.parseInt(orderProductIdField.getText());
            int cardId = Integer.parseInt(orderCardIdField.getText());
            int quantity = Integer.parseInt(orderQuantityField.getText());

            User user = userDAO.getUserById(userId);
            ArrayList<Product> products = productDAO.getAllProducts();
            Product product = products.stream()
                    .filter(p -> p.getProductId() == productId)
                    .findFirst()
                    .orElse(null);
            CreditCard card = creditCardDAO.getCreditCardById(cardId);

            if (user == null || product == null || card == null) {
                JOptionPane.showMessageDialog(this, "Hata: Kullanıcı, ürün veya kart bulunamadı!");
                System.err.println("Hata: Kullanıcı, ürün veya kart bulunamadı!");
                return;
            }

            Order order = new Order(user, product, card, quantity);
            if (order.processOrder() && orderDAO.createOrder(order)) {
                JOptionPane.showMessageDialog(this, "Sipariş oluşturuldu!");
                System.out.println("Sipariş başarıyla oluşturuldu.");
            } else {
                JOptionPane.showMessageDialog(this, "Sipariş oluşturulamadı!");
                System.err.println("Sipariş oluşturulamadı!");
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            System.err.println("Hata: Geçersiz sayı formatı: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Hata: Geçersiz sayı formatı!");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Sipariş oluşturma hatası: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Hata: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
    System.out.println("Program başlatılıyor...");
    SwingUtilities.invokeLater(() -> {
        System.out.println("Arayüz başlatılıyor...");
        Main main = new Main(); // Dönen değeri değişkene ata
    });
}
}