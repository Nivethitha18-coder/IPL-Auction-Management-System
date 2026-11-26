
package iplauction;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.io.InputStream;
import java.sql.*;

public class LoginFrame extends JFrame {

    private final Color darkBlue = new Color(10, 20, 40);
    private final Color gold = new Color(205, 170, 105);
    private final Color white = Color.WHITE;
    private final Color muted = new Color(195, 200, 210);

    private JTextField usernameField;
    private JPasswordField passwordField;

    private Image backgroundImage;

    public LoginFrame() {

        setTitle("IPL Auction and Player Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 650));
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        loadBackgroundImage();
        createUI();
    }

    private void loadBackgroundImage() {

        try {
            InputStream stream =
                    getClass().getResourceAsStream("/iplauction/stadium.jpg");

            if (stream != null) {
                backgroundImage = javax.imageio.ImageIO.read(stream);
                stream.close();
            }

        } catch (Exception e) {
            System.out.println("Unable to load stadium image: " + e.getMessage());
        }
    }

    private void createUI() {

        JPanel background = new JPanel(new GridBagLayout()) {

            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g.create();

                if (backgroundImage != null) {
                    g2.drawImage(
                            backgroundImage,
                            0, 0,
                            getWidth(), getHeight(),
                            this
                    );
                } else {
                    g2.setColor(darkBlue);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }

                g2.setColor(new Color(5, 12, 27, 175));
                g2.fillRect(0, 0, getWidth(), getHeight());

                g2.dispose();
            }
        };

        background.setBorder(new EmptyBorder(30, 60, 30, 60));

        // LEFT BRANDING

        JPanel branding = new JPanel();
        branding.setOpaque(false);
        branding.setLayout(new BoxLayout(branding, BoxLayout.Y_AXIS));

        JLabel smallTitle = new JLabel("WELCOME TO");
        smallTitle.setFont(new Font("Arial", Font.BOLD, 18));
        smallTitle.setForeground(gold);

        JLabel title1 = new JLabel("IPL AUCTION");
        title1.setFont(new Font("Arial", Font.BOLD, 45));
        title1.setForeground(white);

        JLabel title2 = new JLabel("AND PLAYER");
        title2.setFont(new Font("Arial", Font.BOLD, 45));
        title2.setForeground(white);

        JLabel title3 = new JLabel("MANAGEMENT SYSTEM");
        title3.setFont(new Font("Arial", Font.BOLD, 34));
        title3.setForeground(gold);

        JLabel description = new JLabel(
                "<html>Manage franchises, players and auction activities<br>" +
                "through a centralized auction management portal.</html>"
        );

        description.setFont(new Font("Arial", Font.PLAIN, 16));
        description.setForeground(muted);

        branding.add(smallTitle);
        branding.add(Box.createVerticalStrut(18));
        branding.add(title1);
        branding.add(title2);
        branding.add(title3);
        branding.add(Box.createVerticalStrut(22));
        branding.add(description);

        // LOGIN PANEL

        JPanel loginPanel = new JPanel(new GridBagLayout()) {

            protected void paintComponent(Graphics g) {

                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                g2.setColor(new Color(35, 40, 52, 225));

                g2.fillRoundRect(
                        0, 0,
                        getWidth(), getHeight(),
                        25, 25
                );

                g2.setColor(new Color(205, 170, 105, 190));
                g2.setStroke(new BasicStroke(1.5f));

                g2.drawRoundRect(
                        1, 1,
                        getWidth() - 3,
                        getHeight() - 3,
                        25, 25
                );

                g2.dispose();

                super.paintComponent(g);
            }
        };

        loginPanel.setOpaque(false);
        loginPanel.setPreferredSize(new Dimension(430, 510));
        loginPanel.setBorder(new EmptyBorder(35, 40, 35, 40));

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 0, 5, 0);

        JLabel loginTitle = new JLabel("SIGN IN", SwingConstants.CENTER);
        loginTitle.setFont(new Font("Arial", Font.BOLD, 29));
        loginTitle.setForeground(white);

        c.gridy = 0;
        c.insets = new Insets(0, 0, 5, 0);
        form.add(loginTitle, c);

        JLabel subtitle = new JLabel(
                "Access your auction management account",
                SwingConstants.CENTER
        );

        subtitle.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitle.setForeground(muted);

        c.gridy = 1;
        c.insets = new Insets(0, 0, 28, 0);
        form.add(subtitle, c);

        // USERNAME

        JLabel usernameLabel = createLabel("USERNAME");

        c.gridy = 2;
        c.insets = new Insets(5, 0, 7, 0);
        form.add(usernameLabel, c);

        usernameField = new JTextField(20);

        usernameField.setDocument(new PlainDocument());
        usernameField.setFont(new Font("Consolas", Font.PLAIN, 16));
        usernameField.setForeground(white);
        usernameField.setBackground(new Color(20, 29, 45));
        usernameField.setCaretColor(white);
        usernameField.setHorizontalAlignment(JTextField.LEFT);
        usernameField.setMargin(new Insets(0, 0, 0, 0));
        usernameField.setPreferredSize(new Dimension(320, 45));

