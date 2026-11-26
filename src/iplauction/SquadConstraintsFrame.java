
package iplauction;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.sql.*;

public class SquadConstraintsFrame extends JFrame {

    private Color darkBlue = new Color(13, 27, 52);
    private Color panelBlue = new Color(25, 45, 75);
    private Color gold = new Color(205, 170, 95);
    private Color white = Color.WHITE;

    private JTextField minSquadField;
    private JTextField maxSquadField;
    private JTextField overseasField;
    private JTextField batsmenField;
    private JTextField bowlersField;
    private JTextField allRoundersField;
    private JTextField wicketKeepersField;

    public SquadConstraintsFrame() {

        setTitle("IPL Auction - Squad Constraints");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 650));
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        getContentPane().setBackground(darkBlue);
        setLayout(new BorderLayout());

        createHeader();
        createForm();
        loadConstraints();
    }

    private void createHeader() {

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(panelBlue);
        header.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel title = new JLabel("SQUAD CONSTRAINTS");
        title.setFont(new Font("Arial", Font.BOLD, 25));
        title.setForeground(gold);

        JButton backButton = new JButton("Back to Dashboard");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.addActionListener(e -> dispose());

        header.add(title, BorderLayout.WEST);
        header.add(backButton, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);
    }

    private void createForm() {

        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(darkBlue);

        JPanel form = new JPanel(new GridLayout(0, 2, 20, 20));
        form.setBackground(panelBlue);
        form.setBorder(new CompoundBorder(
                new LineBorder(gold, 1),
                new EmptyBorder(35, 40, 35, 40)
        ));

        JLabel heading = new JLabel("Common Rules for All Five Teams");
        heading.setFont(new Font("Arial", Font.BOLD, 22));
        heading.setForeground(white);

        minSquadField = new JTextField();
        maxSquadField = new JTextField();
        overseasField = new JTextField();
        batsmenField = new JTextField();
        bowlersField = new JTextField();
        allRoundersField = new JTextField();
        wicketKeepersField = new JTextField();

        addField(form, "Minimum Squad Size", minSquadField);
        addField(form, "Maximum Squad Size", maxSquadField);
        addField(form, "Maximum Overseas Players", overseasField);
        addField(form, "Maximum Batsmen", batsmenField);
        addField(form, "Maximum Bowlers", bowlersField);
        addField(form, "Maximum All-Rounders", allRoundersField);
        addField(form, "Maximum Wicket-Keepers", wicketKeepersField);

        JButton saveButton = new JButton("SAVE CONSTRAINTS");
        saveButton.setFont(new Font("Arial", Font.BOLD, 16));
        saveButton.setBackground(gold);
        saveButton.setForeground(darkBlue);
        saveButton.setFocusPainted(false);
        saveButton.addActionListener(e -> saveConstraints());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottom.setBackground(panelBlue);
        bottom.add(saveButton);

        JPanel content = new JPanel(new BorderLayout(10, 25));
        content.setBackground(panelBlue);
        content.setBorder(new EmptyBorder(25, 25, 25, 25));
        content.add(heading, BorderLayout.NORTH);
        content.add(form, BorderLayout.CENTER);
        content.add(bottom, BorderLayout.SOUTH);

        outer.add(content);
        add(outer, BorderLayout.CENTER);
    }

    private void addField(JPanel panel, String name, JTextField field) {

        JLabel label = new JLabel(name);
        label.setFont(new Font("Arial", Font.BOLD, 15));
        label.setForeground(white);

        field.setFont(new Font("Arial", Font.PLAIN, 16));
        field.setPreferredSize(new Dimension(180, 38));

        panel.add(label);
        panel.add(field);
    }

    private void loadConstraints() {

        String sql = "SELECT min_squad_size, max_squad_size, max_overseas, "
                + "max_batsmen, max_bowlers, max_all_rounders, "
                + "max_wicket_keepers FROM users WHERE role='ADMIN' LIMIT 1";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                minSquadField.setText(String.valueOf(rs.getInt("min_squad_size")));
                maxSquadField.setText(String.valueOf(rs.getInt("max_squad_size")));
                overseasField.setText(String.valueOf(rs.getInt("max_overseas")));
                batsmenField.setText(String.valueOf(rs.getInt("max_batsmen")));
                bowlersField.setText(String.valueOf(rs.getInt("max_bowlers")));
                allRoundersField.setText(String.valueOf(rs.getInt("max_all_rounders")));
                wicketKeepersField.setText(String.valueOf(rs.getInt("max_wicket_keepers")));
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Unable to load constraints: " + e.getMessage());
        }
    }

    private void saveConstraints() {

        try {

            int min = Integer.parseInt(minSquadField.getText().trim());
            int max = Integer.parseInt(maxSquadField.getText().trim());
            int overseas = Integer.parseInt(overseasField.getText().trim());
            int batsmen = Integer.parseInt(batsmenField.getText().trim());
            int bowlers = Integer.parseInt(bowlersField.getText().trim());
            int allRounders = Integer.parseInt(allRoundersField.getText().trim());
            int wicketKeepers = Integer.parseInt(wicketKeepersField.getText().trim());

            if (min < 1 || max < min || max > 100
                    || overseas < 0 || overseas > max
                    || batsmen < 0 || batsmen > max
                    || bowlers < 0 || bowlers > max
                    || allRounders < 0 || allRounders > max
                    || wicketKeepers < 0 || wicketKeepers > max) {

                JOptionPane.showMessageDialog(this,
                        "Enter valid constraints. Minimum must not exceed maximum.");
                return;
            }

            String sql = "UPDATE users SET min_squad_size=?, max_squad_size=?, "
                    + "max_overseas=?, max_batsmen=?, max_bowlers=?, "
                    + "max_all_rounders=?, max_wicket_keepers=? WHERE role='ADMIN'";

            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, min);
                ps.setInt(2, max);
                ps.setInt(3, overseas);
                ps.setInt(4, batsmen);
                ps.setInt(5, bowlers);
                ps.setInt(6, allRounders);
                ps.setInt(7, wicketKeepers);

                int updated = ps.executeUpdate();

                if (updated > 0) {
                    JOptionPane.showMessageDialog(this,
                            "Squad constraints saved successfully!");
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Admin account not found.");
                }
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Enter valid whole numbers in all fields.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Database Error: " + e.getMessage());
        }
    }
}
