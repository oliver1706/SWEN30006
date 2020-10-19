package game;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import game.Whist.Suit;

import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


public class AI {
    private FilterStrategy[] filterStrategies = new FilterStrategy[4];
    private SelectionStrategy[] selectionStrategies = new SelectionStrategy[4];

    private static final Comparator<Card> rankComparator = new HighestRankSorter();

    public AI() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (Whist.PLAYER_0 == 1){
            filterStrategies[0] = (FilterStrategy) Class.forName(Whist.PLAYER_0_FILTER_STRATEGY).getDeclaredConstructor().newInstance();
            selectionStrategies[0] = (SelectionStrategy) Class.forName(Whist.PLAYER_0_SELECTION_STRATEGY).getDeclaredConstructor().newInstance();
        }
        if (Whist.PLAYER_1 == 1){
            filterStrategies[1] = (FilterStrategy) Class.forName(Whist.PLAYER_1_FILTER_STRATEGY).getDeclaredConstructor().newInstance();
            selectionStrategies[1] = (SelectionStrategy) Class.forName(Whist.PLAYER_1_SELECTION_STRATEGY).getDeclaredConstructor().newInstance();
        }
        if (Whist.PLAYER_2 == 1){
            filterStrategies[2] = (FilterStrategy) Class.forName(Whist.PLAYER_2_FILTER_STRATEGY).getDeclaredConstructor().newInstance();
            selectionStrategies[2] = (SelectionStrategy) Class.forName(Whist.PLAYER_2_SELECTION_STRATEGY).getDeclaredConstructor().newInstance();
        }
        if (Whist.PLAYER_3 == 1){
            filterStrategies[3] = (FilterStrategy) Class.forName(Whist.PLAYER_3_FILTER_STRATEGY).getDeclaredConstructor().newInstance();
            selectionStrategies[3] = (SelectionStrategy) Class.forName(Whist.PLAYER_3_SELECTION_STRATEGY).getDeclaredConstructor().newInstance();
        }
    }
    public Card getCard(Hand currentTrick, Hand currentHand, Suit trump, Suit lead, int player, int turnsLeft) {
        List<Card> filteredHand;
        if(lead != null){
            filteredHand = getFilteredHand(currentHand, trump, lead, player);
        } else{
            filteredHand = currentHand.getCardList();
        }
        filteredHand.sort(rankComparator);
        return getCardToPlay(filteredHand, trump, lead, currentTrick, player, turnsLeft);
    }

    private List<Card> getFilteredHand(Hand currentHand, Suit trump, Suit lead, int player){
        return filterStrategies[player].getFilteredHand(currentHand, trump, lead, player);
    }

    private Card getCardToPlay(List<Card> filteredHand, Suit trump, Suit lead, Hand currentTrick, int player, int turn){
        return selectionStrategies[player].getCard(filteredHand, trump, lead, currentTrick, player, turn);
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
