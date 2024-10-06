package ChessCore;

import ChessCore.Pieces.*;

public final class ClassicBoardInitializer implements BoardInitializer {
    private static final BoardInitializer instance = new ClassicBoardInitializer();

    private PiecesFactory Factory;
    
    private ClassicBoardInitializer() {
    }

    public static BoardInitializer getInstance() {
        return instance;
    }

    @Override
    public Piece[][] initialize() {
        Piece[][] initialState = {
                        
            {Factory.CreatPiece("Rook", Player.WHITE),Factory.CreatPiece("Knight", Player.WHITE),Factory.CreatPiece("Bishop", Player.WHITE), Factory.CreatPiece("Queen", Player.WHITE), Factory.CreatPiece("King", Player.WHITE), Factory.CreatPiece("Bishop", Player.WHITE),Factory.CreatPiece("Knight", Player.WHITE), Factory.CreatPiece("Rook", Player.WHITE)},
            {Factory.CreatPiece("Pawn", Player.WHITE), Factory.CreatPiece("Pawn", Player.WHITE), Factory.CreatPiece("Pawn", Player.WHITE), Factory.CreatPiece("Pawn", Player.WHITE), Factory.CreatPiece("Pawn", Player.WHITE), Factory.CreatPiece("Pawn", Player.WHITE), Factory.CreatPiece("Pawn", Player.WHITE), Factory.CreatPiece("Pawn", Player.WHITE)},
            {null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null},
            {Factory.CreatPiece("Pawn", Player.BLACK), Factory.CreatPiece("Pawn", Player.BLACK), Factory.CreatPiece("Pawn", Player.BLACK), Factory.CreatPiece("Pawn", Player.BLACK), Factory.CreatPiece("Pawn", Player.BLACK), Factory.CreatPiece("Pawn", Player.BLACK), Factory.CreatPiece("Pawn", Player.BLACK), Factory.CreatPiece("Pawn", Player.BLACK)},
            {Factory.CreatPiece("Rook", Player.BLACK),Factory.CreatPiece("Knight", Player.BLACK),Factory.CreatPiece("Bishop", Player.BLACK), Factory.CreatPiece("Queen", Player.BLACK), Factory.CreatPiece("King", Player.BLACK), Factory.CreatPiece("Bishop", Player.BLACK),Factory.CreatPiece("Knight", Player.BLACK), Factory.CreatPiece("Rook", Player.BLACK)},
            
            
//            {new Rook(Player.WHITE), new Knight(Player.WHITE), new Bishop(Player.WHITE), new Queen(Player.WHITE), new King(Player.WHITE), new Bishop(Player.WHITE), new Knight(Player.WHITE), new Rook(Player.WHITE)},
//            {new Pawn(Player.WHITE), new Pawn(Player.WHITE), new Pawn(Player.WHITE), new Pawn(Player.WHITE), new Pawn(Player.WHITE), new Pawn(Player.WHITE), new Pawn(Player.WHITE), new Pawn(Player.WHITE)},
//            {null, null, null, null, null, null, null, null},
//            {null, null, null, null, null, null, null, null},
//            {null, null, null, null, null, null, null, null},
//            {null, null, null, null, null, null, null, null},
//            {new Pawn(Player.BLACK), new Pawn(Player.BLACK), new Pawn(Player.BLACK), new Pawn(Player.BLACK), new Pawn(Player.BLACK), new Pawn(Player.BLACK), new Pawn(Player.BLACK), new Pawn(Player.BLACK)},
//            {new Rook(Player.BLACK), new Knight(Player.BLACK), new Bishop(Player.BLACK), new Queen(Player.BLACK), new King(Player.BLACK), new Bishop(Player.BLACK), new Knight(Player.BLACK), new Rook(Player.BLACK)}
        };
        return initialState;
    }
}
