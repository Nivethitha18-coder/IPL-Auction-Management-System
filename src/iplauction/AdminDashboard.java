
package iplauction;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminDashboard extends JFrame {

    private Color darkBlue = new Color(13, 27, 52);
    private Color panelBlue = new Color(25, 45, 75);
    private Color buttonBlue = new Color(40, 105, 190);
    private Color lightBlue = new Color(75, 155, 235);
    private Color white = Color.WHITE;
    private Color mutedBlue = new Color(175, 195, 220);
    private Color borderBlue = new Color(55, 80, 115);
    private Color gold = new Color(205, 170, 95);

    private JLabel totalPlayers;
    private JLabel availablePlayers;
    private JLabel soldPlayers;
    private JLabel totalBids;
    private JLabel liveLabel;

    private JTable playerTable;
    private DefaultTableModel playerModel;

    private JPanel franchiseCardsPanel;
    private JLabel selectedPlayerLabel;
    private JLabel highestBidLabel;
    private JLabel auctionStatusLabel;

    private int selectedLotId = -1;
    private int selectedPlayerId = -1;
    private int selectedCurrentRound = 1;

    private String selectedPlayerName = "";

    private double highestBid = 0;
    private int highestTeamId = -1;
    private String highestTeamName = "";
    private int highestBidCount = 0;

    private JFrame bidsFrame;

    public AdminDashboard() {

        setTitle("IPL Auction System - Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 750);
        setMinimumSize(new Dimension(1000, 650));
        setLocationRelativeTo(null);
        setResizable(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        getContentPane().setBackground(darkBlue);
        setLayout(new BorderLayout());

        createHeader();
        createDashboard();
        loadStatistics();
    }

    // ---------------- WINDOW SETTINGS ----------------

    private void maximizeFrame(JFrame frame) {

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1200, 750);
        frame.setMinimumSize(new Dimension(900, 600));
        frame.setLocationRelativeTo(null);
        frame.setResizable(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    private JFrame createChildFrame(String title) {

        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1200, 750);
        frame.setMinimumSize(new Dimension(900, 600));
        frame.setLocationRelativeTo(this);
        frame.setResizable(true);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(darkBlue);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        return frame;
    }

    // ---------------- HEADER ----------------

    private void createHeader() {

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(panelBlue);
        header.setBorder(new EmptyBorder(18, 35, 18, 35));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(panelBlue);

        JLabel title = new JLabel("IPL AUCTION SYSTEM");
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setForeground(white);

        JLabel subtitle = new JLabel("ADMINISTRATION PORTAL");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitle.setForeground(lightBlue);

        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subtitle);

        JButton logoutButton = createButton("LOGOUT");

        logoutButton.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        header.add(titlePanel, BorderLayout.WEST);
        header.add(logoutButton, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);
    }

    // ---------------- MAIN DASHBOARD ----------------

    private void createDashboard() {

        JPanel mainPanel = new JPanel(new BorderLayout(20, 22));
        mainPanel.setBackground(darkBlue);
        mainPanel.setBorder(new EmptyBorder(25, 35, 25, 35));

        JPanel welcomePanel = new JPanel();
        welcomePanel.setLayout(new BoxLayout(welcomePanel, BoxLayout.Y_AXIS));
        welcomePanel.setBackground(darkBlue);

        JLabel welcome = new JLabel("Welcome, Admin");
        welcome.setFont(new Font("Arial", Font.BOLD, 27));
        welcome.setForeground(white);

        JLabel description = new JLabel(
                "Manage teams, players and auction activities"
        );
        description.setFont(new Font("Arial", Font.PLAIN, 14));
        description.setForeground(mutedBlue);

        welcomePanel.add(welcome);
        welcomePanel.add(Box.createVerticalStrut(7));
        welcomePanel.add(description);

        JPanel statisticsPanel = createStatisticsPanel();

        JPanel actionSection = new JPanel(new BorderLayout(0, 15));
        actionSection.setBackground(darkBlue);

        JLabel actionTitle = new JLabel("Quick Actions");
        actionTitle.setFont(new Font("Arial", Font.BOLD, 21));
        actionTitle.setForeground(white);

        actionSection.add(actionTitle, BorderLayout.NORTH);
        actionSection.add(createActionCards(), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(panelBlue);
        bottomPanel.setBorder(new EmptyBorder(13, 20, 13, 20));

        liveLabel = new JLabel("Live Auctions: 0");
        liveLabel.setFont(new Font("Arial", Font.BOLD, 14));
        liveLabel.setForeground(lightBlue);

        JLabel footer = new JLabel("IPL Auction Management System");
        footer.setFont(new Font("Arial", Font.PLAIN, 12));
        footer.setForeground(mutedBlue);

        bottomPanel.add(liveLabel, BorderLayout.WEST);
        bottomPanel.add(footer, BorderLayout.EAST);

        JPanel content = new JPanel(new BorderLayout(0, 22));
        content.setBackground(darkBlue);
        content.add(welcomePanel, BorderLayout.NORTH);

        JPanel centerContent = new JPanel(new BorderLayout(0, 22));
        centerContent.setBackground(darkBlue);
        centerContent.add(statisticsPanel, BorderLayout.NORTH);
        centerContent.add(actionSection, BorderLayout.CENTER);

        content.add(centerContent, BorderLayout.CENTER);
        content.add(bottomPanel, BorderLayout.SOUTH);

        mainPanel.add(content, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }

    // ---------------- STATISTICS ----------------

    private JPanel createStatisticsPanel() {

        JPanel panel = new JPanel(new GridLayout(1, 4, 18, 0));
        panel.setBackground(darkBlue);

        totalPlayers = new JLabel("0");
        availablePlayers = new JLabel("0");
        soldPlayers = new JLabel("0");
        totalBids = new JLabel("0");

        panel.add(createStatCard("TOTAL PLAYERS", totalPlayers));
        panel.add(createStatCard("AVAILABLE PLAYERS", availablePlayers));
        panel.add(createStatCard("SOLD PLAYERS", soldPlayers));
        panel.add(createStatCard("TOTAL BIDS", totalBids));

        return panel;
    }

    private JPanel createStatCard(String heading, JLabel valueLabel) {

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(panelBlue);

        card.setBorder(new CompoundBorder(
                new LineBorder(borderBlue, 1, true),
                new EmptyBorder(20, 22, 20, 22)
        ));

        JLabel title = new JLabel(heading);
        title.setFont(new Font("Arial", Font.BOLD, 12));
        title.setForeground(mutedBlue);

        valueLabel.setFont(new Font("Arial", Font.BOLD, 30));
        valueLabel.setForeground(white);

        card.add(title);
        card.add(Box.createVerticalStrut(12));
        card.add(valueLabel);

        return card;
    }

    public void loadStatistics() {

        String playerQuery = "SELECT COUNT(*) FROM players";

        String availableQuery =
                "SELECT COUNT(*) FROM players " +
                "WHERE team_id IS NULL AND purchase_type IS NULL";

        String soldQuery =
                "SELECT COUNT(*) FROM auction_lots WHERE status='SOLD'";

        String bidQuery = "SELECT COUNT(*) FROM bids";

        String liveQuery =
                "SELECT COUNT(*) FROM auction_lots WHERE status='LIVE'";

        try (Connection con = DBConnection.getConnection()) {

            totalPlayers.setText(String.valueOf(getCount(con, playerQuery)));
            availablePlayers.setText(String.valueOf(getCount(con, availableQuery)));
            soldPlayers.setText(String.valueOf(getCount(con, soldQuery)));
            totalBids.setText(String.valueOf(getCount(con, bidQuery)));

            liveLabel.setText(
                    "Live Auctions: " + getCount(con, liveQuery)
            );

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load dashboard statistics.\n" + e.getMessage()
            );
        }
    }

    private int getCount(Connection con, String query)
            throws SQLException {

        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        }

        return 0;
    }

    // ---------------- ACTION CARDS ----------------

    private JPanel createActionCards() {

        JPanel panel = new JPanel(new GridLayout(2, 4, 18, 18));
        panel.setBackground(darkBlue);

        JButton managePlayers = createActionCard(
                "👥", "MANAGE PLAYERS",
                "Add, edit and view player details"
        );

        JButton addTeam = createActionCard(
                "🛡", "TEAM DETAILS",
                "View existing IPL team details"
        );

        JButton auctionControl = createActionCard(
                "🔨", "AUCTION CONTROL",
                "Start and close player bidding"
        );

        JButton viewBids = createActionCard(
                "📊", "VIEW BIDS",
                "View bids and assign players"
        );

        JButton refresh = createActionCard(
                "🔄", "REFRESH DASHBOARD",
                "Update dashboard statistics"
        );

        JButton reports = createActionCard(
                "🏆", "AUCTION SUMMARY",
                "View sold player and team details"
        );

        JButton passwordRequests = createActionCard(
                "🔑", "PASSWORD REQUESTS",
                "Review owner forgot-password requests"
        );

        JButton constraints = createActionCard(
                "⚙", "SQUAD CONSTRAINTS",
                "Configure common squad rules"
        );

        managePlayers.addActionListener(e -> {
            ManagePlayersFrame frame = new ManagePlayersFrame();
            maximizeFrame(frame);
            frame.setVisible(true);
        });

        addTeam.addActionListener(e -> {
            AddTeamFrame frame = new AddTeamFrame(this);
            maximizeFrame(frame);
            frame.setVisible(true);
        });

        auctionControl.addActionListener(e -> {
            AuctionControlFrame frame = new AuctionControlFrame(this);
            maximizeFrame(frame);
            frame.setVisible(true);
        });

        viewBids.addActionListener(e -> showBids());
        refresh.addActionListener(e -> loadStatistics());
        reports.addActionListener(e -> showAuctionSummary());
        passwordRequests.addActionListener(e -> showPasswordRequests());
        constraints.addActionListener(e -> showSquadConstraints());

        panel.add(managePlayers);
        panel.add(addTeam);
        panel.add(auctionControl);
        panel.add(viewBids);
        panel.add(refresh);
        panel.add(reports);
        panel.add(passwordRequests);
        panel.add(constraints);

        return panel;
    }

    private JButton createActionCard(
            String icon, String heading, String description) {

        JButton button = new JButton(
                "<html><div style='text-align:center;'>" +
                "<span style='font-size:30px;'>" + icon + "</span><br><br>" +
                "<b style='font-size:15px;'>" + heading + "</b><br><br>" +
                "<span style='font-size:11px;'>" + description + "</span>" +
                "</div></html>"
        );

        button.setBackground(panelBlue);
        button.setForeground(white);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setFocusPainted(false);
        button.setBorder(new CompoundBorder(
                new LineBorder(borderBlue, 1, true),
                new EmptyBorder(15, 12, 15, 12)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(buttonBlue);
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(panelBlue);
            }
        });

        return button;
    }

    private JButton createButton(String text) {

        JButton button = new JButton(text);
        button.setBackground(buttonBlue);
        button.setForeground(white);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(11, 20, 11, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    // ---------------- SQUAD CONSTRAINTS ----------------

    private void showSquadConstraints() {

        JFrame frame = createChildFrame("Squad Constraints");

        JLabel title = createScreenTitle("SQUAD CONSTRAINTS");

        JPanel form = new JPanel(new GridLayout(7, 2, 20, 20));
        form.setBackground(panelBlue);
        form.setBorder(new EmptyBorder(35, 60, 35, 60));

        JTextField min = new JTextField();
        JTextField max = new JTextField();
        JTextField overseas = new JTextField();
        JTextField batsmen = new JTextField();
        JTextField bowlers = new JTextField();
        JTextField allRounders = new JTextField();
        JTextField keepers = new JTextField();

        addConstraintField(form, "Minimum Squad Size", min);
        addConstraintField(form, "Maximum Squad Size", max);
        addConstraintField(form, "Maximum Overseas Players", overseas);
        addConstraintField(form, "Maximum Batsmen", batsmen);
        addConstraintField(form, "Maximum Bowlers", bowlers);
        addConstraintField(form, "Maximum All-Rounders", allRounders);
        addConstraintField(form, "Maximum Wicket-Keepers", keepers);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        bottom.setBackground(panelBlue);

        JButton save = createButton("SAVE CONSTRAINTS");
        JButton close = createButton("CLOSE");

        save.addActionListener(e -> {

            try {

                int minValue = Integer.parseInt(min.getText().trim());
                int maxValue = Integer.parseInt(max.getText().trim());
                int overseasValue = Integer.parseInt(overseas.getText().trim());
                int batsmenValue = Integer.parseInt(batsmen.getText().trim());
                int bowlersValue = Integer.parseInt(bowlers.getText().trim());
                int allRoundersValue = Integer.parseInt(allRounders.getText().trim());
                int keepersValue = Integer.parseInt(keepers.getText().trim());

                if (minValue < 1 || maxValue < minValue || maxValue > 100
                        || overseasValue < 0 || overseasValue > maxValue
                        || batsmenValue < 0 || batsmenValue > maxValue
                        || bowlersValue < 0 || bowlersValue > maxValue
                        || allRoundersValue < 0 || allRoundersValue > maxValue
                        || keepersValue < 0 || keepersValue > maxValue) {

                    JOptionPane.showMessageDialog(
                            frame,
                            "Please enter valid squad limits."
                    );
                    return;
                }

                String query =
                        "UPDATE users SET min_squad_size=?, max_squad_size=?, " +
                        "max_overseas=?, max_batsmen=?, max_bowlers=?, " +
                        "max_all_rounders=?, max_wicket_keepers=? " +
                        "WHERE role='ADMIN'";

                try (Connection con = DBConnection.getConnection();
                     PreparedStatement ps = con.prepareStatement(query)) {

                    ps.setInt(1, minValue);
                    ps.setInt(2, maxValue);
                    ps.setInt(3, overseasValue);
                    ps.setInt(4, batsmenValue);
                    ps.setInt(5, bowlersValue);
                    ps.setInt(6, allRoundersValue);
                    ps.setInt(7, keepersValue);

                    int updated = ps.executeUpdate();

                    JOptionPane.showMessageDialog(
                            frame,
                            updated > 0
                                    ? "Squad constraints saved successfully!"
                                    : "Admin account not found."
                    );
                }

            } catch (NumberFormatException ex) {

                JOptionPane.showMessageDialog(
                        frame,
                        "Enter valid whole numbers."
                );

            } catch (SQLException ex) {

                JOptionPane.showMessageDialog(
                        frame,
                        "Unable to save constraints.\n" + ex.getMessage()
                );
            }
        });

        close.addActionListener(e -> frame.dispose());

        bottom.add(save);
        bottom.add(close);

        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(darkBlue);
        center.add(form);

        frame.add(title, BorderLayout.NORTH);
        frame.add(center, BorderLayout.CENTER);
        frame.add(bottom, BorderLayout.SOUTH);

        loadConstraintFields(
                frame, min, max, overseas, batsmen,
                bowlers, allRounders, keepers
        );

        frame.setVisible(true);
    }

    private JLabel createScreenTitle(String text) {

        JLabel title = new JLabel(text);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(gold);
        title.setOpaque(true);
        title.setBackground(panelBlue);
        title.setBorder(new EmptyBorder(22, 30, 22, 30));

        return title;
    }

    private void addConstraintField(
            JPanel panel, String name, JTextField field) {

        JLabel label = new JLabel(name);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(white);

        field.setFont(new Font("Arial", Font.PLAIN, 16));
        field.setPreferredSize(new Dimension(220, 38));

        panel.add(label);
        panel.add(field);
    }

    private void loadConstraintFields(
            JFrame frame,
            JTextField min,
            JTextField max,
            JTextField overseas,
            JTextField batsmen,
            JTextField bowlers,
            JTextField allRounders,
            JTextField keepers) {

        String query =
                "SELECT min_squad_size, max_squad_size, max_overseas, " +
                "max_batsmen, max_bowlers, max_all_rounders, " +
                "max_wicket_keepers FROM users " +
                "WHERE role='ADMIN' LIMIT 1";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                min.setText(String.valueOf(rs.getInt("min_squad_size")));
                max.setText(String.valueOf(rs.getInt("max_squad_size")));
                overseas.setText(String.valueOf(rs.getInt("max_overseas")));
                batsmen.setText(String.valueOf(rs.getInt("max_batsmen")));
                bowlers.setText(String.valueOf(rs.getInt("max_bowlers")));
                allRounders.setText(String.valueOf(rs.getInt("max_all_rounders")));
                keepers.setText(String.valueOf(rs.getInt("max_wicket_keepers")));
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Unable to load constraints.\n" + e.getMessage()
            );
        }
    }

    // ---------------- VIEW BIDS ----------------

    private void showBids() {

        bidsFrame = createChildFrame("Auction Bid Management");

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(panelBlue);
        header.setBorder(new EmptyBorder(18, 25, 18, 25));

        JPanel headingPanel = new JPanel();
        headingPanel.setLayout(new BoxLayout(headingPanel, BoxLayout.Y_AXIS));
        headingPanel.setBackground(panelBlue);

        JLabel title = new JLabel("AUCTION BID MANAGEMENT");
        title.setFont(new Font("Arial", Font.BOLD, 23));
        title.setForeground(white);

        JLabel subtitle = new JLabel(
                "View franchise bids, tie-breaker rounds and assign players"
        );
        subtitle.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitle.setForeground(mutedBlue);

        headingPanel.add(title);
        headingPanel.add(Box.createVerticalStrut(5));
        headingPanel.add(subtitle);

        header.add(headingPanel, BorderLayout.WEST);
        bidsFrame.add(header, BorderLayout.NORTH);

        JPanel leftPanel = new JPanel(new BorderLayout(0, 12));
        leftPanel.setBackground(panelBlue);
        leftPanel.setBorder(new EmptyBorder(18, 18, 18, 18));
        leftPanel.setPreferredSize(new Dimension(300, 600));

        JLabel playerHeading = new JLabel("AUCTION PLAYERS");
        playerHeading.setFont(new Font("Arial", Font.BOLD, 15));
        playerHeading.setForeground(white);

        playerModel = new DefaultTableModel(
                new String[]{"LOT", "PLAYER", "STATUS"}, 0
        ) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        playerTable = new JTable(playerModel);
        playerTable.setBackground(new Color(20, 38, 65));
        playerTable.setForeground(white);
        playerTable.setSelectionBackground(buttonBlue);
        playerTable.setSelectionForeground(white);
        playerTable.setRowHeight(38);
        playerTable.setFont(new Font("Arial", Font.PLAIN, 12));
        playerTable.setGridColor(borderBlue);
        playerTable.setShowVerticalLines(false);

        playerTable.getTableHeader().setBackground(darkBlue);
        playerTable.getTableHeader().setForeground(white);
        playerTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));

        JScrollPane playerScroll = new JScrollPane(playerTable);
        playerScroll.setBorder(new LineBorder(borderBlue));

        leftPanel.add(playerHeading, BorderLayout.NORTH);
        leftPanel.add(playerScroll, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout(0, 15));
        rightPanel.setBackground(darkBlue);
        rightPanel.setBorder(new EmptyBorder(18, 18, 18, 18));

        JPanel playerInfoPanel = new JPanel(new BorderLayout());
        playerInfoPanel.setBackground(panelBlue);
        playerInfoPanel.setBorder(new EmptyBorder(15, 18, 15, 18));

        selectedPlayerLabel = new JLabel("Select a player");
        selectedPlayerLabel.setFont(new Font("Arial", Font.BOLD, 21));
        selectedPlayerLabel.setForeground(white);

        auctionStatusLabel = new JLabel(" ");
        auctionStatusLabel.setFont(new Font("Arial", Font.BOLD, 12));
        auctionStatusLabel.setForeground(lightBlue);

        JPanel playerInfoLeft = new JPanel();
        playerInfoLeft.setLayout(new BoxLayout(playerInfoLeft, BoxLayout.Y_AXIS));
        playerInfoLeft.setBackground(panelBlue);
        playerInfoLeft.add(selectedPlayerLabel);
        playerInfoLeft.add(Box.createVerticalStrut(7));
        playerInfoLeft.add(auctionStatusLabel);

        highestBidLabel = new JLabel("Highest Bid: ₹0");
        highestBidLabel.setFont(new Font("Arial", Font.BOLD, 16));
        highestBidLabel.setForeground(new Color(90, 220, 140));

        playerInfoPanel.add(playerInfoLeft, BorderLayout.WEST);
        playerInfoPanel.add(highestBidLabel, BorderLayout.EAST);

        franchiseCardsPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        franchiseCardsPanel.setBackground(darkBlue);

        JScrollPane cardsScroll = new JScrollPane(franchiseCardsPanel);
        cardsScroll.setBorder(null);
        cardsScroll.getViewport().setBackground(darkBlue);
        cardsScroll.getVerticalScrollBar().setUnitIncrement(15);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 15));
        centerPanel.setBackground(darkBlue);
        centerPanel.add(playerInfoPanel, BorderLayout.NORTH);
        centerPanel.add(cardsScroll, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 10, 10)
        );
        actionPanel.setBackground(panelBlue);

        JButton tieBreakerButton = createButton("START TIE-BREAKER");
        JButton assignButton = createButton("ASSIGN TO HIGHEST BIDDER");
        JButton unsoldButton = createButton("MARK AS UNSOLD");
        JButton refreshButton = createButton("REFRESH");
        JButton closeButton = createButton("CLOSE");

        tieBreakerButton.addActionListener(e -> startTieBreaker());
        assignButton.addActionListener(e -> assignHighestBidder());
        unsoldButton.addActionListener(e -> markPlayerUnsold());

        refreshButton.addActionListener(e -> {
            loadAuctionPlayers();

            if (selectedLotId != -1) {
                showSelectedPlayerBids();
            }
        });

        closeButton.addActionListener(e -> bidsFrame.dispose());

        actionPanel.add(tieBreakerButton);
        actionPanel.add(assignButton);
        actionPanel.add(unsoldButton);
        actionPanel.add(refreshButton);
        actionPanel.add(closeButton);

        rightPanel.add(centerPanel, BorderLayout.CENTER);
        rightPanel.add(actionPanel, BorderLayout.SOUTH);

        JPanel body = new JPanel(new BorderLayout(15, 0));
        body.setBackground(darkBlue);
        body.setBorder(new EmptyBorder(15, 15, 15, 15));
        body.add(leftPanel, BorderLayout.WEST);
        body.add(rightPanel, BorderLayout.CENTER);

        bidsFrame.add(body, BorderLayout.CENTER);

        playerTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = playerTable.getSelectedRow();

                if (row >= 0) {
                    selectedLotId = Integer.parseInt(
                            playerModel.getValueAt(row, 0).toString()
                    );

                    selectedPlayerId = -1;
                    showSelectedPlayerBids();
                }
            }
        });

        loadAuctionPlayers();
        bidsFrame.setVisible(true);
    }

    private void loadAuctionPlayers() {

        playerModel.setRowCount(0);

        String query =
                "SELECT al.lot_id, p.player_name, al.status " +
                "FROM auction_lots al " +
                "JOIN players p ON al.player_id=p.player_id " +
                "WHERE al.status IN ('LIVE','CLOSED') " +
                "ORDER BY al.lot_id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                playerModel.addRow(new Object[]{
                        rs.getInt("lot_id"),
                        rs.getString("player_name"),
                        rs.getString("status")
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    bidsFrame,
                    "Unable to load auction players.\n" + e.getMessage()
            );
        }
    }

    private void showSelectedPlayerBids() {

        franchiseCardsPanel.removeAll();

        highestBid = 0;
        highestTeamId = -1;
        highestTeamName = "";
        highestBidCount = 0;

        String playerQuery =
                "SELECT p.player_id, p.player_name, al.status, " +
                "al.current_round, al.tie_breaker_active " +
                "FROM auction_lots al " +
                "JOIN players p ON al.player_id=p.player_id " +
                "WHERE al.lot_id=?";

        try (Connection con = DBConnection.getConnection()) {

            try (PreparedStatement ps = con.prepareStatement(playerQuery)) {

                ps.setInt(1, selectedLotId);

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {

                        selectedPlayerId = rs.getInt("player_id");
                        selectedPlayerName = rs.getString("player_name");
                        selectedCurrentRound = rs.getInt("current_round");

                        String status = rs.getString("status");
                        boolean tieBreakerActive =
                                rs.getBoolean("tie_breaker_active");

                        selectedPlayerLabel.setText(selectedPlayerName);

                        auctionStatusLabel.setText(
                                "STATUS: " + status +
                                " | ROUND: " + selectedCurrentRound +
                                (tieBreakerActive ? " | TIE-BREAKER ROUND" : "")
                        );

                    } else {
                        return;
                    }
                }
            }

            String teamQuery =
                    "SELECT team_id, team_name FROM teams ORDER BY team_name";

            try (PreparedStatement ps = con.prepareStatement(teamQuery);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    createFranchiseBidCard(
                            con,
                            rs.getInt("team_id"),
                            rs.getString("team_name")
                    );
                }
            }

            if (highestBid > 0) {

                if (highestBidCount > 1) {
                    highestBidLabel.setText(
                            "TIED HIGHEST BID: ₹" +
                            String.format("%.2f", highestBid) +
                            " | " + highestBidCount + " TEAMS"
                    );
                    highestBidLabel.setForeground(new Color(255, 170, 90));

                } else {
                    highestBidLabel.setText(
                            "Highest Bid: ₹" +
                            String.format("%.2f", highestBid) +
                            " | " + highestTeamName
                    );
                    highestBidLabel.setForeground(new Color(90, 220, 140));
                }

            } else {
                highestBidLabel.setText("No bids in current round");
                highestBidLabel.setForeground(mutedBlue);
            }

            franchiseCardsPanel.revalidate();
            franchiseCardsPanel.repaint();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    bidsFrame,
                    "Unable to display bids.\n" + e.getMessage()
            );
        }
    }

    private void createFranchiseBidCard(
            Connection con,
            int teamId,
            String teamName) throws SQLException {

        String bidQuery =
                "SELECT bid_amount, bid_round, bid_time FROM bids " +
                "WHERE lot_id=? AND team_id=? " +
                "ORDER BY bid_round ASC, bid_id ASC";

        List<String> bidHistory = new ArrayList<>();

        double currentRoundBid = 0;
        int currentRoundBidCount = 0;

        try (PreparedStatement ps = con.prepareStatement(bidQuery)) {

            ps.setInt(1, selectedLotId);
            ps.setInt(2, teamId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    double amount = rs.getDouble("bid_amount");
                    int round = rs.getInt("bid_round");
                    Timestamp bidTime = rs.getTimestamp("bid_time");

                    bidHistory.add(
                            "Round " + round + " : ₹" +
                            String.format("%.2f", amount) +
                            (bidTime == null ? "" : "  (" + bidTime + ")")
                    );

                    if (round == selectedCurrentRound) {
                        currentRoundBid = amount;
                        currentRoundBidCount++;
                    }
                }
            }
        }

        if (currentRoundBid > highestBid) {
            highestBid = currentRoundBid;
            highestTeamId = teamId;
            highestTeamName = teamName;
            highestBidCount = 1;

        } else if (currentRoundBid > 0 && currentRoundBid == highestBid) {
            highestBidCount++;
        }

        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(panelBlue);
        card.setBorder(new CompoundBorder(
                new LineBorder(
                        currentRoundBid > 0
                                ? new Color(70, 100, 140)
                                : borderBlue,
                        1,
                        true
                ),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(panelBlue);

        JLabel teamLabel = new JLabel(teamName.toUpperCase());
        teamLabel.setFont(new Font("Arial", Font.BOLD, 16));
        teamLabel.setForeground(white);

        JLabel bidCountLabel = new JLabel(
                currentRoundBidCount + " BID(S) IN THIS ROUND"
        );
        bidCountLabel.setFont(new Font("Arial", Font.BOLD, 10));
        bidCountLabel.setForeground(lightBlue);

        topPanel.add(teamLabel, BorderLayout.WEST);
        topPanel.add(bidCountLabel, BorderLayout.EAST);

        JLabel amountLabel = new JLabel(
                currentRoundBid == 0
                        ? "No bid in current round"
                        : "₹" + String.format("%.2f", currentRoundBid)
        );

        amountLabel.setFont(new Font("Arial", Font.BOLD, 23));
        amountLabel.setForeground(
                currentRoundBid == 0 ? mutedBlue : white
        );

        JTextArea historyArea = new JTextArea();
        historyArea.setEditable(false);
        historyArea.setLineWrap(true);
        historyArea.setWrapStyleWord(true);
        historyArea.setBackground(new Color(17, 33, 57));
        historyArea.setForeground(mutedBlue);
        historyArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        historyArea.setBorder(new EmptyBorder(8, 8, 8, 8));

        if (bidHistory.isEmpty()) {
            historyArea.setText("No bidding history available.");
        } else {
            StringBuilder history = new StringBuilder();

            for (String entry : bidHistory) {
                history.append(entry).append("\n");
            }

            historyArea.setText(history.toString());
        }

        JScrollPane historyScroll = new JScrollPane(historyArea);
        historyScroll.setPreferredSize(new Dimension(250, 110));
        historyScroll.setBorder(new LineBorder(borderBlue));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(panelBlue);
        content.add(amountLabel);
        content.add(Box.createVerticalStrut(10));
        content.add(historyScroll);

        card.add(topPanel, BorderLayout.NORTH);
        card.add(content, BorderLayout.CENTER);

        franchiseCardsPanel.add(card);
    }

    // ---------------- START TIE-BREAKER ----------------

    private void startTieBreaker() {

        if (selectedLotId == -1) {
            JOptionPane.showMessageDialog(
                    bidsFrame,
                    "Please select a player first."
            );
            return;
        }

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try {

                String lotQuery =
                        "SELECT status, current_round FROM auction_lots " +
                        "WHERE lot_id=? FOR UPDATE";

                String status = "";
                int currentRound = 1;

                try (PreparedStatement ps = con.prepareStatement(lotQuery)) {

                    ps.setInt(1, selectedLotId);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (rs.next()) {
                            status = rs.getString("status");
                            currentRound = rs.getInt("current_round");
                        }
                    }
                }

                if (!"CLOSED".equalsIgnoreCase(status)) {

                    con.rollback();

                    JOptionPane.showMessageDialog(
                            bidsFrame,
                            "Close the current auction round before starting a tie-breaker."
                    );

                    return;
                }

                String tieQuery =
                        "SELECT COUNT(*) AS tied_teams, " +
                        "MAX(bid_amount) AS highest_amount " +
                        "FROM bids WHERE lot_id=? AND bid_round=? " +
                        "AND bid_amount=(" +
                        "SELECT MAX(b2.bid_amount) FROM bids b2 " +
                        "WHERE b2.lot_id=? AND b2.bid_round=?)";

                int tiedTeams = 0;
                double topAmount = 0;

                try (PreparedStatement ps = con.prepareStatement(tieQuery)) {

                    ps.setInt(1, selectedLotId);
                    ps.setInt(2, currentRound);
                    ps.setInt(3, selectedLotId);
                    ps.setInt(4, currentRound);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (rs.next()) {
                            tiedTeams = rs.getInt("tied_teams");
                            topAmount = rs.getDouble("highest_amount");
                        }
                    }
                }

                if (tiedTeams < 2) {

                    con.rollback();

                    JOptionPane.showMessageDialog(
                            bidsFrame,
                            "There is no tie in the highest bid.\n" +
                            "A tie-breaker is needed only when two or more teams have the same highest bid."
                    );

                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(
                        bidsFrame,
                        "A tie has been detected!\n\n" +
                        "Highest Bid: ₹" + String.format("%.2f", topAmount) +
                        "\nTied Teams: " + tiedTeams +
                        "\nCurrent Round: " + currentRound +
                        "\n\nStart Round " + (currentRound + 1) +
                        " for only the tied teams?",
                        "Start Tie-Breaker",
                        JOptionPane.YES_NO_OPTION
                );

                if (confirm != JOptionPane.YES_OPTION) {
                    con.rollback();
                    return;
                }

                String update =
                        "UPDATE auction_lots SET current_round=?, " +
                        "tie_breaker_active=TRUE, status='LIVE' " +
                        "WHERE lot_id=? AND status='CLOSED'";

                try (PreparedStatement ps = con.prepareStatement(update)) {

                    ps.setInt(1, currentRound + 1);
                    ps.setInt(2, selectedLotId);

                    if (ps.executeUpdate() == 0) {
                        throw new SQLException(
                                "Unable to start the tie-breaker. Refresh and try again."
                        );
                    }
                }

                con.commit();

                JOptionPane.showMessageDialog(
                        bidsFrame,
                        "Tie-breaker started successfully!\n\n" +
                        "Round: " + (currentRound + 1) +
                        "\nOnly the teams tied in Round " + currentRound +
                        " can submit bids."
                );

                loadAuctionPlayers();
                showSelectedPlayerBids();
                loadStatistics();

            } catch (Exception ex) {
                con.rollback();
                throw ex;

            } finally {
                con.setAutoCommit(true);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    bidsFrame,
                    "Unable to start tie-breaker.\n" + e.getMessage()
            );
        }
    }

    // ---------------- SQUAD VALIDATION ----------------

    private String checkSquadConstraints(
            Connection con,
            int teamId,
            int playerId) throws SQLException {

        int maxSquad;
        int maxOverseas;
        int maxBatsmen;
        int maxBowlers;
        int maxAllRounders;
        int maxWicketKeepers;

        String settingsQuery =
                "SELECT max_squad_size, max_overseas, max_batsmen, " +
                "max_bowlers, max_all_rounders, max_wicket_keepers " +
                "FROM users WHERE role='ADMIN' LIMIT 1";

        try (PreparedStatement ps = con.prepareStatement(settingsQuery);
             ResultSet rs = ps.executeQuery()) {

            if (!rs.next()) {
                return "Admin squad constraints were not found.";
            }

            maxSquad = rs.getInt("max_squad_size");
            maxOverseas = rs.getInt("max_overseas");
            maxBatsmen = rs.getInt("max_batsmen");
            maxBowlers = rs.getInt("max_bowlers");
            maxAllRounders = rs.getInt("max_all_rounders");
            maxWicketKeepers = rs.getInt("max_wicket_keepers");
        }

        String category;
        String nationality;

        String playerQuery =
                "SELECT category, nationality FROM players " +
                "WHERE player_id=? AND team_id IS NULL FOR UPDATE";

        try (PreparedStatement ps = con.prepareStatement(playerQuery)) {

            ps.setInt(1, playerId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return "Player is no longer available for assignment.";
                }

                category = rs.getString("category");
                nationality = rs.getString("nationality");
            }
        }

        int squadCount = 0;
        int overseasCount = 0;
        int batsmenCount = 0;
        int bowlersCount = 0;
        int allRoundersCount = 0;
        int wicketKeepersCount = 0;

        String countQuery =
                "SELECT COUNT(*) AS squad_count, " +
                "COALESCE(SUM(nationality='OVERSEAS'),0) AS overseas_count, " +
                "COALESCE(SUM(category='BATSMAN'),0) AS batsmen_count, " +
                "COALESCE(SUM(category='BOWLER'),0) AS bowlers_count, " +
                "COALESCE(SUM(category='ALL_ROUNDER'),0) AS allrounder_count, " +
                "COALESCE(SUM(category='WICKET_KEEPER'),0) AS keeper_count " +
                "FROM players WHERE team_id=?";

        try (PreparedStatement ps = con.prepareStatement(countQuery)) {

            ps.setInt(1, teamId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    squadCount = rs.getInt("squad_count");
                    overseasCount = rs.getInt("overseas_count");
                    batsmenCount = rs.getInt("batsmen_count");
                    bowlersCount = rs.getInt("bowlers_count");
                    allRoundersCount = rs.getInt("allrounder_count");
                    wicketKeepersCount = rs.getInt("keeper_count");
                }
            }
        }

        if (squadCount + 1 > maxSquad) {
            return "Maximum squad size reached. Maximum allowed: " + maxSquad;
        }

        if ("OVERSEAS".equalsIgnoreCase(nationality)
                && overseasCount + 1 > maxOverseas) {
            return "Maximum overseas player limit reached. Maximum allowed: "
                    + maxOverseas;
        }

        if ("BATSMAN".equalsIgnoreCase(category)
                && batsmenCount + 1 > maxBatsmen) {
            return "Maximum batsmen limit reached. Maximum allowed: "
                    + maxBatsmen;
        }

        if ("BOWLER".equalsIgnoreCase(category)
                && bowlersCount + 1 > maxBowlers) {
            return "Maximum bowlers limit reached. Maximum allowed: "
                    + maxBowlers;
        }

        if ("ALL_ROUNDER".equalsIgnoreCase(category)
                && allRoundersCount + 1 > maxAllRounders) {
            return "Maximum all-rounders limit reached. Maximum allowed: "
                    + maxAllRounders;
        }

        if ("WICKET_KEEPER".equalsIgnoreCase(category)
                && wicketKeepersCount + 1 > maxWicketKeepers) {
            return "Maximum wicket-keepers limit reached. Maximum allowed: "
                    + maxWicketKeepers;
        }

        return null;
    }

    // ---------------- ASSIGN PLAYER ----------------

    private void assignHighestBidder() {

        if (selectedLotId == -1 || selectedPlayerId == -1) {
            JOptionPane.showMessageDialog(
                    bidsFrame,
                    "Please select a player first."
            );
            return;
        }

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try {

                String statusQuery =
                        "SELECT status, current_round FROM auction_lots " +
                        "WHERE lot_id=? FOR UPDATE";

                String status = "";
                int currentRound = 1;

                try (PreparedStatement ps = con.prepareStatement(statusQuery)) {

                    ps.setInt(1, selectedLotId);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (rs.next()) {
                            status = rs.getString("status");
                            currentRound = rs.getInt("current_round");
                        }
                    }
                }

                if (!"CLOSED".equalsIgnoreCase(status)) {

                    con.rollback();

                    JOptionPane.showMessageDialog(
                            bidsFrame,
                            "Only CLOSED auction players can be assigned."
                    );

                    return;
                }

                String highestQuery =
                        "SELECT b.team_id, t.team_name, b.bid_amount " +
                        "FROM bids b " +
                        "JOIN teams t ON b.team_id=t.team_id " +
                        "WHERE b.lot_id=? AND b.bid_round=? " +
                        "AND b.bid_amount=(" +
                        "SELECT MAX(b2.bid_amount) FROM bids b2 " +
                        "WHERE b2.lot_id=? AND b2.bid_round=?) " +
                        "ORDER BY b.team_id";

                int winningTeamId = -1;
                String winningTeamName = "";
                double winningAmount = 0;
                int tiedTeams = 0;

                try (PreparedStatement ps = con.prepareStatement(highestQuery)) {

                    ps.setInt(1, selectedLotId);
                    ps.setInt(2, currentRound);
                    ps.setInt(3, selectedLotId);
                    ps.setInt(4, currentRound);

                    try (ResultSet rs = ps.executeQuery()) {

                        while (rs.next()) {

                            tiedTeams++;

                            if (tiedTeams == 1) {
                                winningTeamId = rs.getInt("team_id");
                                winningTeamName = rs.getString("team_name");
                                winningAmount = rs.getDouble("bid_amount");
                            }
                        }
                    }
                }

                if (winningTeamId == -1) {

                    con.rollback();

                    JOptionPane.showMessageDialog(
                            bidsFrame,
                            "No bids are available in the current round."
                    );

                    return;
                }

                if (tiedTeams > 1) {

                    con.rollback();

                    JOptionPane.showMessageDialog(
                            bidsFrame,
                            "PLAYER CANNOT BE ASSIGNED!\n\n" +
                            "The highest bid is still tied between " +
                            tiedTeams + " teams.\n" +
                            "Start another tie-breaker round."
                    );

                    return;
                }

                String purseQuery =
                        "SELECT remaining_purse FROM teams " +
                        "WHERE team_id=? FOR UPDATE";

                double remainingPurse = 0;

                try (PreparedStatement ps = con.prepareStatement(purseQuery)) {

                    ps.setInt(1, winningTeamId);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (rs.next()) {
                            remainingPurse = rs.getDouble("remaining_purse");

                        } else {

                            con.rollback();

                            JOptionPane.showMessageDialog(
                                    bidsFrame,
                                    "Winning team was not found."
                            );

                            return;
                        }
                    }
                }

                String constraintError = checkSquadConstraints(
                        con,
                        winningTeamId,
                        selectedPlayerId
                );

                if (constraintError != null) {

                    con.rollback();

                    JOptionPane.showMessageDialog(
                            bidsFrame,
                            "Cannot assign this player to " + winningTeamName
                                    + ".\n\n" + constraintError
                    );

                    return;
                }

                if (winningAmount > remainingPurse) {

                    con.rollback();

                    JOptionPane.showMessageDialog(
                            bidsFrame,
                            winningTeamName +
                            " does not have sufficient remaining purse."
                    );

                    return;
                }

                String updateTeam =
                        "UPDATE teams SET remaining_purse=remaining_purse-? " +
                        "WHERE team_id=?";

                try (PreparedStatement ps = con.prepareStatement(updateTeam)) {

                    ps.setDouble(1, winningAmount);
                    ps.setInt(2, winningTeamId);
                    ps.executeUpdate();
                }

                String updatePlayer =
                        "UPDATE players SET team_id=?, " +
                        "purchase_type='AUCTION', purchase_price=? " +
                        "WHERE player_id=? AND team_id IS NULL";

                try (PreparedStatement ps = con.prepareStatement(updatePlayer)) {

                    ps.setInt(1, winningTeamId);
                    ps.setDouble(2, winningAmount);
                    ps.setInt(3, selectedPlayerId);

                    if (ps.executeUpdate() == 0) {
                        throw new SQLException(
                                "Player is no longer available for assignment."
                        );
                    }
                }

                String updateLot =
                        "UPDATE auction_lots SET status='SOLD', " +
                        "sold_team_id=?, sold_price=?, tie_breaker_active=FALSE " +
                        "WHERE lot_id=? AND status='CLOSED'";

                try (PreparedStatement ps = con.prepareStatement(updateLot)) {

                    ps.setInt(1, winningTeamId);
                    ps.setDouble(2, winningAmount);
                    ps.setInt(3, selectedLotId);

                    if (ps.executeUpdate() == 0) {
                        throw new SQLException(
                                "Auction lot status changed. Please refresh."
                        );
                    }
                }

                con.commit();

                JOptionPane.showMessageDialog(
                        bidsFrame,
                        "PLAYER ASSIGNED SUCCESSFULLY!\n\n" +
                        "Player: " + selectedPlayerName +
                        "\nTeam: " + winningTeamName +
                        "\nWinning Bid: ₹" +
                        String.format("%.2f", winningAmount) +
                        "\nWinning Round: " + currentRound
                );

                loadAuctionPlayers();
                showSelectedPlayerBids();
                loadStatistics();

            } catch (Exception ex) {

                con.rollback();
                throw ex;

            } finally {
                con.setAutoCommit(true);
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    bidsFrame,
                    "Unable to assign player.\n" + e.getMessage()
            );
        }
    }

    // ---------------- MARK PLAYER UNSOLD ----------------

    private void markPlayerUnsold() {

        if (selectedLotId == -1) {
            JOptionPane.showMessageDialog(
                    bidsFrame,
                    "Please select a player first."
            );
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                bidsFrame,
                "Are you sure you want to mark this player as UNSOLD?",
                "Confirm Unsold",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try (Connection con = DBConnection.getConnection()) {

            String status = "";

            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT status FROM auction_lots WHERE lot_id=?")) {

                ps.setInt(1, selectedLotId);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        status = rs.getString("status");
                    }
                }
            }

            if (!"CLOSED".equalsIgnoreCase(status)) {

                JOptionPane.showMessageDialog(
                        bidsFrame,
                        "Only CLOSED players can be marked UNSOLD."
                );

                return;
            }

            int bidCount = 0;

            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT COUNT(*) FROM bids WHERE lot_id=?")) {

                ps.setInt(1, selectedLotId);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        bidCount = rs.getInt(1);
                    }
                }
            }

            if (bidCount > 0) {

                JOptionPane.showMessageDialog(
                        bidsFrame,
                        "This player has bids. Assign the player to " +
                        "the highest bidder instead."
                );

                return;
            }

            String update =
                    "UPDATE auction_lots SET status='UNSOLD', " +
                    "sold_team_id=NULL, sold_price=NULL, " +
                    "tie_breaker_active=FALSE WHERE lot_id=?";

            try (PreparedStatement ps = con.prepareStatement(update)) {

                ps.setInt(1, selectedLotId);
                ps.executeUpdate();
            }

            JOptionPane.showMessageDialog(
                    bidsFrame,
                    selectedPlayerName + " marked as UNSOLD."
            );

            loadAuctionPlayers();
            showSelectedPlayerBids();
            loadStatistics();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    bidsFrame,
                    "Unable to mark player UNSOLD.\n" + e.getMessage()
            );
        }
    }

    // ---------------- AUCTION SUMMARY ----------------

    private void showAuctionSummary() {

        JFrame frame = createChildFrame("Auction Summary");

        frame.add(createScreenTitle("AUCTION SUMMARY"), BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(
                new String[]{
                        "PLAYER", "TEAM", "PURCHASE TYPE",
                        "PURCHASE PRICE", "STATUS"
                }, 0
        ) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        styleTable(table);

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(panelBlue);

        String query =
                "SELECT p.player_name, t.team_name, " +
                "p.purchase_type, p.purchase_price, al.status " +
                "FROM auction_lots al " +
                "JOIN players p ON al.player_id=p.player_id " +
                "LEFT JOIN teams t ON al.sold_team_id=t.team_id " +
                "ORDER BY al.lot_id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                model.addRow(new Object[]{
                        rs.getString("player_name"),
                        rs.getString("team_name") == null
                                ? "-" : rs.getString("team_name"),
                        rs.getString("purchase_type") == null
                                ? "-" : rs.getString("purchase_type"),
                        rs.getObject("purchase_price") == null
                                ? "-" : rs.getDouble("purchase_price"),
                        rs.getString("status")
                });
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    frame,
                    "Unable to load auction summary.\n" + e.getMessage()
            );
        }

        JButton close = createButton("CLOSE");
        close.addActionListener(e -> frame.dispose());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setBackground(panelBlue);
        bottom.add(close);

        frame.add(scroll, BorderLayout.CENTER);
        frame.add(bottom, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    // ---------------- PASSWORD REQUESTS ----------------

    private void showPasswordRequests() {

        JFrame frame = createChildFrame("Owner Password Requests");

        frame.add(
                createScreenTitle("OWNER PASSWORD REQUESTS"),
                BorderLayout.NORTH
        );

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"USER ID", "USERNAME", "FULL NAME"}, 0
        ) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        styleTable(table);

        JScrollPane scroll = new JScrollPane(table);

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(darkBlue);
        center.setBorder(new EmptyBorder(15, 20, 15, 20));
        center.add(scroll, BorderLayout.CENTER);

        JPanel bottom = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 10, 10)
        );
        bottom.setBackground(panelBlue);

        JButton resetButton = createButton("RESET PASSWORD");
        JButton refreshButton = createButton("REFRESH");
        JButton closeButton = createButton("CLOSE");

        resetButton.addActionListener(e -> {

            int row = table.getSelectedRow();

            if (row == -1) {
                JOptionPane.showMessageDialog(
                        frame,
                        "Please select an owner request."
                );
                return;
            }

            int userId = Integer.parseInt(
                    model.getValueAt(row, 0).toString()
            );

            String username = model.getValueAt(row, 1).toString();

            resetOwnerPassword(frame, userId, username);
            loadPasswordRequests(model);
        });

        refreshButton.addActionListener(e -> loadPasswordRequests(model));
        closeButton.addActionListener(e -> frame.dispose());

        bottom.add(resetButton);
        bottom.add(refreshButton);
        bottom.add(closeButton);

        frame.add(center, BorderLayout.CENTER);
        frame.add(bottom, BorderLayout.SOUTH);

        loadPasswordRequests(model);
        frame.setVisible(true);
    }

    private void styleTable(JTable table) {

        table.setRowHeight(35);
        table.setBackground(panelBlue);
        table.setForeground(white);
        table.setGridColor(borderBlue);
        table.setSelectionBackground(buttonBlue);
        table.setSelectionForeground(white);

        table.getTableHeader().setBackground(darkBlue);
        table.getTableHeader().setForeground(white);
        table.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 12)
        );
    }

    // ---------------- LOAD PASSWORD REQUESTS ----------------

    private void loadPasswordRequests(DefaultTableModel model) {

        model.setRowCount(0);

        String query =
                "SELECT user_id, username, full_name FROM users " +
                "WHERE role='OWNER' AND password_reset_requested=1 " +
                "ORDER BY reset_request_time";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                model.addRow(new Object[]{
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("full_name")
                });
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load password requests.\n" + e.getMessage()
            );
        }
    }

    // ---------------- RESET OWNER PASSWORD ----------------

    private void resetOwnerPassword(
            JFrame frame,
            int userId,
            String username) {

        JPasswordField passwordField = new JPasswordField(18);

        String defaultPassword = "owner@123";

        if (username.equalsIgnoreCase("rcb_owner")) {
            defaultPassword = "rcb@123";
        } else if (username.equalsIgnoreCase("csk_owner")) {
            defaultPassword = "csk@123";
        } else if (username.equalsIgnoreCase("mi_owner")) {
            defaultPassword = "mi@123";
        } else if (username.equalsIgnoreCase("srh_owner")) {
            defaultPassword = "srh@123";
        } else if (username.equalsIgnoreCase("kkr_owner")) {
            defaultPassword = "kkr@123";
        }

        passwordField.setText(defaultPassword);

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.add(new JLabel("Set new password for " + username));
        panel.add(passwordField);

        int result = JOptionPane.showConfirmDialog(
                frame,
                panel,
                "Reset Owner Password",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String newPassword =
                new String(passwordField.getPassword()).trim();

        if (newPassword.isEmpty()) {

            JOptionPane.showMessageDialog(
                    frame,
                    "Password cannot be empty."
            );

            return;
        }

        if (newPassword.length() < 6) {

            JOptionPane.showMessageDialog(
                    frame,
                    "Password must contain at least 6 characters."
            );

            return;
        }

        String query =
                "UPDATE users SET password=?, " +
                "password_reset_requested=0, reset_request_time=NULL " +
                "WHERE user_id=? AND role='OWNER'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, newPassword);
            ps.setInt(2, userId);

            int updated = ps.executeUpdate();

            if (updated > 0) {

                JOptionPane.showMessageDialog(
                        frame,
                        "Password reset successfully for " + username
                );

            } else {

                JOptionPane.showMessageDialog(
                        frame,
                        "Password request could not be updated."
                );
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    frame,
                    "Unable to reset password.\n" + e.getMessage()
            );
        }
    }
}
