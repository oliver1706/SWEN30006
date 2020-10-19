package game;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import game.Whist.Suit;

import java.util.List;

public interface SelectionStrategy {
    public Card getCard(List<Card> hand, Suit trump, Suit lead, Hand currentTrick, int player, int turnsLeft);
}
