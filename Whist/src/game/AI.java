package game;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Deck;
import ch.aplu.jcardgame.Hand;
import game.Whist.Suit;

import java.util.*;
import java.util.stream.Collectors;


public class AI {
    public Deck deck;

    public AI (Deck deck){
        this.deck = deck;
    }
    private static Random random = new Random();

    public enum FilterMethod {
        NAIVE,
        SAVE_TRUMP,
        NONE
    }

    public enum SelectMethod {
        RANDOM,
        HIGHEST_RANK,
        SMART
    }

    private static final Comparator<Card> rankComparator = new HighestRankSorter();

    public static Card getCard(Hand currentTrick, Hand currentHand, Suit trump, Suit lead, int player, int turn) {
        List<Card> filteredHand;
        if(lead != null){
            filteredHand = getFilteredHand(currentHand, trump, lead, player);
        } else{
            filteredHand = currentHand.getCardList();
        }
        filteredHand.sort(rankComparator);
        return getCardToPlay(filteredHand, trump, lead, currentTrick, player, turn);
    }

    private static List<Card> getFilteredHand(Hand currentHand, Suit trump, Suit lead, int player){
        FilterMethod filterMethod = FilterMethod.NONE;
        switch (filterMethod) {
            case NAIVE:
                List<Card> trumpAndLeadCards = currentHand.getCardsWithSuit(lead);
                if (trump != lead) {
                    trumpAndLeadCards.addAll(currentHand.getCardsWithSuit(trump));
                }
                if (trumpAndLeadCards.isEmpty()){
                    return currentHand.getCardList();
                } else {
                    return trumpAndLeadCards;
                }
            case SAVE_TRUMP:
                List<Card> leadCards = currentHand.getCardsWithSuit(lead);
                if (!leadCards.isEmpty()) { return leadCards; }
                if (lead == trump) {
                    return currentHand.getCardList();
                }
                List<Card> trumpCards = currentHand.getCardsWithSuit(trump);
                if (!trumpCards.isEmpty()) { return trumpCards; }
                return currentHand.getCardList();
            default:
                return currentHand.getCardList();
        }
    }

    private static Card getCardToPlay(List<Card> filteredHand, Suit trump, Suit lead, Hand currentTrick, int player, int turn){
        SelectMethod selectMethod = SelectMethod.SMART;
        switch (selectMethod) {
            case HIGHEST_RANK:
                filteredHand.sort(new HighestRankSorter());
                return filteredHand.get(0);
            case SMART:
                if (lead == null) {
                    return filteredHand.get(random.nextInt(filteredHand.size()));
                }
                HighestCardSorter highestCardSorter = new HighestCardSorter(trump, lead);
                // Check if we can win
//                List<Card> playedTrumps = currentTrick.getCardsWithSuit(trump);
//                playedTrumps.sort(rankComparator);
//                List<Card> myTrumps = getCardsWithSuit(filteredHand, trump);
//                myTrumps.sort(rankComparator);
//                List<Card> playedLead = currentTrick.getCardsWithSuit(lead);
//                playedLead.sort(rankComparator);
//                List<Card> myLeads = getCardsWithSuit(filteredHand, lead);
//                //System.out.println()
//                myLeads.sort(rankComparator);
                List<Card> playedCards = currentTrick.getCardList();
                playedCards.sort(highestCardSorter);
                System.out.println("Highest current played cards are " + playedCards);
                Card currentHighest = playedCards.get(playedCards.size() - 1);
                List<Card> higherCards = filteredHand.stream().filter(card -> highestCardSorter.compare(card, currentHighest) > 0)
                        .sorted(highestCardSorter).collect(Collectors.toList());
                System.out.println("My higher cards are " + higherCards);
                if(higherCards.isEmpty()){
                    //We have no cards that can win
                    return filteredHand.get(0);
                } else {
                    return higherCards.get(0);
                }
//                if (lead != trump && playedTrumps.isEmpty()){
//                    if (!myTrumps.isEmpty()) {
//                        // Play lowest trump
//                        return myTrumps.get(myTrumps.size() - 1);
//                    } else if (myLeads.isEmpty()) {
//                        // Nothing we can do, play the lowest
//                        return filteredHand.get(filteredHand.size() - 1);
//                    } else {
//                        //No trumps have been played, we have lead cards
//                        Card highestLeadCard = currentTrick.getCardsWithSuit(lead)
//                                .get(currentTrick.getNumberOfCardsWithSuit(lead) - 1);
//                        System.out.println(highestLeadCard);
//                        if (highestLeadCard.getRankId() > myLeads.get(myLeads.size() - 1).getRankId()) {
//                            // Cheat by not playing our lead cards against a higher card
//                            return filteredHand.get(filteredHand.size() - 1);
//                        } else {
//                            // return the lowest lead card we have that's still above the lowest
//                            List<Card> potentialHigherCards = myLeads.stream().filter(card -> card.getRankId() > highestLeadCard.getRankId())
//                                    .sorted(rankComparator).collect(Collectors.toList());
//                            return potentialHigherCards.get(potentialHigherCards.size() - 1);
//                        }
//                    }
//                } else {
//                    if(myLeads.isEmpty()){
//                        // Nothing to play
//                        return filteredHand.get(filteredHand.size() - 1);
//                    } else {
//                        // We have lead cards
//                        Card highestTrumpCard = playedTrumps.get(playedTrumps.size() - 1);
//                        System.out.println(highestTrumpCard);
//                        if (highestTrumpCard.getRankId() > myLeads.get(myLeads.size() - 1).getRankId()) {
//                            // Cheat by not playing our trump cards against a higher card
//                            return filteredHand.get(filteredHand.size() - 1);
//                        } else {
//                            // return the lowest lead card we have that's still above the lowest
//                            List<Card> potentialHigherCards = myTrumps.stream().filter(card -> card.getRankId() > highestTrumpCard.getRankId())
//                                    .sorted(rankComparator).collect(Collectors.toList());
//                            return potentialHigherCards.get(potentialHigherCards.size() - 1);
//                        }
//                    }
//
//                }
            default:
                return filteredHand.get(random.nextInt(filteredHand.size()));

        }
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

    private static class HighestCardSorter implements Comparator<Card> {
        Suit lead;
        Suit trump;


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

        public HighestCardSorter(Suit trump, Suit lead){
            this.trump = trump;
            this.lead = lead;
        }
    }

    private static List<Card> getCardsWithSuit(List<Card> cards, Suit suit) {
        return cards.stream().filter(card -> card.getSuit() == suit).collect(Collectors.toList());
    }
}
