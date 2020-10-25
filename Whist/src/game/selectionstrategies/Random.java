package game.selectionstrategies;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import game.SelectionStrategy;
import game.Whist;

import java.util.List;

public class Random implements SelectionStrategy {
    @Override
    public Card getCard(List<Card> hand, Whist.Suit trump, Whist.Suit lead, Hand currentTrick, int turnsLeft) {
        return hand.get(Whist.random.nextInt(hand.size()));
    }
}
