package game.selectionstrategies;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import game.SelectionStrategy;
import game.Whist;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Smart implements SelectionStrategy {
    @Override
    public Card getCard(List<Card> hand, Whist.Suit trump, Whist.Suit lead, Hand currentTrick, int turnsLeft) {
        if (lead == null) {
            return hand.get(Whist.random.nextInt(hand.size()));
        }
        HighestCardSorter highestCardSorter = new HighestCardSorter(trump, lead);
        List<Card> playedCards = currentTrick.getCardList();
        playedCards.sort(highestCardSorter);
        System.out.println("Highest current played cards are " + playedCards);
        Card currentHighest = playedCards.get(playedCards.size() - 1);
        List<Card> higherCards = hand.stream().filter(card -> highestCardSorter.compare(card, currentHighest) > 0)
                .sorted(highestCardSorter).collect(Collectors.toList());
        System.out.println("My higher cards are " + higherCards);
        if(higherCards.isEmpty()){
            //We have no cards that can win
            return hand.get(0);
        } else {
            return higherCards.get(0);
        }
    }

    private static class HighestCardSorter implements Comparator<Card> {
        Whist.Suit lead;
        Whist.Suit trump;


        @Override
        public int compare(Card o1, Card o2){
            if(o1.getSuit() == trump && o2.getSuit() != trump) {
                return 1;
            }
            if(o1.getSuit() != trump && o2.getSuit() == trump) {
                return -1;
            }
            if(o1.getSuit() == trump && o2.getSuit() == trump) {
                return Integer.compare(o2.getRankId(), o1.getRankId());
            }
            if(o1.getSuit() == lead && o2.getSuit() != lead) {
                return 1;
            }
            if(o1.getSuit() != lead && o2.getSuit() == lead) {
                return -1;
            }
            if(o1.getSuit() == lead && o2.getSuit() == lead) {
                return Integer.compare(o2.getRankId(), o1.getRankId());
            }
            return Integer.compare(o2.getRankId(), o1.getRankId());
        }

        private HighestCardSorter(Whist.Suit trump, Whist.Suit lead){
            this.trump = trump;
            this.lead = lead;
        }
    }
}
