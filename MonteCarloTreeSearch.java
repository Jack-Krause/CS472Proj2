package edu.iastate.cs472.proj2;

/**
 * 
 * @author 
 *
 */

/**
 * This class implements the Monte Carlo tree search method to find the best
 * move at the current state.
 */
public class MonteCarloTreeSearch extends AdversarialSearch {

	/**
     * The input parameter legalMoves contains all the possible moves.
     * It contains four integers:  fromRow, fromCol, toRow, toCol
     * which represents a move from (fromRow, fromCol) to (toRow, toCol).
     * It also provides a utility method `isJump` to see whether this
     * move is a jump or a simple move.
     *
     * Each legalMove in the input now contains a single move
     * or a sequence of jumps: (rows[0], cols[0]) -> (rows[1], cols[1]) ->
     * (rows[2], cols[2]).
     *
     * @param legalMoves All the legal moves for the agent at current step.
     */
    public CheckersMove makeMove(CheckersMove[] legalMoves) {
        // The checker board state can be obtained from this.board,
        // which is an 2D array of the following integers defined below:
    	// 
        // 0 - empty square,
        // 1 - red man
        // 2 - red king
        // 3 - black man
        // 4 - black king
        System.out.println(board);
        System.out.println();

        // TODO 
        
        // Return the move for the current state.
        // Here, we simply return the first legal move for demonstration.
        return legalMoves[0];
    }
    
    // TODO
    // 
    // Implement your helper methods here. They include at least the methods for selection,  
    // expansion, simulation, and back-propagation. 
    // 
    // For representation of the search tree, you are suggested (but limited) to use a 
    // child-sibling tree already implemented in the two classes CSTree and CSNode (which  
    // you may feel free to modify).  If you decide not to use the child-sibling tree, simply 
    // remove these two classes. 
    //

    public CheckersMove monteCarloTreeSearch() {


        return null;
    }

    /**
     * Step 1 of MCTS: Selection
     * Starting at the root, choose successors until a leaf if reached
     * (guided by a selection policy)
     * @param node
     * @return
     */
    CheckersMove select(MCNode<CheckersData> node) {
        return null;
    }

    /**
     * Step 2 of MCTS: Expansion
     * Grow the search tree by generating a new child of
     * the selected node
     */
    void expansion() {

    }

    /**
     * Step 3 of MCTS: Simulation
     * Perform playout from the newly generated child node
     * choose moves for both players using playout policy
     * @param tree
     * @return
     */
    MCNode<CheckersData> simulation(MCNode<CheckersData> tree) {
        MCNode<CheckersData> current = tree;
        while (current.state.getLegalMoves(1).length > 0) {
            CheckersMove move = select(current);
            node = followLink(node, move);
        }

        return null;
    }

    /**
     * Step 4 of MCTS: Back-propogation
     * Use the result of the simulation to update all search tree nodes,
     * going up to the root
     */
    void backPropogation() {}



}
