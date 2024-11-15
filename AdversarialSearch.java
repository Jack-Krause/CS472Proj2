package edu.iastate.cs472.proj2;

/**
 * 
 * @author Jack Krause
 *
 */

/**
 * This class is to be extended by the class MonteCarloTreeSearch.
 */
public abstract class AdversarialSearch {
    protected CheckersData board;
    protected int side;

    // An instance of this class will be created in the Checkers.Board
    // It would be better to keep the default constructor.

    protected void setCheckersData(CheckersData board, int side) {
        this.board = board;
        this.side = side;
    }
    
    /** 
     * 
     * @return an array of valid moves
     */
    protected CheckersMove[] legalMoves() {
        return this.board.getLegalMoves(CheckersData.RED);
    }
	
    /**
     * Return a move returned from the Monte Carlo tree search.
     * 
     * @param legalMoves
     * @return CheckersMove 
     */
    public abstract CheckersMove makeMove(CheckersMove[] legalMoves);
}
