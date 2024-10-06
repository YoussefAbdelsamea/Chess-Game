package ChessCore;

import ChessCore.Pieces.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public abstract class ChessGame {

    private moveReciver moveReciver = new moveReciver();
    private Invoker Invoker = new Invoker();
    
    private ChessBoard board;
    private GameStatus gameStatus = GameStatus.IN_PROGRESS;
    private Player whoseTurn = Player.WHITE;

    private Move lastMove;
    private PiecesFactory Factory;
    
    private boolean canWhiteCastleKingSide = true;
    private boolean canWhiteCastleQueenSide = true;
    private boolean canBlackCastleKingSide = true;
    private boolean canBlackCastleQueenSide = true;
    private boolean isEnPassantMove = false;

    private static final Stack<Piece> stack = new Stack<>();

    public static Stack<Piece> getStack() {
        return stack;
    }
    
    public void setPromotionMove(boolean promotionMove) {
        this.promotionMove = promotionMove;
    }
    private boolean promotionMove = false;

    public void setIsEnPassantMove(boolean isEnPassantMove) {
        this.isEnPassantMove = isEnPassantMove;
    }

    public PiecesFactory getFactory() {
        return Factory;
    }

    protected ChessGame(BoardInitializer boardInitializer) {
        this.board = new ChessBoard(boardInitializer.initialize());
    }

    public boolean isCanWhiteCastleKingSide() {
        return canWhiteCastleKingSide;
    }

    public boolean isIsEnPassantMove() {
        return isEnPassantMove;
    }

    public boolean isCanWhiteCastleQueenSide() {
        return canWhiteCastleQueenSide;
    }

    public boolean isCanBlackCastleKingSide() {
        return canBlackCastleKingSide;
    }

    public boolean isCanBlackCastleQueenSide() {
        return canBlackCastleQueenSide;
    }

    public boolean isPromotionMove() {
        return promotionMove;
    }

    protected boolean isValidMove(Move move) {
        if (isGameEnded()) {
            return false;
        }

        Piece pieceAtFrom = board.getPieceAtSquare(move.getFromSquare());
        if (pieceAtFrom == null || pieceAtFrom.getOwner() != whoseTurn || !pieceAtFrom.isValidMove(move, this)) {
            return false;
        }

        Piece pieceAtTo = board.getPieceAtSquare(move.getToSquare());
        if (pieceAtTo != null && pieceAtTo.getOwner() == whoseTurn) {
            return false;
        }

        return isValidMoveCore(move);
    }
    
    public void setWhoseTurn(Player whoseTurn) {
        this.whoseTurn = whoseTurn;
    }

    public void setLastMove(Move lastMove) {
        this.lastMove = lastMove;
    }
    
    public void setCanWhiteCastleKingSide(boolean canWhiteCastleKingSide) {
        this.canWhiteCastleKingSide = canWhiteCastleKingSide;
    }

    public void setCanWhiteCastleQueenSide(boolean canWhiteCastleQueenSide) {
        this.canWhiteCastleQueenSide = canWhiteCastleQueenSide;
    }

    public void setCanBlackCastleKingSide(boolean canBlackCastleKingSide) {
        this.canBlackCastleKingSide = canBlackCastleKingSide;
    }

    public void setCanBlackCastleQueenSide(boolean canBlackCastleQueenSide) {
        this.canBlackCastleQueenSide = canBlackCastleQueenSide;
    }

    public Move getLastMove() {
        return lastMove;
    }

    public Player getWhoseTurn() {
        return whoseTurn;
    }

    ChessBoard getBoard() {
        return board;
    }

    protected abstract boolean isValidMoveCore(Move move);

    public boolean isTherePieceInBetween(Move move) {
        return board.isTherePieceInBetween(move);
    }

    public boolean hasPieceIn(Square square) {
        return board.getPieceAtSquare(square) != null;
    }

    public boolean hasPieceInSquareForPlayer(Square square, Player player) {
        Piece piece = board.getPieceAtSquare(square);
        return piece != null && piece.getOwner() == player;
    }

    public Piece undoMove(Move move, Move lastMove) {
        moveReciver.moveUpdate(move, lastMove, this);
        
        Command undoMove = new undoMove(moveReciver);
        
        Invoker.setCommand(undoMove);
        if(Invoker.doComand()){
            return (Piece) stack.pop();
        }
        return null;
    }

    public boolean makeMove(Move move) {
        
        moveReciver.reciverUpdate(move, this);
        
        Command makeMove = new makeMove(moveReciver);
        
        Invoker.setCommand(makeMove);
        return Invoker.doComand();
    }

    public void updateGameStatus() {
        Player whoseTurn = getWhoseTurn();
        boolean isInCheck = Utilities.isInCheck(whoseTurn, getBoard());
        boolean hasAnyValidMoves = hasAnyValidMoves();
        if (isInCheck) {
            if (!hasAnyValidMoves && whoseTurn == Player.WHITE) {
                gameStatus = GameStatus.BLACK_WON;
            } else if (!hasAnyValidMoves && whoseTurn == Player.BLACK) {
                gameStatus = GameStatus.WHITE_WON;
            } else if (whoseTurn == Player.WHITE) {
                gameStatus = GameStatus.WHITE_UNDER_CHECK;
            } else {
                gameStatus = GameStatus.BLACK_UNDER_CHECK;
            }
        } else if (!hasAnyValidMoves) {
            gameStatus = GameStatus.STALEMATE;
        } else {
            gameStatus = GameStatus.IN_PROGRESS;
        }

        // Note: Insufficient material can happen while a player is in check. Consider this scenario:
        // Board with two kings and a lone pawn. The pawn is promoted to a Knight with a check.
        // In this game, a player will be in check but the game also ends as insufficient material.
        // For this case, we just mark the game as insufficient material.
        // It might be better to use some sort of a "Flags" enum.
        // Or, alternatively, don't represent "check" in gameStatus
        // Instead, have a separate isWhiteInCheck/isBlackInCheck methods.
        if (isInsufficientMaterial()) {
            gameStatus = GameStatus.INSUFFICIENT_MATERIAL;
        }

    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public boolean isGameEnded() {
        return gameStatus == GameStatus.WHITE_WON
                || gameStatus == GameStatus.BLACK_WON
                || gameStatus == GameStatus.STALEMATE
                || gameStatus == GameStatus.INSUFFICIENT_MATERIAL;
    }

    private boolean isInsufficientMaterial() {
        /*
        If both sides have any one of the following, and there are no pawns on the board:

        A lone king
        a king and bishop
        a king and knight
         */
        int whiteBishopCount = 0;
        int blackBishopCount = 0;
        int whiteKnightCount = 0;
        int blackKnightCount = 0;

        for (BoardFile file : BoardFile.values()) {
            for (BoardRank rank : BoardRank.values()) {
                Piece p = getPieceAtSquare(new Square(file, rank));
                if (p == null || p instanceof King) {
                    continue;
                }

                if (p instanceof Bishop) {
                    if (p.getOwner() == Player.WHITE) {
                        whiteBishopCount++;
                    } else {
                        blackBishopCount++;
                    }
                } else if (p instanceof Knight) {
                    if (p.getOwner() == Player.WHITE) {
                        whiteKnightCount++;
                    } else {
                        blackKnightCount++;
                    }
                } else {
                    // There is a non-null piece that is not a King, Knight, or Bishop.
                    // This can't be insufficient material.
                    return false;
                }
            }
        }

        boolean insufficientForWhite = whiteKnightCount + whiteBishopCount <= 1;
        boolean insufficientForBlack = blackKnightCount + blackBishopCount <= 1;
        return insufficientForWhite && insufficientForBlack;
    }

    private boolean hasAnyValidMoves() {
        for (BoardFile file : BoardFile.values()) {
            for (BoardRank rank : BoardRank.values()) {
                if (!getAllValidMovesFromSquare(new Square(file, rank)).isEmpty()) {
                    return true;
                }
            }
        }

        return false;
    }

    public List<Square> getAllValidMovesFromSquare(Square square) {
        ArrayList<Square> validMoves = new ArrayList<>();
        for (var i : BoardFile.values()) {
            for (var j : BoardRank.values()) {
                var sq = new Square(i, j);
                if (isValidMove(new Move(square, sq, PawnPromotion.Queen))) {
                    validMoves.add(sq);
                }
            }
        }

        return validMoves;
    }

    public Piece getPieceAtSquare(Square square) {
        return board.getPieceAtSquare(square);
    }
    
//    public void resetGame(){
//        this.board = new ChessBoard(this.board);
//    }
}
