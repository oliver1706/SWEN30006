package game.filterstrategies;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import game.FilterStrategy;
import game.Whist;

import java.util.List;

public class None implements FilterStrategy {
    @Override
    public List<Card> getFilteredHand(Hand currentHand, Whist.Suit trump, Whist.Suit lead) {
        return currentHand.getCardList();
    }
}
