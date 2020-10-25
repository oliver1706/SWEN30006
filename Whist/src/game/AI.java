package game;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import game.Whist.Suit;

import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


public class AI {
    private static final Comparator<Card> rankComparator = new HighestRankSorter();

    public static Card getCard(Hand currentTrick, Hand currentHand, Suit trump, Suit lead, int player, int turnsLeft) {
        List<Card> filteredHand;
        if(lead != null){
            filteredHand = getFilteredHand(currentHand, trump, lead, player);
        } else{
            filteredHand = currentHand.getCardList();
        }
        filteredHand.sort(rankComparator);
        return getCardToPlay(filteredHand, trump, lead, currentTrick, player, turnsLeft);
    }

    private static List<Card> getFilteredHand(Hand currentHand, Suit trump, Suit lead, int player){
        return Whist.PLAYERS[player].getFilterStrategy().getFilteredHand(currentHand, trump, lead, player);
    }

    private static Card getCardToPlay(List<Card> filteredHand, Suit trump, Suit lead, Hand currentTrick, int player, int turn){
        return Whist.PLAYERS[player].getSelectionStrategy().getCard(filteredHand, trump, lead, currentTrick, player, turn);
    }

    private static class HighestRankSorter implements Comparator<Card>{

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

    private static List<Card> getCardsWithSuit(List<Card> cards, Suit suit) {
        return cards.stream().filter(card -> card.getSuit() == suit).collect(Collectors.toList());
    }
}
