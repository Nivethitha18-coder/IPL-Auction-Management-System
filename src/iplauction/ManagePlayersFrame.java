
package iplauction;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.math.BigDecimal;

public class ManagePlayersFrame extends JFrame implements ActionListener {

    JLabel titleLabel, searchLabel, filterLabel;
    JTextField searchField;
    JComboBox<String> categoryBox;
    JTable playerTable;
    DefaultTableModel model;
    TableRowSorter<DefaultTableModel> sorter;

    JButton addButton, removeButton, refreshButton, backButton;

    Color darkBlue = new Color(15, 25, 45);
    Color panelBlue = new Color(25, 40, 65);
    Color accentBlue = new Color(40, 120, 220);
    Color white = Color.WHITE;

    public ManagePlayersFrame() {

        setTitle("IPL Auction System - Manage Players");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(darkBlue);

        // HEADER
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(darkBlue);
        headerPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        titleLabel = new JLabel("MANAGE PLAYERS");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setForeground(white);

        headerPanel.add(titleLabel, BorderLayout.WEST);

        backButton = new JButton("BACK");
        backButton.addActionListener(this);
        styleButton(backButton, new Color(90, 100, 120));

        headerPanel.add(backButton, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // SEARCH AND FILTER PANEL
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        filterPanel.setBackground(panelBlue);
        filterPanel.setBorder(new EmptyBorder(5, 15, 5, 15));

        searchLabel = new JLabel("Search Player:");
        searchLabel.setForeground(white);
        searchLabel.setFont(new Font("Arial", Font.BOLD, 14));

        searchField = new JTextField(20);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));

        filterLabel = new JLabel("Category:");
        filterLabel.setForeground(white);
        filterLabel.setFont(new Font("Arial", Font.BOLD, 14));

        String categories[] = {
            "ALL",
            "BATSMAN",
            "BOWLER",
            "ALL_ROUNDER",
            "WICKET_KEEPER"
        };

        categoryBox = new JComboBox<String>(categories);
        categoryBox.setFont(new Font("Arial", Font.PLAIN, 14));

        filterPanel.add(searchLabel);
        filterPanel.add(searchField);
        filterPanel.add(filterLabel);
        filterPanel.add(categoryBox);

        // TABLE
        String columns[] = {
            "Player ID",
            "Player Name",
            "Category",
            "Nationality",
            "Base Price (Cr)",
            "Team",
            "Purchase Type",
            "Purchase Price (Cr)",
            "Status"
        };

        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        playerTable = new JTable(model);
        playerTable.setFont(new Font("Arial", Font.PLAIN, 13));
        playerTable.setRowHeight(30);
        playerTable.setBackground(new Color(235, 240, 248));
        playerTable.setForeground(Color.BLACK);
        playerTable.setSelectionBackground(accentBlue);
        playerTable.setSelectionForeground(white);
        playerTable.getTableHeader().setFont(
            new Font("Arial", Font.BOLD, 13)
        );
        playerTable.getTableHeader().setBackground(
            new Color(40, 65, 100)
        );
        playerTable.getTableHeader().setForeground(white);

        sorter = new TableRowSorter<DefaultTableModel>(model);
        playerTable.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(playerTable);
        scrollPane.getViewport().setBackground(Color.WHITE);

