package ChessCore;

import ChessCore.Pieces.*;

public class moveReciver {

    private memento memento = new memento();
    private Move move;
    private Move lastMove;
    private ChessGame game;

    public void reciverUpdate(Move move, ChessGame game) {
        this.game = game;
        this.move = move;
    }
    
    public void moveUpdate(Move move, Move lastMove, ChessGame game) {
        this.game = game;
        this.lastMove = lastMove;
        this.move = move;
    }

    public boolean makeMove() {
        if (!game.isValidMove(move)) {
            return false;
        }

        Square fromSquare = move.getFromSquare();
        Piece fromPiece = game.getBoard().getPieceAtSquare(fromSquare);
        // If the king has moved, castle is not allowed.
        if (fromPiece instanceof King) {
            if (fromPiece.getOwner() == Player.WHITE) {
                game.setCanWhiteCastleKingSide(false);
                game.setCanWhiteCastleQueenSide(false);
            } else {
                game.setCanBlackCastleKingSide(false);
                game.setCanBlackCastleQueenSide(false);
            }
        }

        // If the rook has moved, castle is not allowed on that specific side..
        if (fromPiece instanceof Rook) {
            if (fromPiece.getOwner() == Player.WHITE) {
                if (fromSquare.getFile() == BoardFile.A && fromSquare.getRank() == BoardRank.FIRST) {
                    game.setCanWhiteCastleQueenSide(false);
                } else if (fromSquare.getFile() == BoardFile.H && fromSquare.getRank() == BoardRank.FIRST) {
                    game.setCanWhiteCastleKingSide(false);
                }
            } else {
                if (fromSquare.getFile() == BoardFile.A && fromSquare.getRank() == BoardRank.EIGHTH) {
                    game.setCanBlackCastleQueenSide(false);
                } else if (fromSquare.getFile() == BoardFile.H && fromSquare.getRank() == BoardRank.EIGHTH) {
                    game.setCanBlackCastleKingSide(false);
                }
            }
        }

        // En-passant.
        if (fromPiece instanceof Pawn
                && move.getAbsDeltaX() == 1
                && !game.hasPieceIn(move.getToSquare())) {
            game.getBoard().setPieceAtSquare(game.getLastMove().getToSquare(), null);
            game.setIsEnPassantMove(true);
        }

        // Promotion
        if (fromPiece instanceof Pawn) {
            BoardRank toSquareRank = move.getToSquare().getRank();
            if (toSquareRank == BoardRank.FIRST || toSquareRank == BoardRank.EIGHTH) {
                Player playerPromoting = toSquareRank == BoardRank.EIGHTH ? Player.WHITE : Player.BLACK;
                PawnPromotion promotion = move.getPawnPromotion();
                game.setPromotionMove(true);
                switch (promotion) {
                    case Queen:
                        fromPiece = game.getFactory().CreatPiece("Queen", playerPromoting);
                        break;
                    case Rook:
                        fromPiece = game.getFactory().CreatPiece("Rook", playerPromoting);
                        break;
                    case Knight:
                        fromPiece = game.getFactory().CreatPiece("Knight", playerPromoting);
                        break;
                    case Bishop:
                        fromPiece = game.getFactory().CreatPiece("Bishop", playerPromoting);
                        break;
                    case None:
                        throw new RuntimeException("Pawn moving to last rank without promotion being set. This should NEVER happen!");
                }
            }
        }

        // Castle
        if (fromPiece instanceof King
                && move.getAbsDeltaX() == 2) {

            Square toSquare = move.getToSquare();
            if (toSquare.getFile() == BoardFile.G && toSquare.getRank() == BoardRank.FIRST) {
                game.setCanWhiteCastleKingSide(true);
                Square h1 = new Square(BoardFile.H, BoardRank.FIRST);
                Square f1 = new Square(BoardFile.F, BoardRank.FIRST);
                Piece rook = game.getBoard().getPieceAtSquare(h1);
                game.getBoard().setPieceAtSquare(h1, null);
                game.getBoard().setPieceAtSquare(f1, rook);
            } else if (toSquare.getFile() == BoardFile.G && toSquare.getRank() == BoardRank.EIGHTH) {
                // Black king-side castle.
                game.setCanBlackCastleKingSide(true);
                Square h8 = new Square(BoardFile.H, BoardRank.EIGHTH);
                Square f8 = new Square(BoardFile.F, BoardRank.EIGHTH);
                Piece rook = game.getBoard().getPieceAtSquare(h8);
                game.getBoard().setPieceAtSquare(h8, null);
                game.getBoard().setPieceAtSquare(f8, rook);
            } else if (toSquare.getFile() == BoardFile.C && toSquare.getRank() == BoardRank.FIRST) {
                // White queen-side castle.
                game.setCanWhiteCastleQueenSide(true);
                Square a1 = new Square(BoardFile.A, BoardRank.FIRST);
                Square d1 = new Square(BoardFile.D, BoardRank.FIRST);
                Piece rook = game.getBoard().getPieceAtSquare(a1);
                game.getBoard().setPieceAtSquare(a1, null);
                game.getBoard().setPieceAtSquare(d1, rook);
            } else if (toSquare.getFile() == BoardFile.C && toSquare.getRank() == BoardRank.EIGHTH) {
                // Black queen-side castle.
                game.setCanBlackCastleQueenSide(true);
                Square a8 = new Square(BoardFile.A, BoardRank.EIGHTH);
                Square d8 = new Square(BoardFile.D, BoardRank.EIGHTH);
                Piece rook = game.getBoard().getPieceAtSquare(a8);
                game.getBoard().setPieceAtSquare(a8, null);
                game.getBoard().setPieceAtSquare(d8, rook);
            }
        }
        
        memento.mementoIni(game, move);
        memento.saveMove();
        
        //game.getStack().push(game.getBoard().getPieceAtSquare(move.getToSquare()));
        game.getBoard().setPieceAtSquare(fromSquare, null);
        game.getBoard().setPieceAtSquare(move.getToSquare(), fromPiece);
        game.setWhoseTurn(Utilities.revertPlayer(game.getWhoseTurn()));
        //game.setLastMove(move);
        game.updateGameStatus();
        return true;
    }

