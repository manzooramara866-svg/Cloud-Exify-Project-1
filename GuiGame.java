package com.cloudexify.guessgame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class GuiGame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainContainer;

    // Core game state
    private Game activeGame;
    private boolean limitAttempts = false;
    private int selectedDifficulty = 1;
    private String playerName = "Player"; // collected before game starts

    // Theme Colors
    private static final Color COLOR_BG           = new Color(18, 18, 20);
    private static final Color COLOR_CARD         = new Color(30, 31, 38);
    private static final Color COLOR_TEXT_PRIMARY  = new Color(245, 245, 245);
    private static final Color COLOR_TEXT_MUTED   = new Color(150, 150, 160);
    private static final Color COLOR_ACCENT_PURPLE = new Color(114, 9, 183);
    private static final Color COLOR_ACCENT_CORAL  = new Color(247, 37, 133);
    private static final Color COLOR_ACCENT_TEAL   = new Color(0, 245, 212);
    private static final Color COLOR_ACCENT_CYAN   = new Color(72, 202, 228);

    // GUI Components (game screen)
    private JLabel statusReadout;
    private JTextField guessInputField;      // invisible, for keyboard capture
    private JLabel guessDisplay;             // visible top-aligned number display
    private StringBuilder guessBuffer = new StringBuilder();
    private DefaultListModel<String> historyListModel;
    private JList<String> historyList;
    private ProximityBar proximityBar;
    private CustomProgressBar attemptsProgressBar;
    private JLabel attemptsLabel;
    private JLabel bestScoreLabel;
    private JPanel numPadPanel;
    private JLabel playerNameLabel;

    // Leaderboard elements
    private JTable leaderboardTable;
    private DefaultTableModel tableModel;

    public GuiGame() {
        setTitle("CloudExify Guessing Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(920, 640);
        setMinimumSize(new Dimension(820, 580));
        setLocationRelativeTo(null);
        setResizable(true);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);
        mainContainer.setBackground(COLOR_BG);

        mainContainer.add(createMenuPanel(),        "MENU");
        mainContainer.add(createGamePanel(),        "GAME");
        mainContainer.add(createLeaderboardPanel(), "LEADERBOARD");

        add(mainContainer);
        cardLayout.show(mainContainer, "MENU");
        updateMenuBestScore();
    }

    private void updateMenuBestScore() {
        int best = Leaderboard.loadBestScore();
        bestScoreLabel.setText(best == Integer.MAX_VALUE
            ? "🏆 Best Score: --"
            : "🏆 Best Score: " + best + " attempts");
    }

    // ══════════════════════════════════════════════════
    // SCREEN 1 — MAIN MENU
    // ══════════════════════════════════════════════════
    private JPanel createMenuPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        // Brand
        JLabel brandLabel = new JLabel("C L O U D E X I F Y", JLabel.CENTER);
        brandLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        brandLabel.setForeground(COLOR_ACCENT_TEAL);
        gbc.gridy = 0;
        panel.add(brandLabel, gbc);

        JLabel titleLabel = new JLabel("GUESSING GAME", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 40));
        titleLabel.setForeground(COLOR_TEXT_PRIMARY);
        gbc.gridy = 1;
        panel.add(titleLabel, gbc);

        bestScoreLabel = new JLabel("🏆 Best Score: Loading...", JLabel.CENTER);
        bestScoreLabel.setFont(new Font("Segoe UI", Font.ITALIC, 15));
        bestScoreLabel.setForeground(COLOR_TEXT_MUTED);
        gbc.gridy = 2;
        panel.add(bestScoreLabel, gbc);

        // ── Settings card ──
        RoundedPanel card = new RoundedPanel(20, COLOR_CARD);
        card.setLayout(new GridBagLayout());
        card.setBorder(new EmptyBorder(20, 30, 20, 30));
        card.setPreferredSize(new Dimension(420, 260));

        GridBagConstraints cg = new GridBagConstraints();
        cg.insets = new Insets(6, 8, 6, 8);
        cg.fill = GridBagConstraints.HORIZONTAL;
        cg.gridx = 0; cg.gridy = 0; cg.gridwidth = 2;

        // Name input row
        JLabel nameHint = new JLabel("YOUR NAME:");
        nameHint.setFont(new Font("Segoe UI", Font.BOLD, 12));
        nameHint.setForeground(COLOR_TEXT_MUTED);
        cg.gridwidth = 1;
        card.add(nameHint, cg);

        JTextField nameField = new JTextField("Player");
        nameField.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameField.setBackground(new Color(20, 20, 28));
        nameField.setForeground(COLOR_TEXT_PRIMARY);
        nameField.setCaretColor(COLOR_ACCENT_TEAL);
        nameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 80), 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        cg.gridx = 1;
        card.add(nameField, cg);

        // Difficulty
        cg.gridx = 0; cg.gridy = 1; cg.gridwidth = 2;
        JLabel diffLabel = new JLabel("DIFFICULTY:", JLabel.LEFT);
        diffLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        diffLabel.setForeground(COLOR_TEXT_MUTED);
        card.add(diffLabel, cg);

        cg.gridy = 2;
        ButtonGroup diffGroup = new ButtonGroup();
        JPanel diffRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        diffRow.setOpaque(false);
        JRadioButton easyBtn = new JRadioButton("⚡ Easy  (1 – 50)",  true);
        JRadioButton hardBtn = new JRadioButton("🔥 Hard  (1 – 200)");
        styleRadioButton(easyBtn); styleRadioButton(hardBtn);
        diffGroup.add(easyBtn); diffGroup.add(hardBtn);
        easyBtn.addActionListener(e -> selectedDifficulty = 1);
        hardBtn.addActionListener(e -> selectedDifficulty = 2);
        diffRow.add(easyBtn); diffRow.add(hardBtn);
        card.add(diffRow, cg);

        // Options row
        cg.gridy = 3;
        JPanel optRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        optRow.setOpaque(false);
        JCheckBox limitCB = new JCheckBox("⏳ Limit Attempts");
        JCheckBox soundCB  = new JCheckBox("🔊 Sound Effects", true);
        styleCheckBox(limitCB); styleCheckBox(soundCB);
        limitCB.addActionListener(e -> limitAttempts = limitCB.isSelected());
        soundCB.addActionListener(e -> SoundManager.setSoundEnabled(soundCB.isSelected()));
        optRow.add(limitCB); optRow.add(soundCB);
        card.add(optRow, cg);

        // Buttons
        cg.gridy = 4;
        JPanel btnRow = new JPanel(new GridLayout(1, 2, 15, 0));
        btnRow.setOpaque(false);
        ModernButton playBtn = new ModernButton("▶  PLAY GAME");
        playBtn.setGradient(COLOR_ACCENT_PURPLE, COLOR_ACCENT_CORAL);
        playBtn.setPreferredSize(new Dimension(0, 46));
        playBtn.addActionListener(e -> {
            SoundManager.playClick();
            String typed = nameField.getText().trim();
            playerName = typed.isEmpty() ? "Player" : typed;
            startNewGame();
        });

        ModernButton lbBtn = new ModernButton("🏆  LEADERBOARD");
        lbBtn.setGradient(new Color(55, 57, 70), new Color(38, 40, 52));
        lbBtn.setPreferredSize(new Dimension(0, 46));
        lbBtn.addActionListener(e -> { SoundManager.playClick(); showLeaderboardScreen(); });

        btnRow.add(playBtn); btnRow.add(lbBtn);
        card.add(btnRow, cg);

        gbc.gridy = 3;
        panel.add(card, gbc);

        return panel;
    }

    // ══════════════════════════════════════════════════
    // SCREEN 2 — GAME DASHBOARD
    // ══════════════════════════════════════════════════
    private JPanel createGamePanel() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBackground(COLOR_BG);
        root.setBorder(new EmptyBorder(14, 16, 14, 16));

        // ── Top bar ──
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);

        JButton backBtn = new JButton("◀ MENU");
        backBtn.setFocusPainted(false); backBtn.setBorderPainted(false);
        backBtn.setContentAreaFilled(false);
        backBtn.setForeground(COLOR_ACCENT_TEAL);
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> { SoundManager.playClick(); cardLayout.show(mainContainer, "MENU"); updateMenuBestScore(); });
        topBar.add(backBtn, BorderLayout.WEST);

        JLabel titleLbl = new JLabel("CLOUDEXIFY GUESSING GAME", JLabel.CENTER);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titleLbl.setForeground(COLOR_TEXT_PRIMARY);
        topBar.add(titleLbl, BorderLayout.CENTER);

        playerNameLabel = new JLabel("", JLabel.RIGHT);
        playerNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        playerNameLabel.setForeground(COLOR_ACCENT_TEAL);
        topBar.add(playerNameLabel, BorderLayout.EAST);

        root.add(topBar, BorderLayout.NORTH);

        // ── Center: stats + display + submit ──
        RoundedPanel centerCard = new RoundedPanel(20, COLOR_CARD);
        centerCard.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(5, 18, 5, 18);

        // Attempts label — top padding ensures it's never clipped
        attemptsLabel = new JLabel("Attempt: 0", JLabel.CENTER);
        attemptsLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        attemptsLabel.setForeground(COLOR_TEXT_PRIMARY);
        attemptsLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        g.gridy = 0;
        centerCard.add(attemptsLabel, g);

        // Attempts bar
        attemptsProgressBar = new CustomProgressBar();
        attemptsProgressBar.setPreferredSize(new Dimension(280, 7));
        g.gridy = 1; g.insets = new Insets(2, 18, 4, 18);
        centerCard.add(attemptsProgressBar, g);

        // Status readout
        statusReadout = new JLabel("GUESS THE NUMBER!", JLabel.CENTER);
        statusReadout.setFont(new Font("Segoe UI", Font.BOLD, 20));
        statusReadout.setForeground(COLOR_ACCENT_TEAL);
        statusReadout.setPreferredSize(new Dimension(360, 38));
        g.gridy = 2; g.insets = new Insets(4, 18, 2, 18);
        centerCard.add(statusReadout, g);

        // Proximity label + bar
        JLabel proxLbl = new JLabel("PROXIMITY RADAR", JLabel.CENTER);
        proxLbl.setFont(new Font("Segoe UI", Font.BOLD, 9));
        proxLbl.setForeground(COLOR_TEXT_MUTED);
        g.gridy = 3; g.insets = new Insets(2, 18, 1, 18);
        centerCard.add(proxLbl, g);

        proximityBar = new ProximityBar();
        proximityBar.setPreferredSize(new Dimension(280, 11));
        g.gridy = 4; g.insets = new Insets(1, 18, 6, 18);
        centerCard.add(proximityBar, g);

        // Calculator display box
        JPanel displayPanel = new JPanel(new BorderLayout()) {
            protected void paintComponent(Graphics g2d) {
                Graphics2D g2 = (Graphics2D) g2d.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(12, 12, 18));
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
                g2.setColor(new Color(0, 245, 212, 55));
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 14, 14);
                g2.dispose();
                super.paintComponent(g2d);
            }
        };
        displayPanel.setOpaque(false);
        displayPanel.setPreferredSize(new Dimension(300, 72));
        displayPanel.setBorder(new EmptyBorder(7, 14, 7, 14));

        JLabel dispHint = new JLabel("YOUR GUESS");
        dispHint.setFont(new Font("Segoe UI", Font.BOLD, 8));
        dispHint.setForeground(new Color(0, 245, 212, 130));
        displayPanel.add(dispHint, BorderLayout.NORTH);

        guessDisplay = new JLabel("_");
        guessDisplay.setFont(new Font("Segoe UI", Font.BOLD, 36));
        guessDisplay.setForeground(COLOR_TEXT_PRIMARY);
        guessDisplay.setVerticalAlignment(JLabel.TOP);
        guessDisplay.setHorizontalAlignment(JLabel.LEFT);
        displayPanel.add(guessDisplay, BorderLayout.CENTER);

        // Hidden keyboard capture field
        guessInputField = new JTextField();
        guessInputField.setOpaque(false);
        guessInputField.setBorder(BorderFactory.createEmptyBorder());
        guessInputField.setForeground(new Color(0, 0, 0, 0));
        guessInputField.setCaretColor(new Color(0, 0, 0, 0));
        guessInputField.setPreferredSize(new Dimension(0, 0));
        guessInputField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (Character.isDigit(c)) {
                    guessBuffer.append(c);
                    guessDisplay.setText(guessBuffer.toString());
                } else if (c == KeyEvent.VK_BACK_SPACE && guessBuffer.length() > 0) {
                    guessBuffer.deleteCharAt(guessBuffer.length() - 1);
                    guessDisplay.setText(guessBuffer.length() == 0 ? "_" : guessBuffer.toString());
                } else if (c == KeyEvent.VK_ENTER) {
                    submitGuess();
                }
                e.consume();
            }
        });
        displayPanel.add(guessInputField, BorderLayout.SOUTH);
        displayPanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { guessInputField.requestFocusInWindow(); }
        });

        g.gridy = 5; g.fill = GridBagConstraints.NONE; g.insets = new Insets(4, 18, 4, 18);
        centerCard.add(displayPanel, g);

        // GUESS button
        ModernButton submitBtn = new ModernButton("GUESS  ↵");
        submitBtn.setPreferredSize(new Dimension(200, 42));
        submitBtn.setGradient(COLOR_ACCENT_PURPLE, COLOR_ACCENT_CORAL);
        submitBtn.addActionListener(e -> submitGuess());
        g.gridy = 6; g.insets = new Insets(4, 18, 10, 18);
        centerCard.add(submitBtn, g);

        root.add(centerCard, BorderLayout.CENTER);

        // ── East: numpad + history, stacked ──
        JPanel eastPanel = new JPanel(new BorderLayout(0, 10));
        eastPanel.setOpaque(false);
        eastPanel.setPreferredSize(new Dimension(260, 0));

        // Numpad — 4 rows × 3 cols, fixed button size so they always fully show
        numPadPanel = new JPanel(new GridLayout(4, 3, 8, 8));
        numPadPanel.setOpaque(false);
        for (int i = 1; i <= 9; i++) numPadPanel.add(createNumPadButton(String.valueOf(i)));
        numPadPanel.add(createNumPadButton("⌫"));
        numPadPanel.add(createNumPadButton("0"));
        numPadPanel.add(createNumPadButton("✔"));

        RoundedPanel numPadCard = new RoundedPanel(16, COLOR_CARD);
        numPadCard.setLayout(new BorderLayout());
        numPadCard.setBorder(new EmptyBorder(10, 10, 10, 10));
        numPadCard.setPreferredSize(new Dimension(260, 230));
        numPadCard.add(numPadPanel, BorderLayout.CENTER);
        eastPanel.add(numPadCard, BorderLayout.NORTH);

        // History log
        RoundedPanel histCard = new RoundedPanel(16, COLOR_CARD);
        histCard.setLayout(new BorderLayout());
        histCard.setBorder(new EmptyBorder(10, 12, 10, 12));

        JLabel histTitle = new JLabel("GUESS LOG", JLabel.CENTER);
        histTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        histTitle.setForeground(COLOR_TEXT_PRIMARY);
        histTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        histCard.add(histTitle, BorderLayout.NORTH);

        historyListModel = new DefaultListModel<>();
        historyList = new JList<>(historyListModel);
        historyList.setBackground(new Color(20, 20, 25));
        historyList.setForeground(COLOR_TEXT_PRIMARY);
        historyList.setFont(new Font("Monospaced", Font.BOLD, 11));
        historyList.setCellRenderer(new GuessLogRenderer());
        JScrollPane scroll = new JScrollPane(historyList);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(40, 40, 50)));
        histCard.add(scroll, BorderLayout.CENTER);

        eastPanel.add(histCard, BorderLayout.CENTER);
        root.add(eastPanel, BorderLayout.EAST);

        return root;
    }

    private JButton createNumPadButton(String text) {
        NumPadButton btn = new NumPadButton(text, COLOR_ACCENT_CORAL, COLOR_ACCENT_TEAL);
        btn.addActionListener(e -> {
            SoundManager.playClick();
            if (text.equals("⌫")) {
                if (guessBuffer.length() > 0) {
                    guessBuffer.deleteCharAt(guessBuffer.length() - 1);
                    guessDisplay.setText(guessBuffer.length() == 0 ? "_" : guessBuffer.toString());
                }
            } else if (text.equals("✔")) {
                submitGuess();
            } else {
                guessBuffer.append(text);
                guessDisplay.setText(guessBuffer.toString());
            }
            guessInputField.requestFocusInWindow();
        });
        return btn;
    }

    // ══════════════════════════════════════════════════
    // SCREEN 3 — LEADERBOARD
    // ══════════════════════════════════════════════════
    private JPanel createLeaderboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(22, 22, 22, 22));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JButton backBtn = new JButton("◀ BACK TO MENU");
        backBtn.setFocusPainted(false); backBtn.setBorderPainted(false); backBtn.setContentAreaFilled(false);
        backBtn.setForeground(COLOR_ACCENT_TEAL);
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> { SoundManager.playClick(); cardLayout.show(mainContainer, "MENU"); updateMenuBestScore(); });
        header.add(backBtn, BorderLayout.WEST);

        JLabel title = new JLabel("🏆  LEADERBOARD — TOP 5", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 21));
        title.setForeground(COLOR_TEXT_PRIMARY);
        header.add(title, BorderLayout.CENTER);
        header.add(new JLabel("             "), BorderLayout.EAST);
        panel.add(header, BorderLayout.NORTH);

        RoundedPanel tableCard = new RoundedPanel(20, COLOR_CARD);
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(new EmptyBorder(14, 14, 14, 14));

        String[] cols = {"Rank", "Name", "Attempts", "Difficulty", "Date"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        leaderboardTable = new JTable(tableModel);
        leaderboardTable.setBackground(new Color(25, 26, 32));
        leaderboardTable.setForeground(COLOR_TEXT_PRIMARY);
        leaderboardTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        leaderboardTable.setRowHeight(42);
        leaderboardTable.setShowGrid(false);
        leaderboardTable.setSelectionBackground(COLOR_ACCENT_PURPLE);
        leaderboardTable.setSelectionForeground(Color.WHITE);

        JTableHeader th = leaderboardTable.getTableHeader();
        th.setBackground(COLOR_CARD);
        th.setForeground(COLOR_ACCENT_TEAL);
        th.setFont(new Font("Segoe UI", Font.BOLD, 13));
        th.setPreferredSize(new Dimension(100, 36));

        DefaultTableCellRenderer cr = new DefaultTableCellRenderer();
        cr.setHorizontalAlignment(JLabel.CENTER);
        leaderboardTable.setDefaultRenderer(Object.class, cr);

        JScrollPane ts = new JScrollPane(leaderboardTable);
        ts.getViewport().setBackground(COLOR_CARD);
        ts.setBorder(BorderFactory.createEmptyBorder());
        tableCard.add(ts, BorderLayout.CENTER);
        panel.add(tableCard, BorderLayout.CENTER);

        return panel;
    }

    private void showLeaderboardScreen() {
        tableModel.setRowCount(0);
        List<Leaderboard.ScoreEntry> scores = Leaderboard.loadLeaderboard();
        String[] medals = {"🥇 1st", "🥈 2nd", "🥉 3rd", "4th", "5th"};
        int rank = 0;
        for (Leaderboard.ScoreEntry e : scores) {
            tableModel.addRow(new Object[]{
                medals[Math.min(rank, medals.length - 1)],
                e.getName(), e.getAttempts() + " attempts", e.getDifficulty(), e.getDate()
            });
            rank++;
        }
        for (int i = scores.size(); i < 5; i++) {
            tableModel.addRow(new Object[]{"--", "—", "—", "—", "—"});
        }
        cardLayout.show(mainContainer, "LEADERBOARD");
    }

    // ══════════════════════════════════════════════════
    // GAME CONTROLLER
    // ══════════════════════════════════════════════════
    private void startNewGame() {
        activeGame = new Game(selectedDifficulty, limitAttempts);
        historyListModel.clear();
        guessBuffer.setLength(0);
        guessDisplay.setText("_");
        guessInputField.setEnabled(true);
        numPadPanel.setVisible(true);

        playerNameLabel.setText("👤 " + playerName + "   ");

        attemptsLabel.setText("Attempt: 0" + (limitAttempts ? " / " + activeGame.getMaxAttempts() : ""));
        attemptsProgressBar.setMaximumValue(limitAttempts ? activeGame.getMaxAttempts() : 20);
        attemptsProgressBar.setValue(0);

        statusReadout.setText("GUESS (1–" + activeGame.getRange() + ")");
        statusReadout.setForeground(COLOR_ACCENT_TEAL);
        proximityBar.reset();

        cardLayout.show(mainContainer, "GAME");
        guessInputField.requestFocusInWindow();
    }

    private void submitGuess() {
        if (activeGame == null || activeGame.isGameOver()) return;

        String raw = guessBuffer.toString().trim();
        if (raw.isEmpty()) return;

        int guess;
        try {
            guess = Integer.parseInt(raw);
        } catch (NumberFormatException ex) {
            statusReadout.setText("ENTER A VALID NUMBER!");
            statusReadout.setForeground(COLOR_ACCENT_CORAL);
            guessBuffer.setLength(0); guessDisplay.setText("_");
            return;
        }

        if (guess < 1 || guess > activeGame.getRange()) {
            statusReadout.setText("OUT OF BOUNDS  (1–" + activeGame.getRange() + ")");
            statusReadout.setForeground(COLOR_ACCENT_CORAL);
            guessBuffer.setLength(0); guessDisplay.setText("_");
            return;
        }

        guessBuffer.setLength(0); guessDisplay.setText("_");

        int dist  = Math.abs(activeGame.getSecretNumber() - guess);
        double ratio = 1.0 - ((double) dist / activeGame.getRange());
        proximityBar.setProximity(ratio);

        String warmCold = activeGame.getProximityHint(guess);
        int result   = activeGame.checkGuess(guess);
        int attempts = activeGame.getAttempts();

        attemptsLabel.setText("Attempt: " + attempts + (limitAttempts ? " / " + activeGame.getMaxAttempts() : ""));
        attemptsProgressBar.setValue(attempts);

        if (result == -1) {
            statusReadout.setText("TOO LOW!   " + warmCold);
            statusReadout.setForeground(COLOR_ACCENT_CYAN);
            SoundManager.playTooLow();
            historyListModel.insertElementAt("#" + attempts + "  →  " + guess + "  (TOO LOW)", 0);
        } else if (result == 1) {
            statusReadout.setText("TOO HIGH!   " + warmCold);
            statusReadout.setForeground(COLOR_ACCENT_CORAL);
            SoundManager.playTooHigh();
            historyListModel.insertElementAt("#" + attempts + "  →  " + guess + "  (TOO HIGH)", 0);
        } else {
            winGame();
            return;
        }

        if (activeGame.isGameOver()) loseGame();
    }

    private void winGame() {
        guessBuffer.setLength(0);
        guessDisplay.setText("✓");
        guessInputField.setEnabled(false);
        numPadPanel.setVisible(false);
        statusReadout.setText("🎉 CORRECT! IN " + activeGame.getAttempts() + " GUESSES!");
        statusReadout.setForeground(COLOR_ACCENT_TEAL);
        SoundManager.playVictory();

        int best = Leaderboard.loadBestScore();
        boolean newBest = activeGame.getAttempts() < best;

        new Timer(1800, e -> {
            // Always save to leaderboard with the name already collected
            Leaderboard.addScoreToLeaderboard(playerName, activeGame.getAttempts(),
                selectedDifficulty == 1 ? "Easy" : "Hard");

            String msg = newBest
                ? "🏆 NEW BEST SCORE!\n" + playerName + ", you won in " + activeGame.getAttempts() + " attempts!\nYour rank has been saved!"
                : "🎉 " + playerName + ", you won in " + activeGame.getAttempts() + " attempts!\nGreat game!";

            int opt = JOptionPane.showConfirmDialog(this, msg + "\n\nPlay again?",
                newBest ? "NEW HIGH SCORE!" : "Victory!",
                JOptionPane.YES_NO_OPTION);
            if (opt == JOptionPane.YES_OPTION) startNewGame();
            else {
                showLeaderboardScreen();
            }
        }) {{ setRepeats(false); start(); }};
    }

    private void loseGame() {
        guessBuffer.setLength(0);
        guessDisplay.setText("✗");
        guessInputField.setEnabled(false);
        numPadPanel.setVisible(false);
        statusReadout.setText("💀 GAME OVER! Secret was " + activeGame.getSecretNumber());
        statusReadout.setForeground(COLOR_ACCENT_CORAL);
        SoundManager.playFailure();

        new Timer(2200, e -> {
            int opt = JOptionPane.showConfirmDialog(this,
                playerName + ", you ran out of attempts.\nSecret number was: " + activeGame.getSecretNumber() + "\n\nPlay again?",
                "Game Over", JOptionPane.YES_NO_OPTION);
            if (opt == JOptionPane.YES_OPTION) startNewGame();
            else { cardLayout.show(mainContainer, "MENU"); updateMenuBestScore(); }
        }) {{ setRepeats(false); start(); }};
    }

    // ══════════════════════════════════════════════════
    // CUSTOM COMPONENTS
    // ══════════════════════════════════════════════════

    static class RoundedPanel extends JPanel {
        private final int r; private final Color bg;
        public RoundedPanel(int r, Color bg) { this.r = r; this.bg = bg; setOpaque(false); }
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, r, r);
            g2.setColor(new Color(60, 61, 75, 70));
            g2.setStroke(new BasicStroke(1));
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, r, r);
            g2.dispose();
        }
    }

    static class ModernButton extends JButton {
        private Color c1 = COLOR_ACCENT_PURPLE, c2 = COLOR_ACCENT_CORAL;
        private boolean hov = false, prs = false;
        public ModernButton(String t) {
            super(t); setContentAreaFilled(false); setFocusPainted(false); setBorderPainted(false);
            setForeground(Color.WHITE); setFont(new Font("Segoe UI", Font.BOLD, 14));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e)  { hov=true;  repaint(); }
                public void mouseExited(MouseEvent e)   { hov=false; repaint(); }
                public void mousePressed(MouseEvent e)  { prs=true;  repaint(); }
                public void mouseReleased(MouseEvent e) { prs=false; repaint(); }
            });
        }
        public void setGradient(Color s, Color e) { c1=s; c2=e; repaint(); }
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w=getWidth(), h=getHeight();
            if (prs) { g2.scale(0.97,0.97); g2.translate(w*0.015, h*0.015); }
            g2.setPaint(new GradientPaint(0,0, hov?c1.brighter():c1, w,h, hov?c2.brighter():c2));
            g2.fillRoundRect(0,0,w,h,20,20);
            FontMetrics fm=g2.getFontMetrics();
            g2.setColor(getForeground());
            g2.drawString(getText(),(w-fm.stringWidth(getText()))/2,(h-fm.getHeight())/2+fm.getAscent());
            g2.dispose();
        }
    }

    static class NumPadButton extends JButton {
        private final Color coral, teal;
        private boolean hov = false;
        public NumPadButton(String t, Color coral, Color teal) {
            super(t); this.coral=coral; this.teal=teal;
            setFont(new Font("Segoe UI", Font.BOLD, 20));
            setForeground(COLOR_TEXT_PRIMARY);
            setContentAreaFilled(false); setFocusPainted(false); setBorderPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e)  { hov=true;  repaint(); }
                public void mouseExited(MouseEvent e)   { hov=false; repaint(); }
            });
        }
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w=getWidth(), h=getHeight();
            g2.setColor(hov ? new Color(60,61,75) : new Color(40,41,50));
            g2.fillRoundRect(0,0,w,h,12,12);
            g2.setColor(new Color(65,66,80)); g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(1,1,w-2,h-2,12,12);
            FontMetrics fm=g2.getFontMetrics(getFont());
            int tx=(w-fm.stringWidth(getText()))/2, ty=(h-fm.getHeight())/2+fm.getAscent();
            if      (getText().equals("⌫")) g2.setColor(coral);
            else if (getText().equals("✔")) g2.setColor(teal);
            else                            g2.setColor(getForeground());
            g2.drawString(getText(),tx,ty);
            g2.dispose();
        }
    }

    static class CustomProgressBar extends JComponent {
        private int max=10, value=0;
        public void setMaximumValue(int m) { max=m; repaint(); }
        public void setValue(int v)        { value=v; repaint(); }
        protected void paintComponent(Graphics g) {
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w=getWidth(), h=getHeight();
            g2.setColor(new Color(45,46,56)); g2.fillRoundRect(0,0,w,h,h,h);
            if (max<=0) { g2.dispose(); return; }
            double pct=Math.min(1.0,(double)value/max);
            int fw=(int)(w*pct);
            if (fw>0) {
                int r=(int)(COLOR_ACCENT_TEAL.getRed()*(1-pct)+COLOR_ACCENT_CORAL.getRed()*pct);
                int gv=(int)(COLOR_ACCENT_TEAL.getGreen()*(1-pct)+COLOR_ACCENT_CORAL.getGreen()*pct);
                int b=(int)(COLOR_ACCENT_TEAL.getBlue()*(1-pct)+COLOR_ACCENT_CORAL.getBlue()*pct);
                g2.setColor(new Color(r,gv,b)); g2.fillRoundRect(0,0,fw,h,h,h);
            }
            g2.dispose();
        }
    }

    static class ProximityBar extends JComponent {
        private double ratio=0.0;
        public void setProximity(double v) { ratio=v; repaint(); }
        public void reset() { ratio=0.0; repaint(); }
        protected void paintComponent(Graphics g) {
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w=getWidth(), h=getHeight();
            g2.setColor(new Color(40,41,50)); g2.fillRoundRect(0,0,w,h,8,8);
            int aw=(int)(w*ratio);
            if (aw>0) {
                Color hc = ratio<0.4 ? COLOR_ACCENT_CYAN : ratio<0.7 ? COLOR_ACCENT_PURPLE : COLOR_ACCENT_CORAL;
                g2.setColor(hc); g2.fillRoundRect(0,0,aw,h,8,8);
            }
            g2.setColor(new Color(60,60,70));
            for (int i=1;i<5;i++) { int tx=(w*i)/5; g2.fillRect(tx,0,2,h); }
            g2.dispose();
        }
    }

    static class GuessLogRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean sel, boolean foc) {
            JLabel lbl=(JLabel)super.getListCellRendererComponent(l,v,i,sel,foc);
            lbl.setBorder(new EmptyBorder(5,10,5,10));
            lbl.setOpaque(true);
            String t=v.toString();
            if      (t.contains("TOO LOW"))  { lbl.setForeground(COLOR_ACCENT_CYAN);  lbl.setBackground(new Color(15,30,40)); }
            else if (t.contains("TOO HIGH")) { lbl.setForeground(COLOR_ACCENT_CORAL); lbl.setBackground(new Color(40,15,30)); }
            else                             { lbl.setForeground(COLOR_ACCENT_TEAL);  lbl.setBackground(new Color(15,40,30)); }
            if (sel) lbl.setBackground(lbl.getBackground().brighter());
            return lbl;
        }
    }

    private void styleRadioButton(JRadioButton rb) {
        rb.setOpaque(false); rb.setForeground(COLOR_TEXT_PRIMARY);
        rb.setFont(new Font("Segoe UI", Font.BOLD, 13)); rb.setFocusPainted(false);
    }
    private void styleCheckBox(JCheckBox cb) {
        cb.setOpaque(false); cb.setForeground(COLOR_TEXT_PRIMARY);
        cb.setFont(new Font("Segoe UI", Font.BOLD, 13)); cb.setFocusPainted(false);
    }
}
