package game;

public class Player {

    private FilterStrategy filterStrategy;
    private SelectionStrategy selectionStrategy;
    private PlayerType playerType;

    public Player(PlayerType type, FilterStrategy filterStrategy, SelectionStrategy selectionStrategy) {
        this.playerType = type;
        this.filterStrategy = filterStrategy;
        this.selectionStrategy = selectionStrategy;
    }

    public FilterStrategy getFilterStrategy() {
        return filterStrategy;
    }

    public SelectionStrategy getSelectionStrategy() {
        return selectionStrategy;
    }

    public PlayerType getPlayerType() {
        return playerType;
    }
}
