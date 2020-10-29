package game;

import ch.aplu.jcardgame.*;
import ch.aplu.jgamegrid.*;

import java.awt.Color;
import java.awt.Font;
import java.io.FileReader;
import java.util.*;

@SuppressWarnings("serial")
public class Whist extends CardGame {

    public enum Suit {
        SPADES, HEARTS, DIAMONDS, CLUBS
    }

    public enum Rank {
        // Reverse order of rank importance (see rankGreater() below)
        // Order of cards is tied to card images
        ACE, KING, QUEEN, JACK, TEN, NINE, EIGHT, SEVEN, SIX, FIVE, FOUR, THREE, TWO
    }

    final String trumpImage[] = {"bigspade.gif", "bigheart.gif", "bigdiamond.gif", "bigclub.gif"};

    // return random Enum value
    public static <T extends Enum<?>> T randomEnum(Class<T> clazz) {
        int x = random.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }

    // return random Card from Hand
    public static Card randomCard(Hand hand) {
        int x = random.nextInt(hand.getNumberOfCards());
        return hand.get(x);
    }

    // return random Card from ArrayList
    public static Card randomCard(ArrayList<Card> list) {
        int x = random.nextInt(list.size());
        return list.get(x);
    }

    public boolean rankGreater(Card card1, Card card2) {
        return card1.getRankId() < card2.getRankId(); // Warning: Reverse rank order of cards (see comment on enum)
    }

    private final String version = "1.0";
    public static final int nbPlayers = 4;
    private final int handWidth = 400;
    private final int trickWidth = 40;
    private final Deck deck = new Deck(Suit.values(), Rank.values(), "cover");
    private final Location[] handLocations = {
            new Location(350, 625),
            new Location(75, 350),
            new Location(350, 75),
            new Location(625, 350)
    };
    private final Location[] scoreLocations = {
            new Location(575, 675),
            new Location(25, 575),
            new Location(575, 25),
            new Location(650, 575)
    };
    private Actor[] scoreActors = {null, null, null, null};
    private final Location trickLocation = new Location(350, 350);
    private final Location textLocation = new Location(350, 450);
    private final int thinkingTime = 2000;
    private Map<Integer, Hand> hands = new HashMap<>();
    private Location hideLocation = new Location(-500, -500);
    private Location trumpsActorLocation = new Location(50, 50);
    private boolean enforceRules = false;

    public void setStatus(String string) {
        setStatusText(string);
    }

    private int[] scores = new int[nbPlayers];

    Font bigFont = new Font("Serif", Font.BOLD, 36);

    private void initScore() {
        for (int i = 0; i < nbPlayers; i++) {
            if (PLAYERS[i].getPlayerType() == PlayerType.DISABLED) {
                continue;
            }
            scores[i] = 0;
            scoreActors[i] = new TextActor("0", Color.WHITE, bgColor, bigFont);
            addActor(scoreActors[i], scoreLocations[i]);
        }
    }

    private void updateScore(int player) {
        removeActor(scoreActors[player]);
        scoreActors[player] = new TextActor(String.valueOf(scores[player]), Color.WHITE, bgColor, bigFont);
        addActor(scoreActors[player], scoreLocations[player]);
    }

    private Card selected;

    private void initRound() {
        Hand pack = deck.toHand(false);
        // graphics
        RowLayout[] layouts = new RowLayout[nbPlayers];
        for (int i = 0; i < PLAYERS.length; i++) {
            if (PLAYERS[i].getPlayerType() == PlayerType.DISABLED) {
                continue;
            }
            dealCards(STARTING_CARDS, i, deck, pack);
            layouts[i] = new RowLayout(handLocations[i], handWidth);
            layouts[i].setRotationAngle(90 * i);
            // layouts[i].setStepDelay(10);
            hands.get(i).setView(this, layouts[i]);
            hands.get(i).setTargetArea(new TargetArea(trickLocation));
            hands.get(i).draw();
        }

//	    for (int i = 1; i < nbPlayers; i++)  // This code can be used to visually hide the cards in a hand (make them face down)
//	      hands[i].setVerso(true);
        // End graphics
    }

