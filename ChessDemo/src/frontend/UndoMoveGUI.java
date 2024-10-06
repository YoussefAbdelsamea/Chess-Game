package frontend;

import ChessCore.BoardFile;
import ChessCore.BoardRank;
import ChessCore.ClassicChessGame;
import ChessCore.GameStatus;
import ChessCore.Move;
import ChessCore.MoveType;
import ChessCore.PawnPromotion;
import ChessCore.Pieces.Piece;
import ChessCore.Player;
import java.util.Stack;

public class UndoMoveGUI {

    private Stack<Move> stack = new Stack<>();
    private String[][] names;
    ClassicChessGame game = ClassicChessGame.getInstance();
    int[] kingsPos = new int[4];

    public UndoMoveGUI(String[][] names) {
        this.names = names;
    }

    public Stack<Move> getStack() {
        return stack;
    }

    public String[][] getNames() {
        return names;
    }

    public void SaveMove(Move move) {
        stack.push(move);
    }

    public void pushMoveWhiteCastleKingSide() {
        Move move = (Move) stack.pop();
        Move moveModified = new Move(move.getFromSquare(), move.getToSquare(), PawnPromotion.None, MoveType.CANWHITECASTLEKINGSIDE);
        stack.push(moveModified);
    }

    public void pushMoveWhiteCastleQueenSide() {
        Move move = (Move) stack.pop();
        Move moveModified = new Move(move.getFromSquare(), move.getToSquare(), PawnPromotion.None, MoveType.CANWHITECASTLEQUEENSIDE);
        stack.push(moveModified);
    }

    public void pushMoveBlackCastleKingSide() {
        Move move = (Move) stack.pop();
        Move moveModified = new Move(move.getFromSquare(), move.getToSquare(), PawnPromotion.None, MoveType.CANBLACKCASTLEKINGSIDE);
        stack.push(moveModified);
    }

    public void pushMoveBlackCastleQueenSide() {
        Move move = (Move) stack.pop();
        Move moveModified = new Move(move.getFromSquare(), move.getToSquare(), PawnPromotion.None, MoveType.CANBLACKCASTLEQUEENSIDE);
        stack.push(moveModified);
    }

    public void pushMoveEnPassant() {
        Move move = (Move) stack.pop();
        Move moveModified = new Move(move.getFromSquare(), move.getToSquare(), PawnPromotion.None, MoveType.ENPASSANT);
        stack.push(moveModified);
    }

    private int Rankreseter(BoardRank I) {
        switch (I) {
            case FIRST -> {
                return 7;
            }
            case SECOND -> {
                return 6;
            }
            case THIRD -> {
                return 5;
            }
            case FORTH -> {
                return 4;
            }
            case FIFTH -> {
                return 3;
            }
            case SIXTH -> {
                return 2;
            }
            case SEVENTH -> {
                return 1;
            }
            case EIGHTH -> {
                return 0;
            }
            default ->
                throw new AssertionError();
        }

    }

    private int Filereseter(BoardFile i) {
        switch (i) {
            case A -> {
                return 0;
            }
            case B -> {
                return 1;
            }
            case C -> {
                return 2;
            }
            case D -> {
                return 3;
            }
            case E -> {
                return 4;
            }
            case F -> {
                return 5;
            }
            case G -> {
                return 6;
            }
            case H -> {
                return 7;
            }
            default ->
                throw new AssertionError();
        }

    }

    public int[] undoMove(int whiteKingPosCol, int whiteKingPosRow, int blackKingPosCol, int blackKingPosRow) {
        Move move = (Move) stack.pop();
        Move lastMove = null;
        if (!stack.isEmpty()) {
            lastMove = (Move) stack.pop();
            stack.push(lastMove);
        }
        Piece toPiece;
        String toPieceName = null;
        String player;
        boolean promotion = false;
        toPiece = game.undoMove(move, lastMove);
        if (game.getWhoseTurn() != Player.WHITE) {
            player = "White";
        } else {
            player = "Black";
        }
        if (null != move.getMoveType()) {
            switch (move.getMoveType()) {
                case CANWHITECASTLEKINGSIDE -> {
                    names[7][5] = "empty,empty";
                    names[7][7] = "White,ROOK";
                }
                case CANWHITECASTLEQUEENSIDE -> {
                    names[7][3] = "empty,empty";
                    names[7][0] = "White,ROOK";
                }
                case CANBLACKCASTLEKINGSIDE -> {
                    names[0][5] = "empty,empty";
                    names[0][7] = "Black,ROOK";
                }
                case CANBLACKCASTLEQUEENSIDE -> {
                    names[0][3] = "empty,empty";
                    names[0][0] = "Black,ROOK";
                }

                case ENPASSANT -> {
                    names[Rankreseter(move.getFromSquare().getRank())][Filereseter(move.getToSquare().getFile())] = player + ",Pawn";
                }
                case PROMOTION -> {
                    if ("White".equals(player)) {
                        names[Rankreseter(move.getFromSquare().getRank())][Filereseter(move.getFromSquare().getFile())] = "Black,Pawn";
                    } else {
                        names[Rankreseter(move.getFromSquare().getRank())][Filereseter(move.getFromSquare().getFile())] = "White,Pawn";
                    }
                    promotion = true;
                }
            }
        }
        if (toPiece == null) {
            toPieceName = "empty";
            player = "empty";
        } else {
            toPieceName = toPiece.toString();
        }

        int colFrom = Rankreseter(move.getFromSquare().getRank());
        int rowfrom = Filereseter(move.getFromSquare().getFile());
        int colTo = Rankreseter(move.getToSquare().getRank());
        int rowTo = Filereseter(move.getToSquare().getFile());
        if (!promotion) {
            names[colFrom][rowfrom] = names[colTo][rowTo];
        }
        String[] str = new String[3];
        str = names[colFrom][rowfrom].split(",");
        if ("King".equals(str[1])) {
            game.setCanBlackCastleKingSide(true);
            game.setCanWhiteCastleKingSide(true);
            game.setCanBlackCastleQueenSide(true);
            game.setCanWhiteCastleQueenSide(true);
            if ("White".equals(str[0])) {
                whiteKingPosCol = colFrom;
                whiteKingPosRow = rowfrom;

            } else if ("Black".equals(str[0])) {
                blackKingPosCol = colFrom;
                blackKingPosRow = rowfrom;
            }
        }
        kingsPos[0] = whiteKingPosCol;
        kingsPos[1] = whiteKingPosRow;
        kingsPos[2] = blackKingPosCol;
        kingsPos[3] = blackKingPosRow;
        names[colTo][rowTo] = player + "," + toPieceName;
        if (game.getGameStatus() == GameStatus.BLACK_UNDER_CHECK) {
            names[blackKingPosCol][blackKingPosRow] = "Black,King,Red";

        } else if (game.getGameStatus() != GameStatus.BLACK_UNDER_CHECK) {
            names[blackKingPosCol][blackKingPosRow] = "Black,King,Normal";
        }
        if (game.getGameStatus() == GameStatus.WHITE_UNDER_CHECK) {
            names[whiteKingPosCol][whiteKingPosRow] = "White,King,Red";

        } else if (game.getGameStatus() != GameStatus.WHITE_UNDER_CHECK) {
            names[whiteKingPosCol][whiteKingPosRow] = "White,King,Normal";
        }
        return kingsPos;
    }

}
