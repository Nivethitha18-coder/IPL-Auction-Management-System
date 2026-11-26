
package iplauction;

import java.awt.*;
import java.math.BigDecimal;
import java.sql.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class AddTeamFrame extends JFrame {

    Color bg = new Color(10, 17, 32);
    Color card = new Color(20, 31, 52);
    Color blue = new Color(45, 115, 220);
    Color orange = new Color(245, 145, 55);
    Color white = new Color(240, 244, 250);
    Color muted = new Color(155, 170, 195);

    JTextField teamNameField, purseField;
    JTable table;
    DefaultTableModel model;

    JLabel totalTeams, assignedOwners, unassignedTeams;

    AdminDashboard dashboard;

    public AddTeamFrame(AdminDashboard dashboard) {

        this.dashboard = dashboard;

        setTitle("IPL Auction and Player Management System - Franchise Management");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(bg);

        add(createHeader(), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);

        loadTeams();

        setVisible(true);
    }

    JPanel createHeader() {

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(15, 27, 48));
        header.setBorder(new EmptyBorder(20, 35, 20, 35));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);

        JLabel title = new JLabel("FRANCHISE MANAGEMENT");
        title.setFont(new Font("Segoe UI", Font.BOLD, 27));
        title.setForeground(white);

        JLabel subtitle = new JLabel(
            "Manage IPL franchises, team details and auction purse"
        );
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(muted);

        titlePanel.add(title);
        titlePanel.add(subtitle);

        JButton back = createButton("BACK TO DASHBOARD", new Color(55, 70, 95));
        back.addActionListener(e -> dispose());

        header.add(titlePanel, BorderLayout.WEST);
        header.add(back, BorderLayout.EAST);

        return header;
    }

    JPanel createMainPanel() {

        JPanel main = new JPanel(new BorderLayout(20, 20));
        main.setBackground(bg);
        main.setBorder(new EmptyBorder(25, 35, 30, 35));

        // STATISTICS

        JPanel stats = new JPanel(new GridLayout(1, 3, 18, 0));
        stats.setBackground(bg);

        totalTeams = new JLabel("0");
        assignedOwners = new JLabel("0");
        unassignedTeams = new JLabel("0");

        stats.add(createStatCard("TOTAL FRANCHISES", totalTeams, blue));
        stats.add(createStatCard("ASSIGNED OWNERS", assignedOwners, orange));
        stats.add(createStatCard("OWNER NOT ASSIGNED", unassignedTeams,
            new Color(70, 170, 130)));

        // TOP SECTION

        JPanel top = new JPanel(new BorderLayout(0, 20));
        top.setBackground(bg);
        top.add(stats, BorderLayout.NORTH);
        top.add(createAddPanel(), BorderLayout.CENTER);

        main.add(top, BorderLayout.NORTH);
        main.add(createTablePanel(), BorderLayout.CENTER);

        return main;
    }

    JPanel createStatCard(String heading, JLabel value, Color accent) {

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(card);
        panel.setBorder(new CompoundBorder(
            new LineBorder(new Color(45, 60, 85), 1, true),
            new EmptyBorder(18, 22, 18, 22)
        ));

        JLabel label = new JLabel(heading);
        label.setForeground(muted);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));

        value.setForeground(accent);
        value.setFont(new Font("Segoe UI", Font.BOLD, 29));

        panel.add(label, BorderLayout.NORTH);
        panel.add(value, BorderLayout.CENTER);

        return panel;
    }

    JPanel createAddPanel() {

        JPanel panel = new JPanel(new BorderLayout(20, 15));
        panel.setBackground(card);
        panel.setBorder(new CompoundBorder(
            new LineBorder(new Color(45, 60, 85), 1, true),
            new EmptyBorder(20, 25, 20, 25)
        ));

        JPanel headingPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        headingPanel.setOpaque(false);

        JLabel heading = new JLabel("ADD NEW FRANCHISE");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 19));
        heading.setForeground(white);

        JLabel description = new JLabel(
            "Create a new team and configure its initial auction purse"
        );
        description.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        description.setForeground(muted);

        headingPanel.add(heading);
        headingPanel.add(description);

        JPanel fields = new JPanel(new GridLayout(2, 2, 18, 8));
        fields.setOpaque(false);

        teamNameField = new JTextField();
        purseField = new JTextField();

        styleField(teamNameField);
        styleField(purseField);

        JPanel namePanel = createField("FRANCHISE NAME", teamNameField);
        JPanel pursePanel = createField("TOTAL PURSE (CRORES)", purseField);

        fields.add(namePanel);
        fields.add(pursePanel);

        JButton addButton = createButton("+  ADD FRANCHISE", blue);
        JButton clearButton = createButton("CLEAR", new Color(65, 80, 105));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);
        actions.add(clearButton);
        actions.add(addButton);

        panel.add(headingPanel, BorderLayout.NORTH);
        panel.add(fields, BorderLayout.CENTER);
        panel.add(actions, BorderLayout.SOUTH);

        addButton.addActionListener(e -> addTeam());
        clearButton.addActionListener(e -> {
            teamNameField.setText("");
            purseField.setText("");
        });

        return panel;
    }

    JPanel createField(String labelText, JTextField field) {

        JPanel panel = new JPanel(new BorderLayout(0, 7));
        panel.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setForeground(muted);

        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);

        return panel;
    }

    void styleField(JTextField field) {

        field.setBackground(new Color(12, 22, 39));
        field.setForeground(white);
        field.setCaretColor(white);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setBorder(new CompoundBorder(
            new LineBorder(new Color(60, 80, 110), 1, true),
            new EmptyBorder(10, 12, 10, 12)
        ));
    }

    JPanel createTablePanel() {

        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(card);
        panel.setBorder(new CompoundBorder(
            new LineBorder(new Color(45, 60, 85), 1, true),
            new EmptyBorder(20, 22, 20, 22)
        ));

        JPanel heading = new JPanel(new BorderLayout());
        heading.setOpaque(false);

        JLabel title = new JLabel("REGISTERED FRANCHISES");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(white);

        JButton refresh = createButton("REFRESH", new Color(45, 115, 150));
        refresh.addActionListener(e -> loadTeams());

        heading.add(title, BorderLayout.WEST);
        heading.add(refresh, BorderLayout.EAST);

        String[] columns = {
            "TEAM ID",
            "FRANCHISE NAME",
            "OWNER ID",
            "TOTAL PURSE (CR)",
            "REMAINING PURSE (CR)",
            "PURSE UTILIZED (CR)"
        };

        model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(38);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setForeground(white);
        table.setBackground(card);
        table.setGridColor(new Color(42, 55, 78));
        table.setSelectionBackground(new Color(40, 75, 120));
        table.setSelectionForeground(white);
        table.setShowVerticalLines(false);

        JTableHeader th = table.getTableHeader();
        th.setBackground(new Color(32, 48, 73));
        th.setForeground(white);
        th.setFont(new Font("Segoe UI", Font.BOLD, 12));
        th.setPreferredSize(new Dimension(100, 42));

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(
                JTable table, Object value, boolean selected,
                boolean focus, int row, int column) {

                Component c = super.getTableCellRendererComponent(
                    table, value, selected, focus, row, column
                );

                if (!selected) {
                    c.setBackground(row % 2 == 0
                        ? new Color(20, 31, 52)
                        : new Color(24, 37, 60));
                    c.setForeground(white);
                }

                setBorder(new EmptyBorder(5, 10, 5, 10));
                return c;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(card);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        panel.add(heading, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    JButton createButton(String text, Color color) {

        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(white);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(11, 17, 11, 17));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    void addTeam() {

        String name = teamNameField.getText().trim();
        String purseText = purseField.getText().trim();

        if (name.isEmpty() || purseText.isEmpty()) {
            JOptionPane.showMessageDialog(
                this, "Please enter both franchise name and total purse."
            );
            return;
        }

        try {

            BigDecimal purse = new BigDecimal(purseText);

            if (purse.compareTo(BigDecimal.ZERO) <= 0
                || purse.scale() > 2
                || purse.precision() > 8) {

                JOptionPane.showMessageDialog(
                    this, "Enter a valid positive purse amount (maximum 2 decimal places)."
                );
                return;
            }

            String sql = "INSERT INTO teams "
                + "(team_name, owner_user_id, total_purse, remaining_purse) "
                + "VALUES (?, NULL, ?, ?)";

            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, name);
                ps.setBigDecimal(2, purse);
                ps.setBigDecimal(3, purse);

                ps.executeUpdate();

                JOptionPane.showMessageDialog(
                    this,
                    "Franchise added successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
                );

                teamNameField.setText("");
                purseField.setText("");

                loadTeams();
            }

        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(
                this, "Purse must be a valid number. Example: 120.00"
            );

        } catch (SQLIntegrityConstraintViolationException ex) {

            JOptionPane.showMessageDialog(
                this, "This franchise name already exists."
            );

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                this, "Database Error: " + ex.getMessage()
            );
        }
    }

    void loadTeams() {

        if (model == null) return;

        model.setRowCount(0);

        int total = 0;
        int assigned = 0;
        int unassigned = 0;

        String sql = "SELECT team_id, team_name, owner_user_id, "
            + "total_purse, remaining_purse FROM teams ORDER BY team_id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                total++;

                Object owner = rs.getObject("owner_user_id");

                if (owner == null) {
                    unassigned++;
                    owner = "Not Assigned";
                } else {
                    assigned++;
                }

                BigDecimal totalPurse = rs.getBigDecimal("total_purse");
                BigDecimal remaining = rs.getBigDecimal("remaining_purse");
                BigDecimal utilized = totalPurse.subtract(remaining);

                model.addRow(new Object[] {
                    rs.getInt("team_id"),
                    rs.getString("team_name"),
                    owner,
                    totalPurse,
                    remaining,
                    utilized
                });
            }

            totalTeams.setText(String.valueOf(total));
            assignedOwners.setText(String.valueOf(assigned));
            unassignedTeams.setText(String.valueOf(unassigned));

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                this, "Unable to load franchises: " + ex.getMessage()
            );
        }
    }
}
