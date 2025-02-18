package edu.iastate.cs472.proj2;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @ Author Jack Krause
 */

/**
 * An object of this class holds data about a game of checkers.
 * It knows what kind of piece is on each square of the checkerboard.
 * Note that RED moves "up" the board (i.e. row number decreases)
 * while BLACK moves "down" the board (i.e. row number increases).
 * Methods are provided to return lists of available legal moves.
 */
public class CheckersData {

  /*  The following constants represent the possible contents of a square
      on the board.  The constants RED and BLACK also represent players
      in the game. */
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    static final int
            EMPTY = 0,
            RED = 1,
            RED_KING = 2,
            BLACK = 3,
            BLACK_KING = 4;
    int[][] board;  // board[r][c] is the contents of row r, column c.
    /**
     * Constructor.  Create the board and set it up for a new game.
     */
    CheckersData() {
        board = new int[8][8];
        setUpGame();
    }

    @Override
    public CheckersData clone() {
        CheckersData copy = new CheckersData();
        for (int i = 0; i < board.length; i++) {
            System.arraycopy(this.board[i], 0, copy.board[i], 0, board[i].length);
        }
        return copy;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < board.length; i++) {
            int[] row = board[i];
            sb.append(8 - i).append(" ");
            for (int n : row) {
                if (n == 0) {
                    sb.append(" ");
                } else if (n == 1) {
                    sb.append(ANSI_RED + "R" + ANSI_RESET);
                } else if (n == 2) {
                    sb.append(ANSI_RED + "K" + ANSI_RESET);
                } else if (n == 3) {
                    sb.append(ANSI_YELLOW + "B" + ANSI_RESET);
                } else if (n == 4) {
                    sb.append(ANSI_YELLOW + "K" + ANSI_RESET);
                }
                sb.append(" ");
            }
            sb.append(System.lineSeparator());
        }
        //sb.append("  a b c d e f g h");
        sb.append("  1 2 3 4 5 6 7 8");

