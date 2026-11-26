
package iplauction;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class RegisterFrame extends JFrame {

    private final Color darkBlue = new Color(10, 25, 55);
    private final Color panelBlue = new Color(20, 45, 85);
    private final Color buttonBlue = new Color(45, 100, 180);

    private final String ADMIN_CODE = "IPLADMIN2026";

    private JTextField nameField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JPasswordField adminCodeField;

    private JComboBox<String> roleBox;
    private JComboBox<String> teamBox;

    private JLabel teamLabel;
    private JLabel adminCodeLabel;
    private JLabel statusLabel;

    private JButton registerButton;

    public RegisterFrame() {

        setTitle("IPL Auction and Player Management System - Registration");
        setSize(600, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel main = new JPanel(new BorderLayout(15, 15));
        main.setBackground(darkBlue);
        main.setBorder(BorderFactory.createEmptyBorder(25, 40, 25, 40));

        JLabel title = new JLabel(
            "ACCOUNT REGISTRATION",
            SwingConstants.CENTER
        );

        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 23));

        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(panelBlue);
        panel.setBorder(
            BorderFactory.createEmptyBorder(20, 30, 20, 30)
        );

        JPanel form = new JPanel(new GridLayout(7, 2, 12, 13));
        form.setBackground(panelBlue);

        roleBox = new JComboBox<>(new String[]{"ADMIN", "OWNER"});

        nameField = new JTextField();
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        confirmPasswordField = new JPasswordField();
        adminCodeField = new JPasswordField();

        teamBox = new JComboBox<>();

        teamLabel = createLabel("Select Team");
        adminCodeLabel = createLabel("Admin Access Code");

        form.add(createLabel("Account Type"));
        form.add(roleBox);

        form.add(createLabel("Full Name"));
        form.add(nameField);

        form.add(createLabel("Username"));
        form.add(usernameField);

        form.add(createLabel("Password"));
        form.add(passwordField);

        form.add(createLabel("Confirm Password"));
        form.add(confirmPasswordField);

        form.add(adminCodeLabel);
        form.add(adminCodeField);

        form.add(teamLabel);
        form.add(teamBox);

        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 12));

        registerButton = new JButton("REGISTER");
        registerButton.setBackground(buttonBlue);
        registerButton.setForeground(Color.WHITE);
        registerButton.setFont(new Font("Arial", Font.BOLD, 13));
        registerButton.setFocusPainted(false);

        JButton closeButton = new JButton("CLOSE");
        closeButton.addActionListener(e -> dispose());

        JPanel buttons = new JPanel(new GridLayout(1, 2, 12, 0));
        buttons.setBackground(panelBlue);
        buttons.add(registerButton);
        buttons.add(closeButton);

        JPanel bottom = new JPanel(new BorderLayout(10, 12));
        bottom.setBackground(panelBlue);
        bottom.add(statusLabel, BorderLayout.CENTER);
        bottom.add(buttons, BorderLayout.SOUTH);

        panel.add(form, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);

        main.add(title, BorderLayout.NORTH);
        main.add(panel, BorderLayout.CENTER);

        add(main);

        loadAvailableTeams();

        roleBox.addActionListener(e -> updateRegistrationFields());

        registerButton.addActionListener(e -> registerUser());

        updateRegistrationFields();
    }

    private JLabel createLabel(String text) {

        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 13));

        return label;
    }

    private void loadAvailableTeams() {

        teamBox.removeAllItems();

        String sql =
            "SELECT team_id, team_name FROM teams " +
            "WHERE owner_user_id IS NULL ORDER BY team_name";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                teamBox.addItem(
                    rs.getInt("team_id") + " - " +
                    rs.getString("team_name")
                );
            }

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                this,
                "Unable to load teams: " + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void updateRegistrationFields() {

        String role = (String) roleBox.getSelectedItem();

        boolean isAdmin = "ADMIN".equals(role);
        boolean hasTeams = teamBox.getItemCount() > 0;

        adminCodeLabel.setVisible(isAdmin);
        adminCodeField.setVisible(isAdmin);

        teamLabel.setVisible(!isAdmin);
        teamBox.setVisible(!isAdmin);

        teamBox.setEnabled(!isAdmin && hasTeams);

        if (isAdmin) {

            statusLabel.setText(
                "Register a new administrator account."
            );

            statusLabel.setForeground(new Color(130, 220, 160));

            registerButton.setEnabled(true);

        } else if (!hasTeams) {

            statusLabel.setText(
                "All five teams are assigned. Owner registration is closed."
            );

            statusLabel.setForeground(new Color(255, 180, 100));

            registerButton.setEnabled(false);

        } else {

            statusLabel.setText(
                "Select an available team for the new owner."
            );

            statusLabel.setForeground(new Color(130, 220, 160));

            registerButton.setEnabled(true);
        }

        revalidate();
        repaint();
    }

    private void registerUser() {

        String role = (String) roleBox.getSelectedItem();

        String fullName = nameField.getText().trim();
        String username = usernameField.getText().trim();

        String password =
            new String(passwordField.getPassword());

        String confirmPassword =
            new String(confirmPasswordField.getPassword());

        if (fullName.isEmpty() ||
            username.isEmpty() ||
            password.isEmpty() ||
            confirmPassword.isEmpty()) {

            JOptionPane.showMessageDialog(
                this,
                "Please fill in all required fields."
            );

            return;
        }

        if (!password.equals(confirmPassword)) {

            JOptionPane.showMessageDialog(
                this,
                "Passwords do not match.",
                "Registration Error",
                JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        if (password.length() < 6) {

            JOptionPane.showMessageDialog(
                this,
                "Password must contain at least 6 characters."
            );

            return;
        }

        int teamId = -1;

        if ("ADMIN".equals(role)) {

            String enteredCode =
                new String(adminCodeField.getPassword());

            if (!ADMIN_CODE.equals(enteredCode)) {

                JOptionPane.showMessageDialog(
                    this,
                    "Invalid Admin Access Code.",
                    "Access Denied",
                    JOptionPane.ERROR_MESSAGE
                );

                return;
            }

        } else {

            String selectedTeam =
                (String) teamBox.getSelectedItem();

            if (selectedTeam == null) {

                JOptionPane.showMessageDialog(
                    this,
                    "No unassigned teams available."
                );

                return;
            }

            teamId = Integer.parseInt(
                selectedTeam.substring(
                    0,
                    selectedTeam.indexOf(" - ")
                )
            );
        }

        String insertUser =
            "INSERT INTO users (username, password, full_name, role) " +
            "VALUES (?, ?, ?, ?)";

        String updateTeam =
            "UPDATE teams SET owner_user_id = ? " +
            "WHERE team_id = ? AND owner_user_id IS NULL";

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try {

                int userId;

                try (
                    PreparedStatement ps = con.prepareStatement(
                        insertUser,
                        PreparedStatement.RETURN_GENERATED_KEYS
                    )
                ) {

                    ps.setString(1, username);
                    ps.setString(2, password);
                    ps.setString(3, fullName);
                    ps.setString(4, role);

                    ps.executeUpdate();

                    try (ResultSet keys = ps.getGeneratedKeys()) {

                        if (!keys.next()) {
                            throw new SQLException(
                                "Unable to get the new user ID."
                            );
                        }

                        userId = keys.getInt(1);
                    }
                }

                if ("OWNER".equals(role)) {

                    try (
                        PreparedStatement ps =
                            con.prepareStatement(updateTeam)
                    ) {

                        ps.setInt(1, userId);
                        ps.setInt(2, teamId);

                        if (ps.executeUpdate() != 1) {

                            throw new SQLException(
                                "This team has already been assigned."
                            );
                        }
                    }
                }

                con.commit();

                JOptionPane.showMessageDialog(
                    this,
                    role + " account registered successfully!",
                    "Registration Successful",
                    JOptionPane.INFORMATION_MESSAGE
                );

                dispose();

            } catch (SQLException ex) {

                con.rollback();

                JOptionPane.showMessageDialog(
                    this,
                    "Registration failed: " + ex.getMessage(),
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE
                );

            } finally {

                con.setAutoCommit(true);
            }

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                this,
                "Database Error: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
