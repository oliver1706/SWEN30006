package game;

import java.util.Properties;

public class PlayerFactory {

    private static PlayerType playerType = null;
    private static FilterStrategy filterStrategy = null;
    private static SelectionStrategy selectionStrategy = null;

    public static Player getPlayer(int playerNumber, Properties properties) {
        String playerTypeString = null;
        try {
            playerTypeString = properties.getProperty("Player" + playerNumber);
            ;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Could not load property Player" + playerNumber + "FilterStrategy");
            System.exit(1);
        }
        try {
            playerType = PlayerType.valueOf(playerTypeString);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Could not load PlayerType for Player" + playerNumber + ", valid options are DISABLED, AI and HUMAN.");
            System.exit(1);
        }
        if (playerType == PlayerType.AI) {
            String filterStrategyString = null;
            String selectionStrategyString = null;
            try {
                filterStrategyString = properties.getProperty("Player" + playerNumber + "FilterStrategy");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Could not load property Player" + playerNumber + "FilterStrategy");
                System.exit(1);
            }
            try {
                selectionStrategyString = properties.getProperty("Player" + playerNumber + "SelectionStrategy");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Could not load property Player" + playerNumber + "SelectionStrategy");
                System.exit(1);
            }
            try {
                filterStrategy = (FilterStrategy) Class.forName(filterStrategyString).getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Could not load FilterStrategy " + filterStrategyString);
                System.exit(1);
            }
            try {
                selectionStrategy = (SelectionStrategy) Class.forName(selectionStrategyString).getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Could not load SelectionStrategy " + selectionStrategyString);
                System.exit(1);
            }
        }

        return new Player(playerType, filterStrategy, selectionStrategy);
    }
}
