package game.selectionstrategies;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import game.AI;
import game.SelectionStrategy;
import game.Whist;

import java.util.Comparator;
import java.util.List;

public class HighestRank implements SelectionStrategy {
    @Override
    public Card getCard(List<Card> hand, Whist.Suit trump, Whist.Suit lead, Hand currentTrick, int turnsLeft) {
        hand.sort(new HighestRankSorter());
        return hand.get(0);
    }

    private static class HighestRankSorter implements Comparator<Card> {

        @Override
        public int compare(Card o1, Card o2) {
            if (o1.getRankId() == o2.getRankId()){
                return 0;
            } else if (o1.getRankId() < o2.getRankId()){
                return 1;
            } else {
                return -1;
            }
        }
    }
}
