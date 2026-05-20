import java.util.ArrayList;
import java.util.List;

public class Player {
    private final String name;
    private int chips;
    private boolean folded;
    private final List<Card> hand;

    public Player(String name, int chips) {
        this.name = name;
        this.chips = chips;
        this.folded = false;
        this.hand = new ArrayList<>();
    }
    public String getName() {
        return name;
    }

    public int getChips() {
        return chips;
    }

    public void addCard(Card card) {
        hand.add(card);
    }

    public List<Card> getHand() {
        return hand;
    }

    public void bet(int amount) {
        chips -= amount;
    }
    public void winChips(int amount) {
        chips += amount;
    }

    public boolean isFolded() {
        return folded;
    }

    public void fold() {
        folded = true;
    }

    public void reset() {
        hand.clear();
        folded = false;
    }
}
