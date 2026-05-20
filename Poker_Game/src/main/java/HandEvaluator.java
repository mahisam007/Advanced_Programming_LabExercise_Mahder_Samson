import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HandEvaluator {

    public static int evaluate(List<Card> cards) {
        Map<Integer, Integer> countMap = new HashMap<>();

        for (Card card : cards) {
            int value = card.getValue();
            countMap.put(value, countMap.getOrDefault(value, 0) + 1);
        }

        int score = 0;
        for (int count : countMap.values()) {
            if (count == 4) {
                score = 700;
            } else if (count == 3) {
                score = 400;
            } else if (count == 2) {
                score = Math.max(score, 200);
            }
        }

        for (Card c : cards) {
            score += c.getValue();
        }

        return score;
    }
}