        return sb.toString();
    }

    public boolean gameOver() {
        CheckersMove[] blackMoves = this.getLegalMoves(BLACK);
        CheckersMove[] redMoves = this.getLegalMoves(RED);

        return (blackMoves == null || blackMoves.length == 0) || (redMoves == null || redMoves.length == 0);
    }

    /**
     * Set up the board with checkers in position for the beginning
     * of a game.  Note that checkers can only be found in squares
     * that satisfy  row % 2 == col % 2.  At the start of the game,
     * all such squares in the first three rows contain black squares
     * and all such squares in the last three rows contain red squares.
     */
    void setUpGame() {
        // Set up the board with pieces BLACK, RED, and EMPTY
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 8; c++) {
                if ((r % 2) == (c % 2)) {
                    this.board[r][c] = BLACK;
                }
            }
        }

        for (int r = 5; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if ((r % 2) == (c % 2)) {
                    this.board[r][c] = RED;
                }
            }
        }
    }



    /**
     * Return the contents of the square in the specified row and column.
     */
    int pieceAt(int row, int col) {
        if (row >= 8 || row < 0 || col >= 8 || col < 0) {
            return -1;
        }
        return board[row][col];
    }


    /**
     * Make the specified move.  It is assumed that move
     * is non-null and that the move it represents is legal.
     * <p>
     * Make a single move or a sequence of jumps
     * recorded in rows and cols.
     */
    void makeMove(CheckersMove move) {
        int l = move.rows.size();

        for (int i = 0; i < l - 1; i++) {
            int fromRow = move.rows.get(i);
            int fromCol = move.cols.get(i);
            int toRow = move.rows.get(i + 1);
            int toCol = move.cols.get(i + 1);

            makeMove(fromRow, fromCol, toRow, toCol);
        }
    }

    int countPieces(int player) {
        int pieceCount = 0;

        for (int i = 0; i < 8; i++) {
            for (int k = 0; k < 8; k++) {
                int piece = pieceAt(i, k);
                if (isPlayerPiece(player, piece)) pieceCount++;
            }
        }

        return pieceCount;
    }

    /**
     * Make the move from (fromRow,fromCol) to (toRow,toCol).  It is
     * assumed that this move is legal.  If the move is a jump, the
     * jumped piece is removed from the board.  If a piece moves to
     * the last row on the opponent's side of the board, the
     * piece becomes a king.
     *
     * @param fromRow row index of the from square
     * @param fromCol column index of the from square
     * @param toRow   row index of the to square
     * @param toCol   column index of the to square
     */
    void makeMove(int fromRow, int fromCol, int toRow, int toCol) {
        // TODO
        //
        // Update the board for the given move. You need to take care of the following situations:
        // 1. move the piece from (fromRow,fromCol) to (toRow,toCol)
        int piece = this.board[fromRow][fromCol];
        this.board[toRow][toCol] = piece;
        this.board[fromRow][fromCol] = EMPTY;

        // 2. if this move is a jump, remove the captured piece
        if (isJump(fromRow, fromCol, toRow, toCol)) {
            // remove captured piece
            int removeRow = (fromRow + toRow) / 2;
            int removeCol = (fromCol + toCol) / 2;
            this.board[removeRow][removeCol] = EMPTY;
        }

        // 3. if the piece moves into the kings row on the opponent's side of the board, crowned it as a king
        if (toRow == 0 && piece == RED) {
            this.board[toRow][toCol] = RED_KING;
        } else if (toRow == 7 && piece == BLACK) {
            this.board[toRow][toCol] = BLACK_KING;
        }
    }

    /**
     * Return an array containing all the legal CheckersMoves
     * for the specified player on the current board.  If the player
     * has no legal moves, null is returned.  The value of player
     * should be one of the constants RED or BLACK; if not, null
     * is returned.  If the returned value is non-null, it consists
     * entirely of jump moves or entirely of regular moves, since
     * if the player can jump, only jumps are legal moves.
     *
     * @param player color of the player, RED or BLACK
     */
    CheckersMove[] getLegalMoves(int player) {
        ArrayList<CheckersMove> moves = new ArrayList<>();
        boolean jumpExists = false;

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                int piece = this.board[r][c];

                if (isPlayerPiece(player, piece)) {
                    CheckersMove[] jumps = getJumpsFrom(player, r, c);

                    if (jumps != null) {
                        moves.addAll(Arrays.asList(jumps));
                    }
                }
            }
        }

        if (!moves.isEmpty()) {
            return moves.toArray(new CheckersMove[moves.size()]);
        }

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                int piece = this.board[r][c];

                if (isPlayerPiece(player, piece)) {
                    CheckersMove[] normals = getNormalMovesFrom(player, r, c);

                    if (normals != null) {
                        moves.addAll(Arrays.asList(normals));
                    }
                }
            }
        }

        if (moves.isEmpty()) return null;
        return moves.toArray(new CheckersMove[moves.size()]);
    }


    /**
     * Return a list of the legal jumps that the specified player can
     * make starting from the specified row and column.  If no such
     * jumps are possible, null is returned.  The logic is similar
     * to the logic of the getLegalMoves() method.
     * <p>
     * Note that each CheckerMove may contain multiple jumps.
     * Each move returned in the array represents a sequence of jumps
     * until no further jump is allowed.
     *
     * @param player The player of the current jump, either RED or BLACK.
     * @param row    row index of the start square.
     * @param col    col index of the start square.
     */
    CheckersMove[] getJumpsFrom(int player, int row, int col) {
        int piece = pieceAt(row, col);
        if (piece == EMPTY || !isPlayerPiece(player, piece)) return null;

        ArrayList<CheckersMove> jumps = new ArrayList<>();
        int[][] directions;

        if (isKing(piece)) {
            directions = new int[][]{{-2, -2}, {-2, 2}, {2, -2}, {2, 2}};
        } else if (player == RED) {
            directions = new int[][]{{-2, -2}, {-2, 2}};
        } else {
            directions = new int[][]{{2, -2}, {2, 2}};
        }

        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            int jumpRow = row + dir[0] / 2;
            int jumpCol = col + dir[1] / 2;

            if (newRow < 0 || newRow >= 8 || newCol < 0 || newCol >= 8) continue;
            if (pieceAt(newRow, newCol) != EMPTY) continue;

            int jumpedPiece = pieceAt(jumpRow, jumpCol);
            if (isOpponent(player, jumpedPiece)) {
                jumps.add(new CheckersMove(row, col, newRow, newCol));

                int[][] boardBackup = copyBoard(this.board);
                this.board[row][col] = EMPTY;
                this.board[jumpRow][jumpCol] = EMPTY;
                this.board[newRow][newCol] = piece;

                CheckersMove[] otherJumps = getJumpsFrom(player, newRow, newCol);

                if (otherJumps!= null && otherJumps.length > 0) {
                    for (CheckersMove jump : otherJumps) {
                        CheckersMove move = new CheckersMove(row, col, newRow, newCol);
                        move.combine(jump);
                        jumps.add(move);
                    }
                } else {
                    jumps.add(new CheckersMove(row, col, newRow, newCol));
                }

                this.board = boardBackup;
            }
        }

        if (jumps.size() == 0) return null;
        return jumps.toArray(new CheckersMove[jumps.size()]);
    }

    private int[][] copyBoard(int[][] board) {
        int[][] copy = new int[8][8];
        for (int i = 0; i < 8; i++) {
            System.arraycopy(board[i], 0, copy[i], 0, 8);
        }
        return copy;
    }

    CheckersMove[] getNormalMovesFrom(int player, int row, int col) {
        int piece = pieceAt(row, col);
        if (piece == EMPTY || !isPlayerPiece(player, piece));

        ArrayList<CheckersMove> moves = new ArrayList<CheckersMove>();
        int[][] directions;

        if (isKing(piece)) {
            directions = new int[][] { {-1, -1}, {-1, 1}, {1, -1}, {1, 1} };
        } else if (player == RED) {
            directions = new int[][] { {-1, -1}, {-1, 1} };
        } else {
            directions = new int[][] { {1, -1}, {1, 1} };
        }

        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];

            if (newRow < 0 || newRow >= 8 || newCol < 0 || newCol >= 8) continue;
            if (pieceAt(newRow, newCol) != EMPTY) continue;

            moves.add(new CheckersMove(row, col, newRow, newCol));
        }

        if (moves.size() == 0) return null;
        return moves.toArray(new CheckersMove[moves.size()]);
    }

    boolean isJump(int fromRow, int fromCol, int toRow, int toCol) {
        return Math.abs(fromRow - toRow) == 2 && Math.abs(fromCol - toCol) == 2;
    }

    boolean isPlayerPiece(int player, int piece) {
        if (player == RED || player == RED_KING) return piece == RED || piece == RED_KING;
        else if (player == BLACK || player == BLACK_KING) return piece == BLACK || piece == BLACK_KING;
        else return false;
    }

    boolean isKing(int piece) {
        return piece == RED_KING || piece == BLACK_KING;
    }

    boolean isOpponent(int player, int piece) {
        if (player == RED || player == RED_KING) return piece == BLACK || piece == BLACK_KING;
        else if (player == BLACK || player == BLACK_KING) return piece == RED || piece == RED_KING;
        else return false;
    }

}
