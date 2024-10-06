package ChessCore;

public class memento {
    
    private ChessGame game;
    private Move move;

    public void mementoIni(ChessGame game, Move move) {
        this.game = game;
        this.move = move;
    }
    
    public void saveMove(){
        this.game.getStack().push(this.game.getBoard().getPieceAtSquare(this.move.getToSquare()));
        this.game.setLastMove(this.move);
    }
}
