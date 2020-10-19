package game.selectionstrategies;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import game.SelectionStrategy;
import game.Whist;

import java.util.List;

public class Random implements SelectionStrategy {
    private java.util.Random random = new java.util.Random();
    @Override
    public Card getCard(List<Card> hand, Whist.Suit trump, Whist.Suit lead, Hand currentTrick, int player, int turnsLeft) {
        return hand.get(random.nextInt(hand.size()));
    }
}
