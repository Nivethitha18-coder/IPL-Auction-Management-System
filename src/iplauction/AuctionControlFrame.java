
package iplauction;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class AuctionControlFrame extends JFrame {

    private final Color darkBlue = new Color(13, 27, 52);
    private final Color panelBlue = new Color(25, 45, 75);
    private final Color buttonBlue = new Color(40, 105, 190);
    private final Color white = Color.WHITE;
    private final Color mutedBlue = new Color(175, 195, 220);

    private AdminDashboard dashboard;

    private JTable auctionTable;
    private DefaultTableModel auctionModel;

    private TableRowSorter<DefaultTableModel> sorter;

    private JTextField searchField;

    private JComboBox<String> statusFilter;
    private JComboBox<String> categoryFilter;
    private JComboBox<String> nationalityFilter;

    public AuctionControlFrame(AdminDashboard dashboard) {

        this.dashboard = dashboard;

        setTitle("IPL Auction System - Auction Management");
        setSize(1250, 750);
        setLocationRelativeTo(dashboard);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel main = new JPanel(new BorderLayout(15, 15));
        main.setBackground(darkBlue);
        main.setBorder(
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        );

        JLabel heading = new JLabel(
                "AUCTION MANAGEMENT PANEL",
                SwingConstants.CENTER
        );

        heading.setForeground(white);
        heading.setFont(new Font("Arial", Font.BOLD, 24));

        createTable();

        JPanel filterPanel = createFilterPanel();

        JScrollPane scroll = new JScrollPane(auctionTable);
        scroll.getViewport().setBackground(panelBlue);

        JPanel tablePanel = new JPanel(new BorderLayout(10, 10));
        tablePanel.setBackground(panelBlue);
        tablePanel.setBorder(
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        );

        JLabel instruction = new JLabel(
                "Select players and perform auction operations"
        );

        instruction.setForeground(white);
        instruction.setFont(new Font("Arial", Font.BOLD, 14));

        tablePanel.add(instruction, BorderLayout.NORTH);
        tablePanel.add(scroll, BorderLayout.CENTER);

        JPanel buttonPanel = createButtonPanel();

        JPanel topPanel = new JPanel(new BorderLayout(10, 15));
        topPanel.setBackground(darkBlue);

        topPanel.add(heading, BorderLayout.NORTH);
        topPanel.add(filterPanel, BorderLayout.CENTER);

        main.add(topPanel, BorderLayout.NORTH);
        main.add(tablePanel, BorderLayout.CENTER);
        main.add(buttonPanel, BorderLayout.SOUTH);

        add(main);

        loadAuctionPlayers();
    }

    // ---------------- CREATE TABLE ----------------

    private void createTable() {

        auctionModel = new DefaultTableModel(
                new String[]{
                        "Lot ID",
                        "Player ID",
                        "Player Name",
                        "Category",
                        "Nationality",
                        "Base Price",
                        "Status"
                },
                0
        ) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        auctionTable = new JTable(auctionModel);

        auctionTable.setSelectionMode(
                ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
        );

        auctionTable.setAutoCreateRowSorter(true);

        sorter = new TableRowSorter<>(auctionModel);
        auctionTable.setRowSorter(sorter);

        styleTable(auctionTable);
    }

    // ---------------- FILTER PANEL ----------------

    private JPanel createFilterPanel() {

        JPanel panel = new JPanel(new GridLayout(2, 4, 12, 10));
        panel.setBackground(darkBlue);
        panel.setBorder(
                BorderFactory.createEmptyBorder(10, 0, 10, 0)
        );

        JLabel searchLabel = createFilterLabel("SEARCH PLAYER");
        JLabel statusLabel = createFilterLabel("AUCTION STATUS");
        JLabel categoryLabel = createFilterLabel("PLAYER CATEGORY");
        JLabel nationalityLabel = createFilterLabel("NATIONALITY");

        searchField = new JTextField();
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setPreferredSize(new Dimension(200, 35));

        statusFilter = new JComboBox<>(
                new String[]{
                        "ALL",
                        "AVAILABLE",
                        "LIVE",
                        "CLOSED",
                        "SOLD",
                        "UNSOLD"
                }
        );

        categoryFilter = new JComboBox<>(
                new String[]{
                        "ALL",
                        "BATSMAN",
                        "BOWLER",
                        "ALL_ROUNDER",
                        "WICKET_KEEPER"
                }
        );

        nationalityFilter = new JComboBox<>(
                new String[]{
                        "ALL",
                        "INDIAN",
                        "OVERSEAS"
                }
        );

        styleCombo(statusFilter);
        styleCombo(categoryFilter);
        styleCombo(nationalityFilter);

        panel.add(searchLabel);
        panel.add(statusLabel);
        panel.add(categoryLabel);
        panel.add(nationalityLabel);

        panel.add(searchField);
        panel.add(statusFilter);
        panel.add(categoryFilter);
        panel.add(nationalityFilter);

        searchField.getDocument().addDocumentListener(
                new DocumentListener() {

                    public void insertUpdate(DocumentEvent e) {
                        applyFilters();
                    }

                    public void removeUpdate(DocumentEvent e) {
                        applyFilters();
                    }

                    public void changedUpdate(DocumentEvent e) {
                        applyFilters();
                    }
                }
        );

        statusFilter.addActionListener(e -> applyFilters());
        categoryFilter.addActionListener(e -> applyFilters());
        nationalityFilter.addActionListener(e -> applyFilters());

        return panel;
    }

    private JLabel createFilterLabel(String text) {

        JLabel label = new JLabel(text);
        label.setForeground(mutedBlue);
        label.setFont(new Font("Arial", Font.BOLD, 12));

        return label;
    }

    private void styleCombo(JComboBox<String> combo) {

        combo.setFont(new Font("Arial", Font.PLAIN, 13));
        combo.setBackground(white);
        combo.setForeground(darkBlue);
        combo.setPreferredSize(new Dimension(180, 35));
    }

    // ---------------- APPLY FILTERS ----------------

    private void applyFilters() {

        if (sorter == null) {
            return;
        }

        ArrayList<RowFilter<Object, Object>> filters =
                new ArrayList<>();

        String search = searchField.getText().trim();

        if (!search.isEmpty()) {

            filters.add(
                    RowFilter.regexFilter(
                            "(?i)" + java.util.regex.Pattern.quote(search),
                            2
                    )
            );
        }

        String status = statusFilter.getSelectedItem().toString();

        if (!status.equals("ALL")) {

            filters.add(
                    RowFilter.regexFilter(
                            "^" + status + "$",
                            6
                    )
            );
        }

        String category = categoryFilter.getSelectedItem().toString();

        if (!category.equals("ALL")) {

            filters.add(
                    RowFilter.regexFilter(
                            "^" + category + "$",
                            3
                    )
            );
        }

        String nationality =
                nationalityFilter.getSelectedItem().toString();

        if (!nationality.equals("ALL")) {

            filters.add(
                    RowFilter.regexFilter(
                            "^" + nationality + "$",
                            4
                    )
            );
        }

        sorter.setRowFilter(RowFilter.andFilter(filters));
    }

    // ---------------- BUTTON PANEL ----------------

    private JPanel createButtonPanel() {

        JPanel panel = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 12, 10)
        );

        panel.setBackground(darkBlue);

        JButton startButton =
                new JButton("START SELECTED AUCTIONS");

        JButton closeButton =
                new JButton("CLOSE SELECTED AUCTIONS");

        JButton refreshButton =
                new JButton("REFRESH ALL");

        JButton clearButton =
                new JButton("CLEAR FILTERS");

        JButton exitButton =
                new JButton("CLOSE WINDOW");

        styleButton(startButton);
        styleButton(closeButton);
        styleButton(refreshButton);
        styleButton(clearButton);
        styleButton(exitButton);

        startButton.addActionListener(e ->
                startSelectedAuctions()
        );

        closeButton.addActionListener(e ->
                closeSelectedAuctions()
        );

        refreshButton.addActionListener(e -> {
            loadAuctionPlayers();
            dashboard.loadStatistics();
        });

        clearButton.addActionListener(e ->
                clearFilters()
        );

        exitButton.addActionListener(e ->
                dispose()
        );

        panel.add(startButton);
        panel.add(closeButton);
        panel.add(refreshButton);
        panel.add(clearButton);
        panel.add(exitButton);

        return panel;
    }

    private void clearFilters() {

        searchField.setText("");
        statusFilter.setSelectedItem("ALL");
        categoryFilter.setSelectedItem("ALL");
        nationalityFilter.setSelectedItem("ALL");

        applyFilters();
    }

    // ---------------- BUTTON DESIGN ----------------

    private void styleButton(JButton button) {

        button.setBackground(buttonBlue);
        button.setForeground(white);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setFocusPainted(false);

        button.setBorder(
                BorderFactory.createEmptyBorder(10, 18, 10, 18)
        );

        button.setCursor(
                new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR)
        );
    }

    // ---------------- TABLE DESIGN ----------------

    private void styleTable(JTable table) {

        table.setBackground(panelBlue);
        table.setForeground(white);
        table.setGridColor(new Color(55, 80, 115));

        table.setSelectionBackground(buttonBlue);
        table.setSelectionForeground(white);

        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setRowHeight(32);

        table.getTableHeader().setBackground(buttonBlue);
        table.getTableHeader().setForeground(white);

        table.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 13)
        );

        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    }

    // ---------------- LOAD ALL AUCTION PLAYERS ----------------

    private void loadAuctionPlayers() {

        auctionModel.setRowCount(0);

        String sql =
                "SELECT al.lot_id, p.player_id, p.player_name, " +
                "p.category, p.nationality, p.base_price, al.status " +
                "FROM auction_lots al " +
                "JOIN players p ON al.player_id = p.player_id " +
                "ORDER BY al.lot_id";

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                auctionModel.addRow(new Object[]{
                        rs.getInt("lot_id"),
                        rs.getInt("player_id"),
                        rs.getString("player_name"),
                        rs.getString("category"),
                        rs.getString("nationality"),
                        rs.getBigDecimal("base_price"),
                        rs.getString("status")
                });
            }

            applyFilters();

        } catch (Exception e) {

            showError("Unable to load auction players.", e);
        }
    }

    // ---------------- START SELECTED AUCTIONS ----------------

    private void startSelectedAuctions() {

        int[] selectedRows = auctionTable.getSelectedRows();

        if (selectedRows.length == 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Select available players first."
            );

            return;
        }

        if (selectedRows.length > 15) {

            JOptionPane.showMessageDialog(
                    this,
                    "You can start a maximum of 15 players in one batch."
            );

            return;
        }

        ArrayList<Integer> lotIds = new ArrayList<>();

        for (int viewRow : selectedRows) {

            int row = auctionTable.convertRowIndexToModel(viewRow);

            String status =
                    auctionModel.getValueAt(row, 6).toString();

            if (!status.equals("AVAILABLE")) {

                JOptionPane.showMessageDialog(
                        this,
                        "Select only AVAILABLE players to start auction.\n" +
                        "Selected player status: " + status
                );

                return;
            }

            lotIds.add(
                    Integer.parseInt(
                            auctionModel.getValueAt(row, 0).toString()
                    )
            );
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Start auction for " + lotIds.size() + " players?",
                "Confirm Auction Start",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        String sql =
                "UPDATE auction_lots SET status = 'LIVE' " +
                "WHERE lot_id = ? AND status = 'AVAILABLE'";

        executeAuctionBatch(lotIds, sql, "started");
    }

    // ---------------- CLOSE SELECTED AUCTIONS ----------------

    private void closeSelectedAuctions() {

        int[] selectedRows = auctionTable.getSelectedRows();

        if (selectedRows.length == 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Select LIVE players first."
            );

            return;
        }

        ArrayList<Integer> lotIds = new ArrayList<>();

        for (int viewRow : selectedRows) {

            int row = auctionTable.convertRowIndexToModel(viewRow);

            String status =
                    auctionModel.getValueAt(row, 6).toString();

            if (!status.equals("LIVE")) {

                JOptionPane.showMessageDialog(
                        this,
                        "Only LIVE players can be closed.\n" +
                        "Selected player status: " + status
                );

                return;
            }

            lotIds.add(
                    Integer.parseInt(
                            auctionModel.getValueAt(row, 0).toString()
                    )
            );
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Close bidding for " + lotIds.size() + " players?",
                "Confirm Close",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        String sql =
                "UPDATE auction_lots SET status = 'CLOSED' " +
                "WHERE lot_id = ? AND status = 'LIVE'";

        executeAuctionBatch(lotIds, sql, "closed");
    }

    // ---------------- EXECUTE AUCTION BATCH ----------------

    private void executeAuctionBatch(
            ArrayList<Integer> lotIds,
            String sql,
            String action) {

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                throw new SQLException("Database connection failed.");
            }

            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                for (int lotId : lotIds) {

                    ps.setInt(1, lotId);
                    ps.addBatch();
                }

                int[] results = ps.executeBatch();

                for (int result : results) {

                    if (result == 0 ||
                            result == PreparedStatement.EXECUTE_FAILED) {

                        throw new SQLException(
                                "One or more player records could not be updated."
                        );
                    }
                }

                con.commit();

                JOptionPane.showMessageDialog(
                        this,
                        lotIds.size() + " players successfully " + action + "."
                );

            } catch (Exception e) {

                con.rollback();
                throw e;

            } finally {

                con.setAutoCommit(true);
            }

        } catch (Exception e) {

            showError("Unable to update auction status.", e);
        }

        loadAuctionPlayers();
        dashboard.loadStatistics();
    }

    // ---------------- ERROR MESSAGE ----------------

    private void showError(String message, Exception e) {

        JOptionPane.showMessageDialog(
                this,
                message + "\n" + e.getMessage(),
                "Auction Control Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
