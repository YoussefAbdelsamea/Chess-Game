package ChessCore;

import ChessCore.Pieces.*;

public class PiecesFactory {
    
    public static Piece CreatPiece(String name, Player alliance){
        
        if(name == null || alliance == null)
            return null;
        else
        {
            switch (name)
            {
                case "Rook" -> {
                    return new Rook(alliance);
                }  
                case "Knight" -> {
                    return new Knight(alliance);
                }
                case "Bishop" -> {
                    return new Bishop(alliance);
                }
                case "Queen" -> {
                    return new Queen(alliance);
                }
                case "King" -> {
                    return new King(alliance);
                }
                case "Pawn" -> {
                    return new Pawn(alliance);
                }
                default ->{
                    
                }
            }    
        }
        return null;
    }
}
