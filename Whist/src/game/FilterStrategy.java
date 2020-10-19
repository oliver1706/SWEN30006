package game;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import game.Whist.Suit;

import java.util.List;

public interface FilterStrategy {
    public List<Card> getFilteredHand(Hand currentHand, Suit trump, Suit lead, int player);
}