    private void dealCards(int cardsToDeal, int player, Deck deck, Hand pack) {
        hands.put(player, new Hand(deck));
        for (int i = 0; i < cardsToDeal; i++) {
            int x = random.nextInt(pack.getNumberOfCards());
            Card dealt = pack.get(x);
            dealt.removeFromHand(false);
            hands.get(player).insert(dealt, false);
        }
        hands.get(player).sort(Hand.SortType.SUITPRIORITY, true);
        if (PLAYERS[player].getPlayerType() != PlayerType.HUMAN) return;
        // Set up human player for interaction
        CardListener cardListener = new CardAdapter()  // Human Player plays card
        {
            public void leftDoubleClicked(Card card) {
                selected = card;
                hands.get(player).setTouchEnabled(false);
            }
        };
        hands.get(player).addCardListener(cardListener);
    }

    private String printHand(ArrayList<Card> cards) {
        String out = "";
        for (int i = 0; i < cards.size(); i++) {
            out += cards.get(i).toString();
            if (i < cards.size() - 1) out += ",";
        }
        return (out);
    }

    private Optional<Integer> playRound() {  // Returns winner, if any
        // Select and display trump suit
        final Suit trumps = randomEnum(Suit.class);
        final Actor trumpsActor = new Actor("sprites/" + trumpImage[trumps.ordinal()]);
        addActor(trumpsActor, trumpsActorLocation);
        // End trump suit
        Hand trick;
        int winner;
        Card winningCard;
        Suit lead = null;
        int nextPlayer;
        do {
            nextPlayer = random.nextInt(nbPlayers); // randomly select player to lead for this round
        } while (PLAYERS[nextPlayer].getPlayerType() == PlayerType.DISABLED);
        for (int i = 0; i < STARTING_CARDS; i++) {
            trick = new Hand(deck);
            selected = null;
            if (PLAYERS[nextPlayer].getPlayerType() == PlayerType.HUMAN) {  // Select lead depending on player type
                hands.get(nextPlayer).setTouchEnabled(true);
                setStatus("Player " + nextPlayer + " double-click on card to lead.");
                while (null == selected) delay(100);
            } else {
                setStatusText("Player " + nextPlayer + " thinking...");
                delay(thinkingTime);
                // selected = randomCard(hands[nextPlayer]);
                selected = AI.getCard(trick, hands.get(nextPlayer), trumps, lead, PLAYERS[nextPlayer], hands.size());
            }
            // Lead with selected card
            trick.setView(this, new RowLayout(trickLocation, (trick.getNumberOfCards() + 2) * trickWidth));
            trick.draw();
            selected.setVerso(false);
            // No restrictions on the card being lead
            lead = (Suit) selected.getSuit();
            selected.transfer(trick, true); // transfer to trick (includes graphic effect)
            winner = nextPlayer;
            winningCard = selected;
            System.out.println("New trick: Lead Player = " + nextPlayer + ", Lead suit = " + selected.getSuit() + ", Trump suit = " + trumps);
            System.out.println("Player " + nextPlayer + " play: " + selected.toString() + " from [" + printHand(hands.get(nextPlayer).getCardList()) + "]");
            // End Lead
            for (int j = 1; j < hands.size(); j++) {
                do {
                    nextPlayer++;
                    if (nextPlayer >= nbPlayers)
                        nextPlayer = 0;  // From last back to first
                } while (PLAYERS[nextPlayer].getPlayerType() == PlayerType.DISABLED);
                selected = null;
                if (PLAYERS[nextPlayer].getPlayerType() == PlayerType.HUMAN) {
                    hands.get(nextPlayer).setTouchEnabled(true);
                    setStatus("Player 0 double-click on card to follow.");
                    while (null == selected) delay(100);
                } else {
                    setStatusText("Player " + nextPlayer + " thinking...");
                    delay(thinkingTime);
                    // selected = randomCard(hands[nextPlayer]);
                    selected = AI.getCard(trick, hands.get(nextPlayer), trumps, lead, PLAYERS[nextPlayer], hands.size() - j);
                }
                // Follow with selected card
                trick.setView(this, new RowLayout(trickLocation, (trick.getNumberOfCards() + 2) * trickWidth));
                trick.draw();
                selected.setVerso(false);  // In case it is upside down
                // Check: Following card must follow suit if possible
                if (selected.getSuit() != lead && hands.get(nextPlayer).getNumberOfCardsWithSuit(lead) > 0) {
                    // Rule violation
                    String violation = "Follow rule broken by player " + nextPlayer + " attempting to play " + selected;
                    //System.out.println(violation);
                    if (enforceRules)
                        try {
                            throw (new BrokeRuleException(violation));
                        } catch (BrokeRuleException e) {
                            e.printStackTrace();
                            System.out.println("A cheating player spoiled the game!");
                            System.exit(1);
                        }
                }
                // End Check
                selected.transfer(trick, true); // transfer to trick (includes graphic effect)
                System.out.println("Winning card: " + winningCard.toString());
                System.out.println("Player " + nextPlayer + " play: " + selected.toString() + " from [" + printHand(hands.get(nextPlayer).getCardList()) + "]");
                if ( // beat current winner with higher card
                        (selected.getSuit() == winningCard.getSuit() && rankGreater(selected, winningCard)) ||
                                // trumped when non-trump was winning
                                (selected.getSuit() == trumps && winningCard.getSuit() != trumps)) {
                    winner = nextPlayer;
                    winningCard = selected;
                }
                // End Follow
            }
            delay(600);
            trick.setView(this, new RowLayout(hideLocation, 0));
            trick.draw();
            nextPlayer = winner;
            System.out.println("Winner: " + winner);
            setStatusText("Player " + nextPlayer + " wins trick.");
            lead = null;
            scores[nextPlayer]++;
            updateScore(nextPlayer);
            if (WINNING_SCORE == scores[nextPlayer])
                return Optional.of(nextPlayer);
        }
        removeActor(trumpsActor);
        return Optional.empty();
    }

