package game;

public class PlayerFactory {

    private static PlayerType playerType = null;
    private static FilterStrategy filterStrategy = null;
    private static SelectionStrategy selectionStrategy = null;

    public static Player getPlayer(String status, String filterStrategyString, String selectionStrategyString){
        try {
            playerType = PlayerType.valueOf(status);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Could not load PlayerType " + status + ", valid options are DISABLED, AI and HUMAN.");
            System.exit(1);
        }
        if (playerType == PlayerType.AI) {
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
