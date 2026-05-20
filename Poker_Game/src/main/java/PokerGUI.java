import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

public class PokerGUI extends JFrame {
    private final PokerGame game;
    private final JLabel statusLabel;
    private final JPanel communityPanel;
    private final JPanel playerHandPanel;
    private final JPanel opponentPanel;
    private final JButton actionButton;
    private final JTextArea logArea;

    public PokerGUI() {
        game = new PokerGame();
        game.addPlayer("You");
        game.addPlayer("Computer");

        setTitle("AASTU Poker - High Res Edition");
        setSize(1000, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(20, 80, 20));

        // TOP: Status and Opponent
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setOpaque(false);

        statusLabel = new JLabel("Welcome! Press Start", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Serif", Font.BOLD, 32));
        statusLabel.setForeground(Color.YELLOW);

        opponentPanel = new JPanel(new FlowLayout());
        opponentPanel.setBorder(BorderFactory.createTitledBorder(null, "COMPUTER", 0, 0, null, Color.WHITE));
        opponentPanel.setOpaque(false);
        opponentPanel.setPreferredSize(new Dimension(950, 180));

        topContainer.add(statusLabel, BorderLayout.NORTH);
        topContainer.add(opponentPanel, BorderLayout.CENTER);
        add(topContainer, BorderLayout.NORTH);

        // CENTER: Community Cards
        communityPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 60));
        communityPanel.setOpaque(false);
        add(communityPanel, BorderLayout.CENTER);

        // EAST: Game Log
        logArea = new JTextArea(10, 18);
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        add(new JScrollPane(logArea), BorderLayout.EAST);

        // SOUTH: Your Hand & Betting
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setOpaque(false);

        playerHandPanel = new JPanel(new FlowLayout());
        playerHandPanel.setBorder(BorderFactory.createTitledBorder(null, "YOUR HAND", 0, 0, null, Color.WHITE));
        playerHandPanel.setOpaque(false);

        JPanel controls = new JPanel(new FlowLayout());
        actionButton = new JButton("Start Game");
        JButton raiseBtn = new JButton("Raise $50");
        JButton foldBtn = new JButton("Fold");

        actionButton.addActionListener(e -> handleGameFlow());
        raiseBtn.addActionListener(e -> {
            game.bet(game.getPlayers().get(0), 50);
            logArea.append("You raised $50. Pot: " + game.getPot() + "\n");
        });
        foldBtn.addActionListener(e -> {
            game.getPlayers().get(0).fold();
            statusLabel.setText("You Folded! Computer Wins.");
            logArea.append("You folded.\n");
        });

        controls.add(actionButton);
        controls.add(raiseBtn);
        controls.add(foldBtn);

        southPanel.add(playerHandPanel, BorderLayout.CENTER);
        southPanel.add(controls, BorderLayout.SOUTH);
        add(southPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
    }

    private void handleGameFlow() {
        if (actionButton.getText().equals("Start Game") || actionButton.getText().equals("Next Round")) {
            game.startNewRound();
            statusLabel.setText("Phase: Pre-Flop");
            actionButton.setText("Deal Flop");
        } else if (game.getCurrentPhase() != PokerGame.Phase.SHOWDOWN) {
            game.advancePhase();
            updateButtonLabel();
        } else {
            Player winner = game.determineWinner();
            statusLabel.setText("WINNER: " + winner.getName() + "!");
            actionButton.setText("Next Round");
        }
        updateUI();
    }

    private void updateButtonLabel() {
        switch (game.getCurrentPhase()) {
            case FLOP -> { actionButton.setText("Deal Turn"); statusLabel.setText("Phase: Flop"); }
            case TURN -> { actionButton.setText("Deal River"); statusLabel.setText("Phase: Turn"); }
            case RIVER -> { actionButton.setText("Show Winner"); statusLabel.setText("Phase: River"); }
        }
    }

    private void updateUI() {
        communityPanel.removeAll();
        opponentPanel.removeAll();
        playerHandPanel.removeAll();

        // 1. Show Community Cards (Hidden slots use the Pinterest back)
        for (Card c : game.getCommunityCards()) {
            communityPanel.add(new CardBox(c, false));
        }
        for (int i = game.getCommunityCards().size(); i < 5; i++) {
            communityPanel.add(new CardBox(null, true));
        }

        // 2. Hide Opponent Hand until SHOWDOWN phase
        boolean revealOpponent = (game.getCurrentPhase() == PokerGame.Phase.SHOWDOWN);
        for (Card c : game.getPlayers().get(1).getHand()) {
            opponentPanel.add(new CardBox(c, !revealOpponent));
        }

        // 3. Always show Your Hand
        for (Card c : game.getPlayers().get(0).getHand()) {
            playerHandPanel.add(new CardBox(c, false));
        }

        revalidate();
        repaint();
    }

    private static class CardBox extends JLabel {
        private static BufferedImage masterSheet;
        private static BufferedImage cardBack;

        public CardBox(Card card, boolean faceDown) {
            setPreferredSize(new Dimension(100, 145));
            try {
                if (masterSheet == null) {
                    masterSheet = ImageIO.read(getClass().getResource("/images/deck.png"));
                }
                if (cardBack == null) {
                    // This loads your Pinterest screenshot saved as card_bg.png
                    cardBack = ImageIO.read(getClass().getResource("/images/card_bg.png"));
                }

                if (faceDown) {
                    // Use scaled version of your Pinterest image for the back
                    Image scaledBack = cardBack.getScaledInstance(100, 145, Image.SCALE_SMOOTH);
                    setIcon(new ImageIcon(scaledBack));
                    setText("");
                } else if (card != null) {
                    int row = getRowForSuit(card.getSuit());
                    int col = getColForRank(card.getRank());
                    setIcon(new ImageIcon(cropCard(row, col)));
                }
            } catch (Exception e) {
                // Fallback to text if a resource fails to load
                setText(faceDown ? "BACK" : (card != null ? card.toString() : "??"));
                setOpaque(true);
                setBackground(Color.WHITE);
                setHorizontalAlignment(CENTER);
                setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
            }
        }

        private Image cropCard(int row, int col) {
            int cardWidth = 256;  // Width from your high-res filename
            int cardHeight = 356; // Height from your high-res filename
            BufferedImage sub = masterSheet.getSubimage(col * cardWidth, row * cardHeight, cardWidth, cardHeight);
            return sub.getScaledInstance(100, 145, Image.SCALE_SMOOTH);
        }

        private int getRowForSuit(String suit) {
            return switch (suit.toLowerCase()) {
                case "clubs" -> 0;
                case "hearts" -> 1;
                case "spades" -> 2;
                case "diamonds" -> 3;
                default -> 0;
            };
        }

        private int getColForRank(String rank) {
            return switch (rank.toUpperCase()) {
                case "A" -> 0;
                case "J" -> 10;
                case "Q" -> 11;
                case "K" -> 12;
                default -> {
                    try {
                        yield Integer.parseInt(rank) - 1;
                    } catch (Exception e) { yield 0; }
                }
            };
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PokerGUI().setVisible(true));
    }
}