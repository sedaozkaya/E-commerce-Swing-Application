package ui;

import dao.UserDAO;
import dao.SellerDAO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import ui.SellerDashboard;
import ui.UserDashboard;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, registerButton, sellerLoginButton;
    
    public LoginFrame() {
        setTitle("E-Ticaret Giriş");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        panel.add(new JLabel("Kullanıcı Adı:"));
        usernameField = new JTextField();
        panel.add(usernameField);
        
        panel.add(new JLabel("Şifre:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);
        
        loginButton = new JButton("Giriş Yap");
        panel.add(loginButton);
        
        registerButton = new JButton("Kayıt Ol");
        panel.add(registerButton);
        
        sellerLoginButton = new JButton("Satıcı Girişi");
        panel.add(sellerLoginButton);
        
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                
                UserDAO userDAO = new UserDAO();
                if (userDAO.validateUser(username, password)) {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Giriş başarılı!");
                    new UserDashboard(username).setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Geçersiz kullanıcı adı veya şifre!", "Hata", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        sellerLoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                
                SellerDAO sellerDAO = new SellerDAO();
                if (sellerDAO.validateSeller(username, password)) {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Satıcı girişi başarılı!");
                    new SellerDashboard().setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Geçersiz satıcı bilgileri!", "Hata", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new RegistrationFrame().setVisible(true);
            }
        });
        
        add(panel);
    }
}