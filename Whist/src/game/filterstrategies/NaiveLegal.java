package game.filterstrategies;
// Team 7

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import game.FilterStrategy;
import game.Whist;

import java.util.List;

public class NaiveLegal implements FilterStrategy {
    @Override
    public List<Card> getFilteredHand(Hand currentHand, Whist.Suit trump, Whist.Suit lead) {
        if (lead == null) return currentHand.getCardList();
        List<Card> trumpAndLeadCards = currentHand.getCardsWithSuit(lead);
        if (trump != lead) {
            trumpAndLeadCards.addAll(currentHand.getCardsWithSuit(trump));
        }
        if (trumpAndLeadCards.isEmpty()){
            return currentHand.getCardList();
        } else {
            return trumpAndLeadCards;
        }
    }
}
