package ChessCore;

public final class ClassicChessGame extends ChessGame {

    private static ClassicChessGame instance;

    private ClassicChessGame() {
        super(ClassicBoardInitializer.getInstance());
    }

    public static ClassicChessGame getInstance() {
        if (instance == null) {
            instance = new ClassicChessGame();
        }
        return instance;
    }

    @Override
    protected boolean isValidMoveCore(Move move) {
        return !Utilities.willOwnKingBeAttacked(this.getWhoseTurn(), move, this.getBoard());
    }
}
