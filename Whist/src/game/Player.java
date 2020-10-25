package game;

public class Player {

    private FilterStrategy filterStrategy;
    private SelectionStrategy selectionStrategy;
    private PlayerType playerType;

    public Player(String status, String filterStrategyString, String selectionStrategyString){
        try {
            playerType = PlayerType.valueOf(status);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Could not load PlayerStatus " + status);
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
    }

    public FilterStrategy getFilterStrategy(){
        return filterStrategy;
    }

    public SelectionStrategy getSelectionStrategy(){
        return selectionStrategy;
    }

    public PlayerType getPlayerType(){
        return playerType;
    }
}
