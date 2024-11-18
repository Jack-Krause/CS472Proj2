package edu.iastate.cs472.proj2;

/**
 * @author
 */

import java.util.ArrayList;
import java.util.List;

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
     * <p>
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
        //System.out.println(board);
        //System.out.println();

        // TODO
        CheckersMove child = monteCarloTreeSearch();
        if (child != null) {
            return child;
        }

        // Return the move for the current state.
        // Here, we simply return the first legal move for demonstration.
        System.out.println("No best move found. Returning");
        return legalMoves[0];
    }

    // Implement your helper methods here. They include at least the methods for selection,
    // expansion, simulation, and back-propagation. 
    // 
    // For representation of the search tree, you are suggested (but limited) to use a 
    // child-sibling tree already implemented in the two classes CSTree and CSNode (which  
    // you may feel free to modify).  If you decide not to use the child-sibling tree, simply 
    // remove these two classes. 
    //
    public CheckersMove monteCarloTreeSearch() {
        MCNode<CheckersData> root = new MCNode<>(this.board.clone(), null, null, this.side);

        for (int i = 0; i < 1000; i++) {
            // Step 1: Selection
            MCNode<CheckersData> selectedNode = select(root);

            // Step 2: Expansion
            MCNode<CheckersData> simNode = expansion(selectedNode);
            // Step 3: Simulation
            double outcome = simulation(simNode);
            int counter = 0;
            if (outcome != 0) {
                counter ++;
//                System.out.println(counter + "- OUT: " + outcome);
            }

            // Step 4: Backpropagation
            backPropagation(simNode, outcome);
        }

        MCNode<CheckersData> bestChild = null;
        double bestScore = Double.NEGATIVE_INFINITY;

        for (MCNode<CheckersData> child : root.getChildren()) {

            double avgScore = child.getAverageScore();

            if (avgScore > bestScore) {
                bestScore = avgScore;
                bestChild = child;
            }
        }


        if (bestChild != null) {
            //Best choice:
            System.out.println(bestChild.getAverageScore());
            return bestChild.move;
        }
//
        CheckersMove[] legalMoves = this.board.getLegalMoves(root.getCurrentPlayer());
        return legalMoves[0];
    }

    /**
     * Step 1 of MCTS: Selection
     * Starting at the root, choose successors until a leaf if reached
     * guided by a selection policy
     *
     * @param node
     * @return
     */
    MCNode<CheckersData> select(MCNode<CheckersData> node) {
        while (! node.isLeaf()) {
            node = bestChild(node);
        }

        return node;
    }

    MCNode<CheckersData> bestChild(MCNode<CheckersData> node) {
        MCNode<CheckersData> selection = node;
        double c = Math.sqrt(2);
        double best = Double.NEGATIVE_INFINITY;

        for (MCNode<CheckersData> child : node.getChildren()) {
            // select the best one according to policy
            double childU = child.UCB1(node.getExplorations(), c);
            if (childU > best) {
                selection = child;
                best = childU;
            }
        }

        return selection;
    }

    /**
     * Step 2 of MCTS: Expansion
     * Grow the search tree by generating a new child of
     * the selected node
     */
    MCNode<CheckersData> expansion(MCNode<CheckersData> node) {
        if (node.isTerminalNode()) return node;

        CheckersMove move = node.getUntriedMove();
        CheckersData newState = node.state.clone();
        newState.makeMove(move);

        MCNode<CheckersData> childNode = new MCNode<>(newState, move, node, this.side);
        node.addChild(childNode);

        return childNode;
    }

    /**
     * Step 3 of MCTS: Simulation
     * Perform playout from the newly generated child node
     * choose moves for both players using playout policy
     *
     * @param tree
     * @return
     */
    double simulation(MCNode<CheckersData> tree) {
        CheckersData state = tree.state.clone();
        int currentPlayer = tree.getCurrentPlayer();

        int depth = 0;
        int maxDepth = 1000;
        int noMoveCount = 0;

        while (! state.gameOver() && depth < maxDepth) {
            CheckersMove[] moves = state.getLegalMoves(currentPlayer);

            if (moves == null || moves.length == 0) {
                noMoveCount++;
                if (noMoveCount == 2) {
                    break;
                    // neither of the players have a move. End the game.
                } else {
                    // continue the sim with the other player
                    currentPlayer = (currentPlayer == CheckersData.RED) ? CheckersData.BLACK : CheckersData.RED;
                    continue;
                }
            } else {
                noMoveCount = 0;
                CheckersMove randomMove = moves[(int) (Math.random() * moves.length)];
                state.makeMove(randomMove);
                currentPlayer = (currentPlayer == CheckersData.RED) ? CheckersData.BLACK : CheckersData.RED;
            }
        }

        double outcome = evaluate(state, this.side);
        return outcome;
    }

    double evaluate(CheckersData state, int aiSide) {
        // Check for terminal game state
        if (!state.gameOver()) {
            return 0.5;
        }

        // Get the legal moves for both players
        CheckersMove[] oppMoves = state.getLegalMoves(aiSide == CheckersData.RED ? CheckersData.BLACK : CheckersData.RED);
        CheckersMove[] curMoves = state.getLegalMoves(aiSide);

        // Check if either player has no legal moves
        boolean oppNoMoves = oppMoves == null || oppMoves.length == 0;
        boolean curNoMoves = curMoves == null || curMoves.length == 0;

        // Count pieces for both players
        int redPieces = state.countPieces(CheckersData.RED) + state.countPieces(CheckersData.RED_KING);
        int blackPieces = state.countPieces(CheckersData.BLACK) + state.countPieces(CheckersData.BLACK_KING);

        // Win/Loss/Draw conditions
        if (curNoMoves && oppNoMoves) {
            return 0.5; // Draw: Both players are stuck
        }
        if (curNoMoves || redPieces == 0) {
            return 1; // Loss: Current player has no moves or no pieces left
        }
        if (oppNoMoves || blackPieces == 0) {
            return 0; // Win: Opponent has no moves or no pieces left
        }

        // Default: Non-terminal states should not reach this point
        throw new IllegalStateException("Unexpected state in evaluate");
    }

    /**
     * Step 4 of MCTS: Back-propagation
     * Use the result of the simulation to update all search tree nodes,
     * going up to the root
     */
    void backPropagation(MCNode<CheckersData> node, double outcome) {
        while (node != null) {
            node.incrementExplorationCount();

            if (node.currentPlayer == this.side) {
                // If it's the AI's node, add the outcome directly
                node.update(outcome);
            } else {
                // If it's the opponent's node, invert the outcome
                node.update(1.0 - outcome);
            }

            node = node.getParent();
        }
    }


}
