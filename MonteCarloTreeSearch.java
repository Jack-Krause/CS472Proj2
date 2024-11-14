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
        //System.out.println(board);
        //System.out.println();

        // TODO
        CheckersMove child = monteCarloTreeSearch();
        if (child != null) {
            return child;
        }

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
        MCNode<CheckersData> root = new MCNode<>(new CheckersData(), null);
        root.state = this.board;
        root.state.board = this.board.board;
        root.setCurrentPlayer();

        for (int i = 0; i < 150; i++) {
            // Step 1: Selection
            MCNode<CheckersData> selectedNode = select(root);

            // Step 2: Expansion
            List<MCNode<CheckersData>> expandedNodes = expansion(selectedNode);
            if (expandedNodes.size() == 0) { continue; }

            MCNode<CheckersData> simNode = expandedNodes.get((int) (Math.random() * expandedNodes.size()));

            // Step 3: Simulation
            double outcome = simulation(simNode);
            System.out.println("Outcome is: " + outcome);

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
            return bestChild.move;
        }

        System.out.println("Best child is null.");
        return null;
    }

    /**
     * Step 1 of MCTS: Selection
     * Starting at the root, choose successors until a leaf if reached
     * guided by a selection policy
     * @param node
     * @return
     */
    MCNode<CheckersData> select(MCNode<CheckersData> node) {
        MCNode<CheckersData> selection = node;
        double c = Math.sqrt(2);
        double best = Double.NEGATIVE_INFINITY;

        for (MCNode<CheckersData> child : node.getChildren()) {
            // select the best one according to policy
            double childU = child.UCB1(child.getExplorations(), c);
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
    List<MCNode<CheckersData>> expansion(MCNode<CheckersData> node) {

        List<MCNode<CheckersData>> generatedChildren = new ArrayList<>();
        //System.out.println("current player" + node.getCurrentPlayer());

        CheckersMove[] moves = node.state.getLegalMoves(node.getCurrentPlayer());

        for (CheckersMove move : moves) {
            CheckersData newState = node.state.clone();
            MCNode<CheckersData> childNode = new MCNode<>(newState, move);

            newState.makeMove(move);

            childNode.setCurrentPlayer();
            childNode.setParent(node);
            node.addChild(childNode);
            generatedChildren.add(childNode);
        }

        return generatedChildren;
    }

    /**
     * Step 3 of MCTS: Simulation
     * Perform playout from the newly generated child node
     * choose moves for both players using playout policy
     * @param tree
     * @return
     */
    double simulation(MCNode<CheckersData> tree) {
        CheckersData state = tree.state.clone();

        while (! state.gameOver()) {
            CheckersMove[] moves = state.getLegalMoves(tree.getCurrentPlayer());
            if (moves == null || moves.length == 0) break;

            CheckersMove randomMove = moves[(int) (Math.random() * moves.length)];
            state.makeMove(randomMove);
            tree.setCurrentPlayer();
        }

        double outcome = evaluate(state, tree.getCurrentPlayer());
        return outcome;
    }

    double evaluate(CheckersData state, int currentPlayer) {
        if (! state.gameOver()) {
//            throw new IllegalStateException("Game is not over");
//            return 0;
            System.out.println("Game not over.");
        }

        CheckersMove[] oppMoves = state.getLegalMoves(currentPlayer == CheckersData.RED ? CheckersData.BLACK : CheckersData.RED);
        CheckersMove[] curMoves = state.getLegalMoves(currentPlayer);

        boolean oppDone = oppMoves == null || oppMoves.length == 0;
        boolean curDone = curMoves == null || curMoves.length == 0;


        if (oppDone && curDone) return 0.5;
        if (oppDone) return 1;
        return 0;
    }

    /**
     * Step 4 of MCTS: Back-propagation
     * Use the result of the simulation to update all search tree nodes,
     * going up to the root
     */
    void backPropagation(MCNode<CheckersData> node, double outcome) {
        while (node != null) {
            node.incrementExplorationCount();
            node.update(outcome);
            System.out.println(outcome + ": " + node.wins);
            node = node.getParent();
        }
    }


}