    public boolean undoMove() {
        Square fromSquare = move.getFromSquare();
        Square toSquare = move.getToSquare();
        Piece toPiece = game.getBoard().getPieceAtSquare(toSquare);
        Piece undoPiece = (Piece) game.getStack().pop();
        Square capturedPawnSqu;
        if (null != move.getMoveType()) {
            switch (move.getMoveType()) {
                case CANWHITECASTLEKINGSIDE -> {
                    game.setCanWhiteCastleKingSide(true);
                    Square h1 = new Square(BoardFile.H, BoardRank.FIRST);
                    Square f1 = new Square(BoardFile.F, BoardRank.FIRST);
                    Piece rook = game.getBoard().getPieceAtSquare(f1);
                    game.getBoard().setPieceAtSquare(h1, rook);
                    game.getBoard().setPieceAtSquare(f1, null);
                }
                case CANWHITECASTLEQUEENSIDE -> {
                    // White queen-side castle.
                    game.setCanWhiteCastleQueenSide(true);
                    Square a1 = new Square(BoardFile.A, BoardRank.FIRST);
                    Square d1 = new Square(BoardFile.D, BoardRank.FIRST);
                    Piece rook = game.getBoard().getPieceAtSquare(d1);
                    game.getBoard().setPieceAtSquare(a1, rook);
                    game.getBoard().setPieceAtSquare(d1, null);
                }
                case CANBLACKCASTLEKINGSIDE -> {
                    // Black king-side castle.
                    game.setCanBlackCastleKingSide(true);
                    Square h8 = new Square(BoardFile.H, BoardRank.EIGHTH);
                    Square f8 = new Square(BoardFile.F, BoardRank.EIGHTH);
                    Piece rook = game.getBoard().getPieceAtSquare(f8);
                    game.getBoard().setPieceAtSquare(h8, rook);
                    game.getBoard().setPieceAtSquare(f8, null);
                }
                case CANBLACKCASTLEQUEENSIDE -> {
                    // Black queen-side castle.
                    game.setCanBlackCastleQueenSide(true);
                    Square a8 = new Square(BoardFile.A, BoardRank.EIGHTH);
                    Square d8 = new Square(BoardFile.D, BoardRank.EIGHTH);
                    Piece rook = game.getBoard().getPieceAtSquare(d8);
                    game.getBoard().setPieceAtSquare(a8, rook);
                    game.getBoard().setPieceAtSquare(d8, null);
                }
                case ENPASSANT -> {
                    game.setIsEnPassantMove(false);
                    capturedPawnSqu = new Square(toSquare.getFile(), fromSquare.getRank());
                    game.getBoard().setPieceAtSquare(capturedPawnSqu, new Pawn(game.getWhoseTurn()));
                }
                case PROMOTION -> {
                    game.setWhoseTurn(Utilities.revertPlayer(game.getWhoseTurn()));
                    toPiece = game.getFactory().CreatPiece("Pawn", game.getWhoseTurn());
                    game.setWhoseTurn(Utilities.revertPlayer(game.getWhoseTurn()));
                }
            }
        }
        game.setWhoseTurn(Utilities.revertPlayer(game.getWhoseTurn()));
        game.getBoard().setPieceAtSquare(fromSquare, toPiece);
        game.getBoard().setPieceAtSquare(move.getToSquare(), undoPiece);
        
        this.lastMove = lastMove;
        game.updateGameStatus();
        
        game.getStack().push(undoPiece);
        return true;
    }
}