        usernameField.setBorder(new CompoundBorder(
                new LineBorder(new Color(110, 115, 125), 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));

        c.gridy = 3;
        c.insets = new Insets(0, 0, 17, 0);
        form.add(usernameField, c);

        // PASSWORD

        JLabel passwordLabel = createLabel("PASSWORD");

        c.gridy = 4;
        c.insets = new Insets(5, 0, 7, 0);
        form.add(passwordLabel, c);

        passwordField = new JPasswordField(20);
        stylePasswordField(passwordField);

        c.gridy = 5;
        c.insets = new Insets(0, 0, 8, 0);
        form.add(passwordField, c);

        // FORGOT PASSWORD

        JButton forgotButton = new JButton("Forgot Password?");

        forgotButton.setFont(new Font("Arial", Font.PLAIN, 12));
        forgotButton.setForeground(gold);
        forgotButton.setContentAreaFilled(false);
        forgotButton.setBorderPainted(false);
        forgotButton.setFocusPainted(false);
        forgotButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel forgotPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 0, 0)
        );

        forgotPanel.setOpaque(false);
        forgotPanel.add(forgotButton);

        c.gridy = 6;
        c.insets = new Insets(0, 0, 15, 0);
        form.add(forgotPanel, c);

        // LOGIN BUTTON

        JButton loginButton = createButton("LOGIN");

        c.gridy = 7;
        c.insets = new Insets(8, 0, 18, 0);
        form.add(loginButton, c);

        JLabel registerText = new JLabel(
                "Don't have an account?",
                SwingConstants.CENTER
        );

        registerText.setFont(new Font("Arial", Font.PLAIN, 12));
        registerText.setForeground(muted);

        c.gridy = 8;
        c.insets = new Insets(5, 0, 5, 0);
        form.add(registerText, c);

        // REGISTER BUTTON

        JButton registerButton = new JButton("REGISTER USER");

        registerButton.setFont(new Font("Arial", Font.BOLD, 13));
        registerButton.setForeground(gold);
        registerButton.setContentAreaFilled(false);
        registerButton.setBorder(new LineBorder(gold, 1, true));
        registerButton.setFocusPainted(false);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerButton.setPreferredSize(new Dimension(200, 40));

        c.gridy = 9;
        c.insets = new Insets(5, 0, 0, 0);
        form.add(registerButton, c);

        loginPanel.add(form);

        // POSITION PANELS

        GridBagConstraints left = new GridBagConstraints();

        left.gridx = 0;
        left.gridy = 0;
        left.weightx = 1;
        left.fill = GridBagConstraints.HORIZONTAL;
        left.anchor = GridBagConstraints.WEST;
        left.insets = new Insets(0, 0, 0, 50);

        background.add(branding, left);

        GridBagConstraints right = new GridBagConstraints();

        right.gridx = 1;
        right.gridy = 0;
        right.weightx = 0;
        right.anchor = GridBagConstraints.EAST;
        right.insets = new Insets(0, 0, 0, 20);

        background.add(loginPanel, right);

        setContentPane(background);

        // BUTTON ACTIONS

        loginButton.addActionListener(e -> login());

        passwordField.addActionListener(e -> login());

        registerButton.addActionListener(e -> {
            RegisterFrame register = new RegisterFrame();
            register.setVisible(true);
        });

        forgotButton.addActionListener(e -> forgotPassword());
    }

    private JLabel createLabel(String text) {

        JLabel label = new JLabel(text);

        label.setFont(new Font("Arial", Font.BOLD, 13));
        label.setForeground(gold);
        label.setHorizontalAlignment(SwingConstants.LEFT);

        return label;
    }

    private void stylePasswordField(JPasswordField field) {

        field.setFont(new Font("Consolas", Font.PLAIN, 16));
        field.setForeground(white);
        field.setBackground(new Color(20, 29, 45));
        field.setCaretColor(white);
        field.setHorizontalAlignment(JTextField.LEFT);
        field.setPreferredSize(new Dimension(320, 45));

        field.setBorder(new CompoundBorder(
                new LineBorder(new Color(110, 115, 125), 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));
    }

    private JButton createButton(String text) {

        JButton button = new JButton(text);

        button.setFont(new Font("Arial", Font.BOLD, 15));
        button.setForeground(new Color(25, 25, 25));
        button.setBackground(gold);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(13, 20, 13, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    // LOGIN AUTHENTICATION

    private void login() {

        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter both username and password."
            );

            return;
        }

        String sql =
                "SELECT user_id, username, role FROM users " +
                "WHERE username = ? AND password = ?";

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    String loggedInUsername = rs.getString("username");
                    String role = rs.getString("role");

                    if ("ADMIN".equalsIgnoreCase(role)) {

                        dispose();

                        AdminDashboard admin = new AdminDashboard();
                        admin.setVisible(true);

                    } else if ("OWNER".equalsIgnoreCase(role)) {

                        dispose();

                        FranchiseManagerDashboard manager =
                                new FranchiseManagerDashboard(loggedInUsername);

                        manager.setVisible(true);

                    } else {

                        JOptionPane.showMessageDialog(
                                this,
                                "Invalid user role."
                        );
                    }

                } else {

                    JOptionPane.showMessageDialog(
                            this,
                            "Invalid username or password.",
                            "Login Failed",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Database connection or login error:\n" + e.getMessage(),
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE
            );

            e.printStackTrace();
        }
    }

    // FORGOT PASSWORD

    private void forgotPassword() {

        String username = JOptionPane.showInputDialog(
                this,
                "Enter your OWNER username:",
                "Forgot Password",
                JOptionPane.QUESTION_MESSAGE
        );

        if (username == null || username.trim().isEmpty()) {
            return;
        }

        String sql =
                "UPDATE users SET password_reset_requested = 1, " +
                "reset_request_time = NOW() " +
                "WHERE username = ? AND role = 'OWNER'";

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setString(1, username.trim());

            int updated = ps.executeUpdate();

            if (updated > 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "Password reset request submitted successfully.\n" +
                        "Please contact the administrator."
                );

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Owner username not found."
                );
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to submit password request:\n" + e.getMessage()
            );

            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
