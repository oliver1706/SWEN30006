package game.filterstrategies;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import game.FilterStrategy;
import game.Whist;

import java.util.List;

public class TrumpSaving implements FilterStrategy {
    @Override
    public List<Card> getFilteredHand(Hand currentHand, Whist.Suit trump, Whist.Suit lead) {
        List<Card> leadCards = currentHand.getCardsWithSuit(lead);
        if (!leadCards.isEmpty()) { return leadCards; }
        if (lead == trump) {
            return currentHand.getCardList();
        }
        List<Card> trumpCards = currentHand.getCardsWithSuit(trump);
        if (!trumpCards.isEmpty()) { return trumpCards; }
        return currentHand.getCardList();
    }
}