        // CENTER PANEL
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(darkBlue);
        centerPanel.add(filterPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // BOTTOM BUTTON PANEL
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        buttonPanel.setBackground(darkBlue);

        addButton = new JButton("ADD PLAYER");
        removeButton = new JButton("REMOVE SELECTED");
        refreshButton = new JButton("REFRESH");

        styleButton(addButton, new Color(30, 150, 90));
        styleButton(removeButton, new Color(190, 65, 65));
        styleButton(refreshButton, accentBlue);

        addButton.addActionListener(this);
        removeButton.addActionListener(this);
        refreshButton.addActionListener(this);

        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(refreshButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // SEARCH FILTER
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                applyFilters();
            }
        });

        categoryBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                applyFilters();
            }
        });

        loadPlayers();

        setVisible(true);
    }

    private void styleButton(JButton button, Color color) {

        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setForeground(white);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 18, 10, 18));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // LOAD PLAYERS FROM DATABASE
    private void loadPlayers() {

        model.setRowCount(0);

        String sql =
            "SELECT p.player_id, p.player_name, p.category, " +
            "p.nationality, p.base_price, t.team_name, " +
            "p.purchase_type, p.purchase_price, " +
            "CASE " +
            "WHEN p.purchase_type = 'RETAINED' THEN 'RETAINED' " +
            "WHEN a.status = 'LIVE' THEN 'LIVE' " +
            "WHEN a.status = 'SOLD' THEN 'SOLD' " +
            "WHEN a.status = 'UNSOLD' THEN 'UNSOLD' " +
            "ELSE 'AVAILABLE' END AS player_status " +
            "FROM players p " +
            "LEFT JOIN teams t ON p.team_id = t.team_id " +
            "LEFT JOIN auction_lots a ON p.player_id = a.player_id " +
            "ORDER BY p.player_id";

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {

                Object data[] = {
                    rs.getInt("player_id"),
                    rs.getString("player_name"),
                    rs.getString("category"),
                    rs.getString("nationality"),
                    rs.getBigDecimal("base_price"),
                    rs.getString("team_name") == null
                        ? "-" : rs.getString("team_name"),
                    rs.getString("purchase_type") == null
                        ? "-" : rs.getString("purchase_type"),
                    rs.getBigDecimal("purchase_price") == null
                        ? "-" : rs.getBigDecimal("purchase_price"),
                    rs.getString("player_status")
                };

                model.addRow(data);
            }

            rs.close();
            pst.close();
            con.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                "Unable to load players: " + e.getMessage()
            );
        }

        applyFilters();
    }

    // SEARCH AND CATEGORY FILTER
    private void applyFilters() {

        String searchText = searchField.getText().trim();
        String selectedCategory =
            categoryBox.getSelectedItem().toString();

        RowFilter<DefaultTableModel, Object> searchFilter =
            RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(searchText), 1);

        RowFilter<DefaultTableModel, Object> categoryFilter = null;

        if (!selectedCategory.equals("ALL")) {
            categoryFilter = RowFilter.regexFilter(
                "^" + java.util.regex.Pattern.quote(selectedCategory) + "$",
                2
            );
        }

        if (categoryFilter == null) {
            sorter.setRowFilter(searchFilter);
        } else {
            java.util.List<RowFilter<DefaultTableModel, Object>> filters =
                new java.util.ArrayList<RowFilter<DefaultTableModel, Object>>();

            filters.add(searchFilter);
            filters.add(categoryFilter);

            sorter.setRowFilter(RowFilter.andFilter(filters));
        }
    }

    // ADD PLAYER
    private void addPlayer() {

        JTextField nameField = new JTextField(15);
        JComboBox<String> categoryField = new JComboBox<String>(
            new String[] {
                "BATSMAN",
                "BOWLER",
                "ALL_ROUNDER",
                "WICKET_KEEPER"
            }
        );

        JComboBox<String> nationalityField = new JComboBox<String>(
            new String[] {
                "INDIAN",
                "OVERSEAS"
            }
        );

        JTextField priceField = new JTextField(15);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));

        panel.add(new JLabel("Player Name:"));
        panel.add(nameField);

        panel.add(new JLabel("Category:"));
        panel.add(categoryField);

        panel.add(new JLabel("Nationality:"));
        panel.add(nationalityField);

        panel.add(new JLabel("Base Price (Cr):"));
        panel.add(priceField);

        int result = JOptionPane.showConfirmDialog(
            this,
            panel,
            "Add New Player",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String name = nameField.getText().trim();
        String category = categoryField.getSelectedItem().toString();
        String nationality = nationalityField.getSelectedItem().toString();

        if (name.isEmpty() || priceField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Please enter all player details."
            );
            return;
        }

        BigDecimal basePrice;

        try {
            basePrice = new BigDecimal(priceField.getText().trim());

            if (basePrice.compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(
                    this,
                    "Base price must be greater than zero."
                );
                return;
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                this,
                "Enter a valid base price."
            );
            return;
        }

        Connection con = null;

        try {

            con = DBConnection.getConnection();
            con.setAutoCommit(false);

            String playerSql =
                "INSERT INTO players " +
                "(player_name, category, nationality, base_price, " +
                "team_id, purchase_type, purchase_price) " +
                "VALUES (?, ?, ?, ?, NULL, NULL, NULL)";

            PreparedStatement pst = con.prepareStatement(
                playerSql,
                Statement.RETURN_GENERATED_KEYS
            );

            pst.setString(1, name);
            pst.setString(2, category);
            pst.setString(3, nationality);
            pst.setBigDecimal(4, basePrice);

            pst.executeUpdate();

            ResultSet keys = pst.getGeneratedKeys();

            if (!keys.next()) {
                throw new SQLException("Player ID was not generated.");
            }

            int playerId = keys.getInt(1);

            String lotSql =
                "INSERT INTO auction_lots (player_id, status) " +
                "VALUES (?, 'AVAILABLE')";

            PreparedStatement lotPst = con.prepareStatement(lotSql);
            lotPst.setInt(1, playerId);
            lotPst.executeUpdate();

            con.commit();

            keys.close();
            pst.close();
            lotPst.close();

            JOptionPane.showMessageDialog(
                this,
                "Player added successfully!"
            );

            loadPlayers();

        } catch (Exception e) {

            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            JOptionPane.showMessageDialog(
                this,
                "Unable to add player: " + e.getMessage()
            );

        } finally {

            try {
                if (con != null) {
                    con.setAutoCommit(true);
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // REMOVE SELECTED AVAILABLE PLAYER
    private void removePlayer() {

        int selectedRow = playerTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                this,
                "Please select a player to remove."
            );
            return;
        }

        int modelRow = playerTable.convertRowIndexToModel(selectedRow);

        int playerId = Integer.parseInt(
            model.getValueAt(modelRow, 0).toString()
        );

        String status = model.getValueAt(modelRow, 8).toString();

        if (!status.equals("AVAILABLE")) {
            JOptionPane.showMessageDialog(
                this,
                "Only AVAILABLE players can be removed."
            );
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to remove this player?",
            "Confirm Removal",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        Connection con = null;

        try {

            con = DBConnection.getConnection();
            con.setAutoCommit(false);

            PreparedStatement lotPst = con.prepareStatement(
                "DELETE FROM auction_lots WHERE player_id = ? AND status = 'AVAILABLE'"
            );

            lotPst.setInt(1, playerId);
            lotPst.executeUpdate();

            PreparedStatement playerPst = con.prepareStatement(
                "DELETE FROM players WHERE player_id = ? " +
                "AND team_id IS NULL AND purchase_type IS NULL"
            );

            playerPst.setInt(1, playerId);

            int deleted = playerPst.executeUpdate();

            if (deleted == 0) {
                throw new SQLException(
                    "Player could not be removed. Check the player's auction status."
                );
            }

            con.commit();

            JOptionPane.showMessageDialog(
                this,
                "Player removed successfully!"
            );

            lotPst.close();
            playerPst.close();

            loadPlayers();

        } catch (Exception e) {

            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            JOptionPane.showMessageDialog(
                this,
                "Unable to remove player: " + e.getMessage()
            );

        } finally {

            try {
                if (con != null) {
                    con.setAutoCommit(true);
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == addButton) {
            addPlayer();
        }

        else if (e.getSource() == removeButton) {
            removePlayer();
        }

        else if (e.getSource() == refreshButton) {
            searchField.setText("");
            categoryBox.setSelectedIndex(0);
            loadPlayers();
        }

        else if (e.getSource() == backButton) {
            dispose();
        }
    }
}
