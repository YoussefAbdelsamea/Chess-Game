package frontend;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import ChessCore.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import java.awt.event.*;
import javax.swing.JMenuBar;
import javax.swing.SwingConstants;

public class ChessGUI {

    //private memento memento = new memento();
    public static JLabel[][] chessBoardSquares = new JLabel[8][8];
    private static String[][] names = new String[8][8];
    private JPanel chessBoard;
    private static boolean firstClk = true;
    private static int row1;
    private static int col1;
    private static String[] whiteTurn;
    private static String[] case1 = new String[2];
    private static String[] case2 = new String[2];
    private static int whiteKingPosRow = 4;
    private static int whiteKingPosCol = 7;
    private static int blackKingPosRow = 4;
    private static int blackKingPosCol = 0;
    private static Color c = new Color(227, 198, 181);
    private static Color[] c1 = new Color[40];
    private static UndoMoveGUI undoMove = new UndoMoveGUI(names);
    List<Square> allValidMoves = new ArrayList<>();
    ClassicChessGame game = ClassicChessGame.getInstance();
    JFrame frame = new JFrame("Chess Game");

    public static JLabel[][] getChessBoardSquares() {
        return chessBoardSquares;
    }

    public ChessGUI() {

        ChessBoardIni();
        Dimension minimumSize = new Dimension(800, 800);
        frame.setMinimumSize(minimumSize);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(chessBoard);
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);
        addMouseListenerToLabels((int row, int col) -> {
//            JOptionPane.showMessageDialog(null, row + "''" + col);
            if (firstClk) {
                whiteTurn = names[col][row].split(",");
            } else {
                whiteTurn = names[col1][row1].split(",");
            }

            if (game.getGameStatus().equals(GameStatus.WHITE_WON)) {
            } else if (game.getGameStatus().equals(GameStatus.BLACK_WON)) {
            } else if (game.getGameStatus().equals(GameStatus.STALEMATE)) {
            } else if (!whiteTurn[0].equals("empty")) {
                if (Player.valueOf(whiteTurn[0].toUpperCase()).equals(game.getWhoseTurn())) {
                    case1 = names[col1][row1].split(",");
                    case2 = names[col][row].split(",");
                    if (case1[0] != null && case2[0] != null) {
                        if (case1[0].equals(case2[0])) {
                            firstClk = true;
                            chessBoardSquares[col1][row1].setBackground(c);
                            resetBackgroundColor(allValidMoves);
                        }
                    }
                    if (firstClk) {
                        chessBoardSquares[col1][row1].setBackground(c);
                        row1 = row;
                        col1 = col;
                        firstClk = false;
                        c = chessBoardSquares[col][row].getBackground();
                        allValidMoves = game.getAllValidMovesFromSquare(new Square(Fileconverter(row), Rankconverter(col)));
                        changeBackgroundColor(allValidMoves);
                        chessBoardSquares[col][row].setBackground(new Color(255, 255, 204));
                    } else if (!firstClk) {
//                        JOptionPane.showMessageDialog(null, names[col1][row1] + "----" + names[col][row] + "   firsclick is " + firstClk);
                        Square S1 = new Square(Fileconverter(row1), Rankconverter(col1));
                        Square S2 = new Square(Fileconverter(row), Rankconverter(col));
                        if (promotionMove(S1, S2, row1, col1, col, row)) {
                            firstClk = true;
                        } else {
                            Move move = new Move(S1, S2);
                            if (game.makeMove(move)) {
                                undoMove.SaveMove(move);
                                if (null != names[col1][row1]) {
                                    switch (names[col1][row1]) {
                                        case "White,King,Normal", "White,King,Red" -> {
                                            if (game.isCanWhiteCastleKingSide()) {
                                                undoMove.pushMoveWhiteCastleKingSide();
                                                names[col][row - 1] = names[col][row + 1];
                                                names[col][row + 1] = "empty,empty";
                                                game.setCanWhiteCastleKingSide(false);
                                                whiteKingPosRow = row;
                                                whiteKingPosCol = col;
                                            } else if (game.isCanWhiteCastleQueenSide()) {
                                                undoMove.pushMoveWhiteCastleQueenSide();
                                                names[col][row + 1] = names[col][row - 2];
                                                names[col][row - 2] = "empty,empty";
                                                game.setCanWhiteCastleQueenSide(false);
                                                whiteKingPosRow = row;
                                                whiteKingPosCol = col;
                                            } else {
                                                whiteKingPosRow = row;
                                                whiteKingPosCol = col;
                                            }
                                        }
                                        case "Black,King,Normal", "Black,King,Red" -> {
                                            if (game.isCanBlackCastleKingSide()) {
                                                undoMove.pushMoveBlackCastleKingSide();
                                                names[col][row - 1] = names[col][row + 1];
                                                names[col][row + 1] = "empty,empty";
                                                game.setCanBlackCastleKingSide(false);
                                                blackKingPosRow = row;
                                                blackKingPosCol = col;
                                            } else if (game.isCanBlackCastleQueenSide()) {
                                                undoMove.pushMoveBlackCastleQueenSide();
                                                names[col][row + 1] = names[col][row - 2];
                                                names[col][row - 2] = "empty,empty";
                                                game.setCanBlackCastleQueenSide(false);
                                                blackKingPosRow = row;
                                                blackKingPosCol = col;
                                            } else {
                                                blackKingPosRow = row;
                                                blackKingPosCol = col;
                                            }
                                        }
                                        case "White,Pawn" -> {
                                            if (game.isIsEnPassantMove()) {
                                                undoMove.pushMoveEnPassant();
                                                game.setIsEnPassantMove(false);
                                                names[col + 1][row] = "empty,empty";
                                            }
                                        }
                                        case "Black,Pawn" -> {
                                            if (game.isIsEnPassantMove()) {
                                                undoMove.pushMoveEnPassant();
                                                game.setIsEnPassantMove(false);
                                                names[col - 1][row] = "empty,empty";
                                            }
                                        }
                                    }
                                }
                                names[col][row] = names[col1][row1];
                                names[col1][row1] = "empty,empty";
                                chessBoardSquares[col1][row1].setBackground(c);
                                resetBackgroundColor(allValidMoves);
                                updatePieces();

                                firstClk = true;

                            } else {
                                JOptionPane.showMessageDialog(null, "NOT VALID MOVE!");
                                chessBoardSquares[col1][row1].setBackground(c);
                                resetBackgroundColor(allValidMoves);
                                firstClk = true;

                            }

                        }

                    }
                }
            }
            if (game.getGameStatus() == GameStatus.BLACK_UNDER_CHECK && firstClk) {
                names[blackKingPosCol][blackKingPosRow] = "Black,King,Red";
                updatePieces();
            } else if (game.getGameStatus() != GameStatus.BLACK_UNDER_CHECK && firstClk) {
                names[blackKingPosCol][blackKingPosRow] = "Black,King,Normal";
                updatePieces();
            }
            if (game.getGameStatus() == GameStatus.WHITE_UNDER_CHECK && firstClk) {
                names[whiteKingPosCol][whiteKingPosRow] = "White,King,Red";
                updatePieces();
            } else if (game.getGameStatus() != GameStatus.WHITE_UNDER_CHECK && firstClk) {
                names[whiteKingPosCol][whiteKingPosRow] = "White,King,Normal";
                updatePieces();
            }

            if (null != game.getGameStatus()) {
                switch (game.getGameStatus()) {
                    case WHITE_WON ->
                        JOptionPane.showMessageDialog(null, "WHITE WON!");
                    case BLACK_WON ->
                        JOptionPane.showMessageDialog(null, "BLACK WON!");
                    case STALEMATE ->
                        JOptionPane.showMessageDialog(null, "DRAW!");
                    default -> {
                    }
                }
            }
        });
    }

    private void addMouseListenerToLabels(ChessboardLabelClickListener listener) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                chessBoardSquares[i][j].addMouseListener(new SquareClickListener(i, j, listener));

            }
        }
    }

    private class SquareClickListener extends MouseAdapter {

        private final int row;
        private final int col;
        private final ChessboardLabelClickListener listener;

        public SquareClickListener(int row, int col, ChessboardLabelClickListener listener) {
            this.row = col;
            this.col = row;
            this.listener = listener;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            listener.onLabelClick(row, col);
        }
    }

    @FunctionalInterface
    public interface ChessboardLabelClickListener {

        void onLabelClick(int row, int col);
    }

    public static String[][] getNames() {
        return names;
    }

    private void UndoMove() {
        int[] kingsPos = undoMove.undoMove(whiteKingPosCol, whiteKingPosRow, blackKingPosCol, blackKingPosRow);
        whiteKingPosCol = kingsPos[0];
        whiteKingPosRow = kingsPos[1];
        blackKingPosCol = kingsPos[2];
        blackKingPosRow = kingsPos[3];
        names = undoMove.getNames();
        if (!firstClk) {
            firstClk = true;
        }
        chessBoardSquares[col1][row1].setBackground(c);
        resetBackgroundColor(allValidMoves);
        updatePieces();
    }

    private void changeBackgroundColor(List<Square> valid) {
        for (int i = 0; i < valid.size(); i++) {
            c1[i] = new Color(chessBoardSquares[Rankreseter(valid.get(i).getRank())][Filereseter(valid.get(i).getFile())].getBackground().getRGB());
            chessBoardSquares[Rankreseter(valid.get(i).getRank())][Filereseter(valid.get(i).getFile())].setBackground(new Color(153, 255, 153));
        }
    }

    private void resetBackgroundColor(List<Square> valid) {

        for (int i = 0; i < valid.size(); i++) {
            chessBoardSquares[Rankreseter(valid.get(i).getRank())][Filereseter(valid.get(i).getFile())].setBackground(c1[i]);
        }
    }

    private void promotionFrameInit(JButton buttonQueen, JButton buttonKnight, JButton buttonRook, JButton buttonBishop, JFrame frame1) {
        JPanel panel = new JPanel(new GridLayout(2, 1));
        JLabel label = new JLabel("Choose promotion");
        Font font = new Font("Serif", Font.PLAIN, 40);
        Font font1 = new Font("Serif", Font.PLAIN, 20);
        label.setFont(font1);
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4));
        buttonQueen.setBackground(new Color(112, 128, 144));
        buttonKnight.setBackground(new Color(112, 128, 144));
        buttonRook.setBackground(new Color(112, 128, 144));
        buttonBishop.setBackground(new Color(112, 128, 144));
        buttonQueen.setFont(font);
        buttonBishop.setFont(font);
        buttonKnight.setFont(font);
        buttonRook.setFont(font);
        Dimension minimumSize = new Dimension(300, 150);
        frame1.setMinimumSize(minimumSize);
        frame1.setLocationRelativeTo(null);
        frame1.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setVerticalAlignment(JLabel.CENTER);
        panel.add(label);

        buttonQueen.setHorizontalTextPosition(SwingConstants.CENTER);
        buttonBishop.setHorizontalTextPosition(SwingConstants.CENTER);
        buttonRook.setHorizontalTextPosition(SwingConstants.CENTER);
        buttonKnight.setHorizontalTextPosition(SwingConstants.CENTER);
        buttonPanel.add(buttonQueen);
        buttonPanel.add(buttonKnight);
        buttonPanel.add(buttonRook);
        buttonPanel.add(buttonBishop);
        Color backgroundColor = new Color(0xECF4D6);
        panel.add(buttonPanel);
        panel.setBackground(backgroundColor);
        frame1.add(panel);
        frame1.pack();
        frame1.setResizable(false);
        frame1.setVisible(true);

    }

    private boolean promotionMove(Square S1, Square S2, int row1, int col1, int col, int row) {
        if (col == 0 && "White,Pawn".equals(names[col1][row1])) {
            for (int i = 0; i < game.getAllValidMovesFromSquare(S1).size(); i++) {
                if (S2.getRank() == game.getAllValidMovesFromSquare(S1).get(i).getRank()) {
                    JFrame frame1 = new JFrame();
                    JButton buttonQueen = new JButton("\u2655");
                    JButton buttonKnight = new JButton("\u2658");
                    JButton buttonRook = new JButton("\u2656");
                    JButton buttonBishop = new JButton("\u2657");
                    promotionFrameInit(buttonQueen, buttonKnight, buttonRook, buttonBishop, frame1);
                    buttonQueen.addActionListener((ActionEvent e) -> {
                        if (game.makeMove(new ChessCore.Move(S1, S2, PawnPromotion.Queen))) {
                            undoMove.SaveMove(new ChessCore.Move(S1, S2, PawnPromotion.Queen));
                            names[col][row] = "White,Queen";
                            names[col1][row1] = "empty,empty";
                            chessBoardSquares[col1][row1].setBackground(c);
                            resetBackgroundColor(allValidMoves);
                            updatePieces();
                            frame1.setVisible(false);

                        }
                    });
                    buttonKnight.addActionListener((ActionEvent e) -> {
                        if (game.makeMove(new ChessCore.Move(S1, S2, PawnPromotion.Knight))) {
                            undoMove.SaveMove(new ChessCore.Move(S1, S2, PawnPromotion.Knight));
                            names[col][row] = "White,Knight";
                            names[col1][row1] = "empty,empty";
                            chessBoardSquares[col1][row1].setBackground(c);
                            resetBackgroundColor(allValidMoves);
                            updatePieces();
                            frame1.setVisible(false);

                        }
                    });
                    buttonRook.addActionListener((ActionEvent e) -> {
                        if (game.makeMove(new ChessCore.Move(S1, S2, PawnPromotion.Rook))) {
                            undoMove.SaveMove(new ChessCore.Move(S1, S2, PawnPromotion.Rook));
                            names[col][row] = "White,ROOK";
                            names[col1][row1] = "empty,empty";
                            chessBoardSquares[col1][row1].setBackground(c);
                            resetBackgroundColor(allValidMoves);
                            updatePieces();
                            frame1.setVisible(false);

                        }
                    });
                    buttonBishop.addActionListener((ActionEvent e) -> {
                        if (game.makeMove(new ChessCore.Move(S1, S2, PawnPromotion.Bishop))) {
                            undoMove.SaveMove(new ChessCore.Move(S1, S2, PawnPromotion.Bishop));
                            names[col][row] = "White,Bishop";
                            names[col1][row1] = "empty,empty";
                            chessBoardSquares[col1][row1].setBackground(c);
                            resetBackgroundColor(allValidMoves);
                            updatePieces();
                            frame1.setVisible(false);

                        }
                    });
                    return true;
                }
            }
        } else if (col == 7 && "Black,Pawn".equals(names[col1][row1])) {
            for (int i = 0; i < game.getAllValidMovesFromSquare(S1).size(); i++) {
                if (S2.getRank() == game.getAllValidMovesFromSquare(S1).get(i).getRank()) {
                    JFrame frame1 = new JFrame();
                    JButton buttonQueen = new JButton("\u265B");
                    JButton buttonKnight = new JButton("\u265E");
                    JButton buttonRook = new JButton("\u265C");
                    JButton buttonBishop = new JButton("\u265D");
                    promotionFrameInit(buttonQueen, buttonKnight, buttonRook, buttonBishop, frame1);

                    buttonQueen.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            if (game.makeMove(new ChessCore.Move(S1, S2, PawnPromotion.Queen))) {
                                undoMove.SaveMove(new ChessCore.Move(S1, S2, PawnPromotion.Queen));
                                names[col][row] = "Black,Queen";
                                names[col1][row1] = "empty,empty";
                                chessBoardSquares[col1][row1].setBackground(c);
                                resetBackgroundColor(allValidMoves);
                                updatePieces();
                                frame1.setVisible(false);

                            }
                        }
                    });
                    buttonKnight.addActionListener((ActionEvent e) -> {
                        if (game.makeMove(new ChessCore.Move(S1, S2, PawnPromotion.Knight))) {
                            undoMove.SaveMove(new ChessCore.Move(S1, S2, PawnPromotion.Knight));
                            names[col][row] = "Black,Knight";
                            names[col1][row1] = "empty,empty";
                            chessBoardSquares[col1][row1].setBackground(c);
                            resetBackgroundColor(allValidMoves);
                            updatePieces();
                            frame1.setVisible(false);
                        }
                    });
                    buttonRook.addActionListener((ActionEvent e) -> {
                        if (game.makeMove(new ChessCore.Move(S1, S2, PawnPromotion.Rook))) {
                            undoMove.SaveMove(new ChessCore.Move(S1, S2, PawnPromotion.Rook));
                            names[col][row] = "Black,ROOK";
                            names[col1][row1] = "empty,empty";
                            chessBoardSquares[col1][row1].setBackground(c);
                            resetBackgroundColor(allValidMoves);
                            updatePieces();
                            frame1.setVisible(false);

                        }
                    });
                    buttonBishop.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            if (game.makeMove(new ChessCore.Move(S1, S2, PawnPromotion.Bishop))) {
                                undoMove.SaveMove(new ChessCore.Move(S1, S2, PawnPromotion.Bishop));
                                names[col][row] = "Black,Bishop";
                                names[col1][row1] = "empty,empty";
                                chessBoardSquares[col1][row1].setBackground(c);
                                resetBackgroundColor(allValidMoves);
                                updatePieces();
                                frame1.setVisible(false);

                            }
                        }
                    });
                    return true;
                }
            }
        }
        return false;
    }

    private BoardRank Rankconverter(int i) {
        switch (i) {
            case 7 -> {
                return BoardRank.FIRST;
            }
            case 6 -> {
                return BoardRank.SECOND;
            }
            case 5 -> {
                return BoardRank.THIRD;
            }
            case 4 -> {
                return BoardRank.FORTH;
            }
            case 3 -> {
                return BoardRank.FIFTH;
            }
            case 2 -> {
                return BoardRank.SIXTH;
            }
            case 1 -> {
                return BoardRank.SEVENTH;
            }
            case 0 -> {
                return BoardRank.EIGHTH;
            }
            default ->
                throw new AssertionError();
        }

    }

    private BoardFile Fileconverter(int i) {
        switch (i) {
            case 0 -> {
                return BoardFile.A;
            }
            case 1 -> {
                return BoardFile.B;
            }
            case 2 -> {
                return BoardFile.C;
            }
            case 3 -> {
                return BoardFile.D;
            }
            case 4 -> {
                return BoardFile.E;
            }
            case 5 -> {
                return BoardFile.F;
            }
            case 6 -> {
                return BoardFile.G;
            }
            case 7 -> {
                return BoardFile.H;
            }
            default ->
                throw new AssertionError();
        }

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

    private void ChessBoardIni() {
        chessBoard = new JPanel(new GridLayout(8, 8));
        Font font = new Font("Serif", Font.PLAIN, 70);
        JMenuBar menu = new JMenuBar();
        JButton Undo = new JButton("Undo");
        //JButton reset = new JButton("Reset");
        //JButton flip = new JButton("Flip");
        menu.add(Undo);
        //menu.add(reset);
        //menu.add(flip);
        frame.add(menu);
        frame.setJMenuBar(menu);
        //flip.setBackground(new Color(227, 198, 181));
        Undo.setBackground(new Color(227, 198, 181));
        //reset.setBackground(new Color(227, 198, 181));
        menu.setBackground(new Color(157, 105, 53));
        Undo.addActionListener((ActionEvent e) -> {
            if (!undoMove.getStack().isEmpty()) {
                UndoMove();
            }
        });
        //flip.addActionListener((ActionEvent e) -> {
        //    flipBoard();
        //});
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                chessBoardSquares[i][j] = new JLabel();
                chessBoardSquares[i][j].setOpaque(true);
                chessBoardSquares[i][j].setFont(font);
                if ((i + j) % 2 == 0) {
                    chessBoardSquares[i][j].setBackground(new Color(227, 198, 181));
                } else {
                    chessBoardSquares[i][j].setBackground(new Color(157, 105, 53));
                }

                switch (i) {
                    case 0 -> {
                        switch (j) {
                            case 0 -> {
                                chessBoardSquares[i][j].setText("\u265C");
                                names[i][j] = "Black,ROOK";
                            }
                            case 1 -> {
                                chessBoardSquares[i][j].setText("\u265E");
                                names[i][j] = "Black,Knight";
                            }
                            case 2 -> {
                                chessBoardSquares[i][j].setText("\u265D");
                                names[i][j] = "Black,Bishop";
                            }
                            case 3 -> {
                                chessBoardSquares[i][j].setText("\u265B");
                                names[i][j] = "Black,Queen";
                            }
                            case 4 -> {
                                chessBoardSquares[i][j].setText("\u265A");
                                names[i][j] = "Black,King,Normal";
                            }
                            case 5 -> {
                                chessBoardSquares[i][j].setText("\u265D");
                                names[i][j] = "Black,Bishop";
                            }
                            case 6 -> {
                                chessBoardSquares[i][j].setText("\u265E");
                                names[i][j] = "Black,Knight";
                            }
                            case 7 -> {
                                chessBoardSquares[i][j].setText("\u265C");
                                names[i][j] = "Black,ROOK";
                            }
                        }
                    }

                    case 1 -> {
                        chessBoardSquares[i][j].setText("\u265F");
                        names[i][j] = "Black,Pawn";
                    }
                    case 6 -> {
                        chessBoardSquares[i][j].setText("\u2659");
                        names[i][j] = "White,Pawn";
                    }
                    case 7 -> {
                        switch (j) {
                            case 0:
                                chessBoardSquares[i][j].setText("\u2656");
                                names[i][j] = "White,ROOK";
                                break;
                            case 1:
                                chessBoardSquares[i][j].setText("\u2658");
                                names[i][j] = "White,Knight";
                                break;
                            case 2:
                                chessBoardSquares[i][j].setText("\u2657");
                                names[i][j] = "White,Bishop";
                                break;
                            case 3:
                                chessBoardSquares[i][j].setText("\u2655");
                                names[i][j] = "White,Queen";
                                break;
                            case 4:
                                chessBoardSquares[i][j].setText("\u2654");
                                names[i][j] = "White,King,Normal";
                                break;
                            case 5:
                                chessBoardSquares[i][j].setText("\u2657");
                                names[i][j] = "White,Bishop";
                                break;
                            case 6:
                                chessBoardSquares[i][j].setText("\u2658");
                                names[i][j] = "White,Knight";
                                break;
                            case 7:
                                chessBoardSquares[i][j].setText("\u2656");
                                names[i][j] = "White,ROOK";
                                break;
                        }
                    }
                    default -> {
                        chessBoardSquares[i][j].setText("");
                        names[i][j] = "empty,empty";
                    }
                }

                chessBoard.add(chessBoardSquares[i][j]);
            }
        }
    }

    private void updatePieces() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                String[] moves = names[i][j].split(",");
                if ((i + j) % 2 == 0) {
                    chessBoardSquares[i][j].setBackground(new Color(227, 198, 181));
                } else {
                    chessBoardSquares[i][j].setBackground(new Color(157, 105, 53));
                }
                switch (moves[0]) {
                    case "Black" -> {
                        switch (moves[1]) {
                            case "King" -> {
                                if (moves[2].equals("Red")) {
                                    chessBoardSquares[i][j].setBackground(new Color(178, 34, 34));
                                }
                                chessBoardSquares[i][j].setText("\u265A ");
                            }
                            case "Queen" ->
                                chessBoardSquares[i][j].setText("\u265B ");
                            case "ROOK" ->
                                chessBoardSquares[i][j].setText("\u265C ");
                            case "Bishop" ->
                                chessBoardSquares[i][j].setText("\u265D ");
                            case "Knight" ->
                                chessBoardSquares[i][j].setText("\u265E ");
                            case "Pawn" ->
                                chessBoardSquares[i][j].setText("\u265F ");
                            default -> {
                            }
                        }
                    }
                    case "White" -> {
                        switch (moves[1]) {
                            case "King" -> {
                                if (moves[2].equals("Red")) {
                                    chessBoardSquares[i][j].setBackground(new Color(178, 34, 34));
                                }
                                chessBoardSquares[i][j].setText("\u2654 ");
                            }
                            case "Queen" ->
                                chessBoardSquares[i][j].setText("\u2655 ");
                            case "ROOK" ->
                                chessBoardSquares[i][j].setText("\u2656 ");
                            case "Bishop" ->
                                chessBoardSquares[i][j].setText("\u2657 ");
                            case "Knight" ->
                                chessBoardSquares[i][j].setText("\u2658 ");
                            case "Pawn" ->
                                chessBoardSquares[i][j].setText("\u2659 ");
                            default -> {
                            }
                        }
                    }
                    default ->
                        chessBoardSquares[i][j].setText("");
                }
            }
        }
    }

    public static void main(String[] args) {
        ChessGUI GUI = new ChessGUI();
    }

//    public void flipBoard() {
//        String[] str = new String[3];
//        String[][] z = new String[8][8];
//        for (int i = 0; i < 8; i++) {
//            for (int j = 0; j < 8; j++) {
//                str = names[i][j].split(",");
//                if ("White".equals(str[0])) {
//                    if (z[7 - i][j] != null) {
//
//                        names[i][j] = z[7 - i][j];
//                    }
////                    System.out.println(i + "," + j);
//
//                } else if ("Black".equals(str[0])) {
//                    z[i][j] = names[i][j];
//                    names[i][j] = names[7 - i][j];
//
//                }
//            }
//        }
//        updatePieces();
//    }
}
