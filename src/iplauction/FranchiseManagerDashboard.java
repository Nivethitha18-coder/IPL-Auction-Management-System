
package iplauction;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class FranchiseManagerDashboard extends JFrame {

    private final Color background = new Color(9, 17, 35);
    private final Color panelColor = new Color(18, 31, 57);
    private final Color cardColor = new Color(25, 42, 72);
    private final Color accent = new Color(55, 130, 230);
    private final Color white = Color.WHITE;
    private final Color muted = new Color(160, 178, 205);

    private String username;
    private String fullName;
    private String teamName;
    private int teamId;

    private BigDecimal totalPurse = BigDecimal.ZERO;
    private BigDecimal remainingPurse = BigDecimal.ZERO;

    private JLabel teamTitle;
    private JLabel welcomeLabel;
    private JLabel totalPurseValue;
    private JLabel remainingPurseValue;
    private JLabel spentPurseValue;
    private JLabel squadValue;
    private JLabel liveValue;

    private JTable squadTable;
    private JTable auctionTable;
    private JTable bidsTable;

    private DefaultTableModel squadModel;
    private DefaultTableModel auctionModel;
    private DefaultTableModel bidsModel;

    public FranchiseManagerDashboard(String username) {

        this.username = username;

        setTitle("IPL Auction System - Franchise Manager");
        setSize(1400, 850);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1100, 700));
        setLocationRelativeTo(null);

        // Important: Do not terminate the application when this
        // dashboard is closed during logout.
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        createDashboard();
        refreshDashboard();

        setVisible(true);
    }

    // ================= CREATE DASHBOARD =================

    private void createDashboard() {

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(background);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(panelColor);
        header.setBorder(
            BorderFactory.createEmptyBorder(20, 30, 20, 30)
        );

        JPanel heading = new JPanel(new GridLayout(2, 1));
        heading.setOpaque(false);

        JLabel title = new JLabel("IPL AUCTION SYSTEM");
        title.setForeground(white);
        title.setFont(new Font("Arial", Font.BOLD, 27));

        JLabel subtitle = new JLabel(
            "FRANCHISE MANAGER DASHBOARD"
        );
        subtitle.setForeground(accent);
        subtitle.setFont(new Font("Arial", Font.BOLD, 14));

        heading.add(title);
        heading.add(subtitle);

        JPanel headerRight = new JPanel(
            new FlowLayout(FlowLayout.RIGHT)
        );
        headerRight.setOpaque(false);

        JButton refreshButton = createButton("Refresh");
        JButton logoutButton = createButton("Logout");

        refreshButton.addActionListener(e -> refreshDashboard());
        logoutButton.addActionListener(e -> logout());

        headerRight.add(refreshButton);
        headerRight.add(logoutButton);

        header.add(heading, BorderLayout.WEST);
        header.add(headerRight, BorderLayout.EAST);

        main.add(header, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(15, 15));
        content.setBackground(background);
        content.setBorder(
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        );

        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setBackground(background);

        welcomeLabel = new JLabel("Welcome, Franchise Manager");
        welcomeLabel.setForeground(white);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 23));

        teamTitle = new JLabel("Your Franchise");
        teamTitle.setForeground(muted);
        teamTitle.setFont(new Font("Arial", Font.PLAIN, 15));

        welcomePanel.add(welcomeLabel, BorderLayout.NORTH);
        welcomePanel.add(teamTitle, BorderLayout.SOUTH);

        content.add(welcomePanel, BorderLayout.NORTH);

        // PURSE CARDS

        JPanel cards = new JPanel(new GridLayout(1, 5, 15, 15));
        cards.setBackground(background);

        totalPurseValue = new JLabel("0", SwingConstants.CENTER);
        remainingPurseValue = new JLabel("0", SwingConstants.CENTER);
        spentPurseValue = new JLabel("0", SwingConstants.CENTER);
        squadValue = new JLabel("0", SwingConstants.CENTER);
        liveValue = new JLabel("0", SwingConstants.CENTER);

        cards.add(createCard("TOTAL PURSE", totalPurseValue));
        cards.add(createCard("REMAINING PURSE", remainingPurseValue));
        cards.add(createCard("PURSE USED", spentPurseValue));
        cards.add(createCard("SQUAD PLAYERS", squadValue));
        cards.add(createCard("LIVE AUCTIONS", liveValue));

        // TABLE MODELS

        squadModel = createModel(new String[] {
            "Player ID", "Player Name", "Category",
            "Nationality", "Purchase Type", "Purchase Price"
        });

        squadTable = new JTable(squadModel);
        styleTable(squadTable);

        auctionModel = createModel(new String[] {
            "Lot ID", "Player ID", "Player Name",
            "Category", "Nationality", "Base Price",
            "Round", "Auction Type"
        });

        auctionTable = new JTable(auctionModel);
        styleTable(auctionTable);

        bidsModel = createModel(new String[] {
            "Player Name", "Lot ID", "Bid Amount",
            "Round", "Bid Time", "Auction Status"
        });

        bidsTable = new JTable(bidsModel);
        styleTable(bidsTable);

        // TABS

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Arial", Font.BOLD, 14));
        tabs.setBackground(panelColor);
        tabs.setForeground(white);

        JPanel auctionPanel = new JPanel(
            new BorderLayout(10, 10)
        );
        auctionPanel.setBackground(background);

        auctionPanel.add(
            createTablePanel(auctionTable),
            BorderLayout.CENTER
        );

        JButton bidButton = createButton("Place / Update Bid");
        bidButton.addActionListener(e -> placeBid());

        JPanel bidButtonPanel = new JPanel(
            new FlowLayout(FlowLayout.RIGHT)
        );
        bidButtonPanel.setBackground(background);
        bidButtonPanel.add(bidButton);

        auctionPanel.add(bidButtonPanel, BorderLayout.SOUTH);

        tabs.addTab("My Squad", createTablePanel(squadTable));
        tabs.addTab("Live Auctions", auctionPanel);
        tabs.addTab("My Bids", createTablePanel(bidsTable));

        JPanel center = new JPanel(
            new BorderLayout(15, 20)
        );
        center.setBackground(background);

        center.add(cards, BorderLayout.NORTH);
        center.add(tabs, BorderLayout.CENTER);

        content.add(center, BorderLayout.CENTER);
        main.add(content, BorderLayout.CENTER);

        setContentPane(main);
    }

    // ================= CARD DESIGN =================

    private JPanel createCard(String heading, JLabel value) {

        JPanel card = new JPanel(new GridLayout(2, 1, 5, 5));
        card.setBackground(cardColor);
        card.setBorder(
            BorderFactory.createEmptyBorder(15, 10, 15, 10)
        );

        JLabel title = new JLabel(
            heading, SwingConstants.CENTER
        );
        title.setForeground(muted);
        title.setFont(new Font("Arial", Font.BOLD, 12));

        value.setForeground(white);
        value.setFont(new Font("Arial", Font.BOLD, 22));

        card.add(title);
        card.add(value);

        return card;
    }

    private JButton createButton(String text) {

        JButton button = new JButton(text);
        button.setBackground(accent);
        button.setForeground(white);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorder(
            BorderFactory.createEmptyBorder(10, 18, 10, 18)
        );

        button.setCursor(
            new java.awt.Cursor(
                java.awt.Cursor.HAND_CURSOR
            )
        );

        return button;
    }

    // ================= TABLE DESIGN =================

    private DefaultTableModel createModel(String[] columns) {

        return new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void styleTable(JTable table) {

        table.setRowHeight(32);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setBackground(cardColor);
        table.setForeground(white);
        table.setGridColor(new Color(55, 70, 100));
        table.setSelectionBackground(accent);
        table.setSelectionForeground(white);

        table.getTableHeader().setBackground(panelColor);
        table.getTableHeader().setForeground(white);
        table.getTableHeader().setFont(
            new Font("Arial", Font.BOLD, 13)
        );

        table.getTableHeader().setPreferredSize(
            new Dimension(100, 38)
        );
    }

    private JPanel createTablePanel(JTable table) {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(background);

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(cardColor);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    // ================= LOAD TEAM DETAILS =================

    private void loadTeamDetails() {

        String sql =
            "SELECT u.full_name, t.team_id, t.team_name, " +
            "t.total_purse, t.remaining_purse " +
            "FROM users u " +
            "JOIN teams t ON u.user_id = t.owner_user_id " +
            "WHERE u.username = ? AND u.role = 'OWNER'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    fullName = rs.getString("full_name");
                    teamId = rs.getInt("team_id");
                    teamName = rs.getString("team_name");

                    totalPurse = rs.getBigDecimal("total_purse");
                    remainingPurse = rs.getBigDecimal(
                        "remaining_purse"
                    );

                    if (totalPurse == null) {
                        totalPurse = BigDecimal.ZERO;
                    }

                    if (remainingPurse == null) {
                        remainingPurse = BigDecimal.ZERO;
                    }

                    welcomeLabel.setText("Welcome, " + fullName);
                    teamTitle.setText("Franchise: " + teamName);

                    updatePurseCards();

                } else {

                    teamId = 0;

                    JOptionPane.showMessageDialog(
                        this,
                        "No franchise is assigned to this owner account.",
                        "Team Not Found",
                        JOptionPane.WARNING_MESSAGE
                    );
                }
            }

        } catch (SQLException e) {
            showDatabaseError(e);
        }
    }

    // ================= PURSE CARDS =================

    private void updatePurseCards() {

        BigDecimal spent = totalPurse.subtract(remainingPurse);

        totalPurseValue.setText(totalPurse.toPlainString());
        remainingPurseValue.setText(
            remainingPurse.toPlainString()
        );
        spentPurseValue.setText(spent.toPlainString());
    }

    // ================= REFRESH DASHBOARD =================

    private void refreshDashboard() {

        loadTeamDetails();

        if (teamId == 0) {
            return;
        }

        loadSquad();
        loadLiveAuctions();
        loadMyBids();
    }

    // ================= LOAD SQUAD =================

    private void loadSquad() {

        squadModel.setRowCount(0);

        String sql =
            "SELECT player_id, player_name, category, nationality, " +
            "purchase_type, purchase_price " +
            "FROM players WHERE team_id = ? " +
            "ORDER BY player_name";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, teamId);

            try (ResultSet rs = ps.executeQuery()) {

                int count = 0;

                while (rs.next()) {

                    squadModel.addRow(new Object[] {
                        rs.getInt("player_id"),
                        rs.getString("player_name"),
                        rs.getString("category"),
                        rs.getString("nationality"),
                        rs.getString("purchase_type"),
                        rs.getBigDecimal("purchase_price")
                    });

                    count++;
                }

                squadValue.setText(String.valueOf(count));
            }

        } catch (SQLException e) {
            showDatabaseError(e);
        }
    }

    // ================= LOAD LIVE AUCTIONS =================

    private void loadLiveAuctions() {

        auctionModel.setRowCount(0);

        String sql =
            "SELECT al.lot_id, p.player_id, p.player_name, " +
            "p.category, p.nationality, p.base_price, " +
            "al.current_round, al.tie_breaker_active " +
            "FROM auction_lots al " +
            "JOIN players p ON al.player_id = p.player_id " +
            "WHERE al.status = 'LIVE' AND p.team_id IS NULL " +
            "ORDER BY al.lot_id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            int count = 0;

            while (rs.next()) {

                int currentRound = rs.getInt("current_round");

                boolean tieBreaker =
                    rs.getBoolean("tie_breaker_active");

                auctionModel.addRow(new Object[] {
                    rs.getInt("lot_id"),
                    rs.getInt("player_id"),
                    rs.getString("player_name"),
                    rs.getString("category"),
                    rs.getString("nationality"),
                    rs.getBigDecimal("base_price"),
                    currentRound,
                    tieBreaker ? "TIE-BREAKER" : "NORMAL"
                });

                count++;
            }

            liveValue.setText(String.valueOf(count));

        } catch (SQLException e) {
            showDatabaseError(e);
        }
    }

    // ================= LOAD OWNER BIDS =================

    private void loadMyBids() {

        bidsModel.setRowCount(0);

        String sql =
            "SELECT p.player_name, b.lot_id, " +
            "b.bid_amount, b.bid_round, b.bid_time, al.status " +
            "FROM bids b " +
            "JOIN auction_lots al ON b.lot_id = al.lot_id " +
            "JOIN players p ON al.player_id = p.player_id " +
            "WHERE b.team_id = ? " +
            "ORDER BY b.bid_time DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, teamId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    bidsModel.addRow(new Object[] {
                        rs.getString("player_name"),
                        rs.getInt("lot_id"),
                        rs.getBigDecimal("bid_amount"),
                        rs.getInt("bid_round"),
                        rs.getTimestamp("bid_time"),
                        rs.getString("status")
                    });
                }
            }

        } catch (SQLException e) {
            showDatabaseError(e);
        }
    }

    // ================= PLACE OR UPDATE BID =================

    private void placeBid() {

        int selectedRow = auctionTable.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                this,
                "Please select a LIVE auction player.",
                "Select Player",
                JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int row = auctionTable.convertRowIndexToModel(selectedRow);

        int lotId = Integer.parseInt(
            auctionModel.getValueAt(row, 0).toString()
        );

        String playerName =
            auctionModel.getValueAt(row, 2).toString();

        BigDecimal basePrice = new BigDecimal(
            auctionModel.getValueAt(row, 5).toString()
        );

        JTextField bidField = new JTextField();

        int choice = JOptionPane.showConfirmDialog(
            this,
            new Object[] {
                "Player: " + playerName,
                "Base Price: " + basePrice,
                "Bidding Round: " +
                    auctionModel.getValueAt(row, 6),
                "Auction Type: " +
                    auctionModel.getValueAt(row, 7),
                "Enter Your Bid Amount:",
                bidField
            },
            "Place / Update Bid",
            JOptionPane.OK_CANCEL_OPTION
        );

        if (choice != JOptionPane.OK_OPTION) {
            return;
        }

        BigDecimal bidAmount;

        try {

            bidAmount = new BigDecimal(
                bidField.getText().trim()
            );

            if (bidAmount.compareTo(BigDecimal.ZERO) <= 0) {

                JOptionPane.showMessageDialog(
                    this,
                    "Bid amount must be greater than zero."
                );

                return;
            }

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                this,
                "Enter a valid bid amount."
            );

            return;
        }

        saveBid(lotId, basePrice, bidAmount);
    }

    // ================= SAVE BID =================

    private void saveBid(
        int lotId,
        BigDecimal basePrice,
        BigDecimal bidAmount
    ) {

        String statusSql =
            "SELECT al.status, al.current_round, " +
            "al.tie_breaker_active, p.base_price, p.team_id " +
            "FROM auction_lots al " +
            "JOIN players p ON al.player_id = p.player_id " +
            "WHERE al.lot_id = ? FOR UPDATE";

        String purseSql =
            "SELECT remaining_purse FROM teams " +
            "WHERE team_id = ? FOR UPDATE";

        String commitmentSql =
            "SELECT COALESCE(SUM(b.bid_amount), 0) " +
            "FROM bids b " +
            "JOIN ( " +
            "SELECT lot_id, team_id, MAX(bid_id) AS latest_id " +
            "FROM bids WHERE team_id = ? GROUP BY lot_id, team_id " +
            ") latest ON b.bid_id = latest.latest_id " +
            "JOIN auction_lots al ON b.lot_id = al.lot_id " +
            "WHERE al.status = 'LIVE' AND b.lot_id <> ?";

        String eligibleSql =
            "SELECT COUNT(*) FROM bids " +
            "WHERE lot_id = ? AND bid_round = ? " +
            "AND bid_amount = ( " +
            "SELECT MAX(bid_amount) FROM bids " +
            "WHERE lot_id = ? AND bid_round = ? " +
            ") AND team_id = ?";

        String highestSql =
            "SELECT MAX(bid_amount) FROM bids " +
            "WHERE lot_id = ? AND bid_round = ?";

        String existingSql =
            "SELECT bid_id, bid_amount FROM bids " +
            "WHERE lot_id = ? AND team_id = ? " +
            "AND bid_round = ?";

        String insertSql =
            "INSERT INTO bids " +
            "(lot_id, team_id, bid_amount, bid_time, bid_round) " +
            "VALUES (?, ?, ?, NOW(), ?)";

        String updateSql =
            "UPDATE bids SET bid_amount = ?, bid_time = NOW() " +
            "WHERE bid_id = ?";

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try {

                String status;
                int currentRound;
                boolean tieBreakerActive;
                int playerTeamId;
                BigDecimal databaseBasePrice;

                // CHECK AUCTION STATUS AND ROUND

                try (PreparedStatement ps =
                         con.prepareStatement(statusSql)) {

                    ps.setInt(1, lotId);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {
                            throw new SQLException(
                                "Auction lot does not exist."
                            );
                        }

                        status = rs.getString("status");
                        currentRound = rs.getInt("current_round");

                        tieBreakerActive =
                            rs.getBoolean("tie_breaker_active");

                        databaseBasePrice =
                            rs.getBigDecimal("base_price");

                        playerTeamId = rs.getInt("team_id");
                    }
                }

                if (!"LIVE".equalsIgnoreCase(status)) {
                    throw new SQLException(
                        "Bidding is closed for this player."
                    );
                }

                if (playerTeamId != 0) {
                    throw new SQLException(
                        "This player has already been assigned."
                    );
                }

                // CHECK TIE-BREAKER ELIGIBILITY

                if (tieBreakerActive) {

                    if (currentRound <= 1) {
                        throw new SQLException(
                            "Invalid tie-breaker round."
                        );
                    }

                    int previousRound = currentRound - 1;
                    int eligibleCount = 0;

                    try (PreparedStatement ps =
                             con.prepareStatement(eligibleSql)) {

                        ps.setInt(1, lotId);
                        ps.setInt(2, previousRound);
                        ps.setInt(3, lotId);
                        ps.setInt(4, previousRound);
                        ps.setInt(5, teamId);

                        try (ResultSet rs = ps.executeQuery()) {

                            if (rs.next()) {
                                eligibleCount = rs.getInt(1);
                            }
                        }
                    }

                    if (eligibleCount == 0) {
                        throw new SQLException(
                            "You are not eligible for this tie-breaker. " +
                            "Only teams tied at the previous highest bid " +
                            "can participate."
                        );
                    }

                    // BID MUST EXCEED PREVIOUS ROUND HIGHEST BID

                    BigDecimal previousHighest = BigDecimal.ZERO;

                    try (PreparedStatement ps =
                             con.prepareStatement(highestSql)) {

                        ps.setInt(1, lotId);
                        ps.setInt(2, previousRound);

                        try (ResultSet rs = ps.executeQuery()) {

                            if (rs.next()) {
                                previousHighest = rs.getBigDecimal(1);
                            }
                        }
                    }

                    if (previousHighest == null) {
                        throw new SQLException(
                            "Previous round bid information not found."
                        );
                    }

                    if (bidAmount.compareTo(previousHighest) <= 0) {
                        throw new SQLException(
                            "Your tie-breaker bid must be greater than " +
                            "the previous highest bid of " +
                            previousHighest.toPlainString()
                        );
                    }

                } else {

                    // NORMAL ROUND BASE PRICE CHECK

                    if (bidAmount.compareTo(databaseBasePrice) < 0) {
                        throw new SQLException(
                            "Bid must be equal to or greater than base price."
                        );
                    }
                }

                // CHECK EXISTING BID IN CURRENT ROUND

                int existingBidId = 0;
                BigDecimal previousOwnBid = null;

                try (PreparedStatement ps =
                         con.prepareStatement(existingSql)) {

                    ps.setInt(1, lotId);
                    ps.setInt(2, teamId);
                    ps.setInt(3, currentRound);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (rs.next()) {
                            existingBidId = rs.getInt("bid_id");
                            previousOwnBid =
                                rs.getBigDecimal("bid_amount");
                        }
                    }
                }

                if (previousOwnBid != null &&
                    bidAmount.compareTo(previousOwnBid) <= 0) {

                    throw new SQLException(
                        "Your updated bid must be greater than your " +
                        "previous bid in this round."
                    );
                }

                // CHECK TEAM PURSE

                BigDecimal currentPurse = BigDecimal.ZERO;

                try (PreparedStatement ps =
                         con.prepareStatement(purseSql)) {

                    ps.setInt(1, teamId);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {
                            throw new SQLException(
                                "Franchise purse not found."
                            );
                        }

                        currentPurse =
                            rs.getBigDecimal("remaining_purse");
                    }
                }

                if (currentPurse == null) {
                    currentPurse = BigDecimal.ZERO;
                }

                // CHECK OTHER LIVE AUCTION COMMITMENTS

                BigDecimal committedAmount = BigDecimal.ZERO;

                try (PreparedStatement ps =
                         con.prepareStatement(commitmentSql)) {

                    ps.setInt(1, teamId);
                    ps.setInt(2, lotId);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (rs.next()) {
                            committedAmount = rs.getBigDecimal(1);
                        }
                    }
                }

                if (committedAmount == null) {
                    committedAmount = BigDecimal.ZERO;
                }

                BigDecimal totalCommitment =
                    committedAmount.add(bidAmount);

                if (totalCommitment.compareTo(currentPurse) > 0) {
                    throw new SQLException(
                        "Insufficient purse! Your other LIVE bids " +
                        "and this bid exceed your remaining purse."
                    );
                }

                // INSERT NEW BID OR UPDATE CURRENT ROUND BID

                if (existingBidId != 0) {

                    try (PreparedStatement ps =
                             con.prepareStatement(updateSql)) {

                        ps.setBigDecimal(1, bidAmount);
                        ps.setInt(2, existingBidId);

                        ps.executeUpdate();
                    }

                } else {

                    try (PreparedStatement ps =
                             con.prepareStatement(insertSql)) {

                        ps.setInt(1, lotId);
                        ps.setInt(2, teamId);
                        ps.setBigDecimal(3, bidAmount);
                        ps.setInt(4, currentRound);

                        ps.executeUpdate();
                    }
                }

                con.commit();

                JOptionPane.showMessageDialog(
                    this,
                    "Your bid has been " +
                    (existingBidId != 0 ? "updated" : "placed") +
                    " successfully!\nBidding Round: " + currentRound,
                    "Bid Successful",
                    JOptionPane.INFORMATION_MESSAGE
                );

                refreshDashboard();

            } catch (SQLException e) {

                con.rollback();

                JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Bid Not Accepted",
                    JOptionPane.WARNING_MESSAGE
                );

            } finally {

                con.setAutoCommit(true);
            }

        } catch (SQLException e) {

            showDatabaseError(e);
        }
    }

    // ================= LOGOUT =================

    private void logout() {

        int choice = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {

            dispose();

            SwingUtilities.invokeLater(() -> {
                LoginFrame login = new LoginFrame();
                login.setVisible(true);
            });
        }
    }

    // ================= DATABASE ERROR =================

    private void showDatabaseError(SQLException e) {

        JOptionPane.showMessageDialog(
            this,
            e.getMessage(),
            "Database Error",
            JOptionPane.ERROR_MESSAGE
        );

        e.printStackTrace();
    }
}