    public Whist() {
        super(700, 700, 30);
        setTitle("Whist (V" + version + ") Constructed for UofM SWEN30006 with JGameGrid (www.aplu.ch)");
        setStatusText("Initializing...");
        initScore();
        Optional<Integer> winner;
        do {
            initRound();
            winner = playRound();
        } while (!winner.isPresent());
        addActor(new Actor("sprites/gameover.gif"), textLocation);
        setStatusText("Game over. Winner is player: " + winner.get());
        refresh();
    }

    public static void main(String[] args) {
        try (FileReader inStream = new FileReader("whist.properties")) {
            Properties properties = new Properties();
            properties.load(inStream);
            STARTING_CARDS = Integer.parseInt(properties.getProperty("StartingCards"));
            WINNING_SCORE = Integer.parseInt(properties.getProperty("WinningScore"));
            for (int i = 0; i < nbPlayers; i++) {
                PLAYERS[i] = PlayerFactory.getPlayer(0, properties.getProperty("Player" + i),
                        properties.getProperty("Player" + i + "FilterStrategy"), properties.getProperty("Player" + i + "SelectionStrategy"));
            }
            if (properties.containsKey("Seed")) {
                int seed = Integer.parseInt(properties.getProperty("Seed"));
                random = new Random(seed);
            } else {
                random = new Random();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading in properties");
            System.exit(1);
        }

        int enabledPlayerCount = 0;
        for (Player player : PLAYERS) {
            if (player.getPlayerType() != PlayerType.DISABLED) {
                enabledPlayerCount++;
            }
        }
        if (enabledPlayerCount <= 1) {
            System.out.println("Only 1 or fewer players were enabled (marked as AI or HUMAN), Whist does not support this.");
            System.exit(1);
        }

        new Whist();
    }

    public static Player[] PLAYERS = new Player[nbPlayers];
    private static int STARTING_CARDS;
    public static int WINNING_SCORE;
    public static Random random;

}
