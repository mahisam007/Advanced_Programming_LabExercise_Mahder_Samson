import java.util.ArrayList;
import java.util.List;

public class PokerGame {
    public enum Phase { PRE_FLOP, FLOP, TURN, RIVER, SHOWDOWN }

    private Phase currentPhase;
    private final List<Player> players;
    private final List<Card> communityCards;
    private Deck deck;
    private int pot;

    public PokerGame() {
        players = new ArrayList<>();
        communityCards = new ArrayList<>();
        currentPhase = Phase.PRE_FLOP;
    }

    public void addPlayer(String name) {
        players.add(new Player(name, 1000));
    }

    public void startNewRound() {
        deck = new Deck();
        communityCards.clear();
        pot = 0;
        currentPhase = Phase.PRE_FLOP;

        for (Player player : players) {
            player.reset();
            player.addCard(deck.dealCard());
            player.addCard(deck.dealCard());
        }
    }

    public void advancePhase() {
        switch (currentPhase) {
            case PRE_FLOP -> { dealFlop(); currentPhase = Phase.FLOP; }
            case FLOP -> { dealTurn(); currentPhase = Phase.TURN; }
            case TURN -> { dealRiver(); currentPhase = Phase.RIVER; }
            case RIVER -> currentPhase = Phase.SHOWDOWN;
        }
    }

    private void dealFlop() { for (int i = 0; i < 3; i++) communityCards.add(deck.dealCard()); }
    private void dealTurn() { communityCards.add(deck.dealCard()); }
    private void dealRiver() { communityCards.add(deck.dealCard()); }

    public Phase getCurrentPhase() { return currentPhase; }
    public List<Player> getPlayers() { return players; }
    public List<Card> getCommunityCards() { return communityCards; }
    public int getPot() { return pot; }

    public void bet(Player player, int amount) {
        player.bet(amount);
        pot += amount;
    }

    public Player determineWinner() {
        Player winner = null;
        int bestScore = -1;
        for (Player player : players) {
            if (player.isFolded()) continue;
            List<Card> totalCards = new ArrayList<>(player.getHand());
            totalCards.addAll(communityCards);
            int score = HandEvaluator.evaluate(totalCards);
            if (score > bestScore) {
                bestScore = score;
                winner = player;
            }
        }
        if (winner != null) winner.winChips(pot);
        return winner;
    }
}