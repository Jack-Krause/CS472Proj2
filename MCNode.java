package edu.iastate.cs472.proj2;

import java.util.ArrayList;
import java.util.List;

/**
 * Node type for the Monte Carlo search tree.
 */
public class MCNode<E>
{
    int currentPlayer;
    MCNode<E> parent;
    CheckersData state;
    CheckersMove move;
    int explorations;
    double wins;
    List<MCNode<E>> children;
    private List<CheckersMove> untriedMoves;

    public MCNode(CheckersData state, CheckersMove move, MCNode<E> parent, int aiSide) {
        this.state = state;
        this.move = move;
        this.explorations = 0;
        this.wins = 0;
        this.children = new ArrayList<MCNode<E>>();
        this.parent = parent;

        if (parent == null) {
            this.currentPlayer = aiSide;
        } else {
            this.currentPlayer = (parent.currentPlayer == CheckersData.RED) ? CheckersData.BLACK : CheckersData.RED;
        }

        this.untriedMoves = new ArrayList<>();
        CheckersMove[] legalMoves = state.getLegalMoves(this.currentPlayer);
        if (legalMoves != null) {
            for (CheckersMove m : legalMoves) {
                this.untriedMoves.add(m);
            }
        }

    }

    public void setParent(MCNode<E> parent) {
        this.parent = parent;
    }

    public MCNode<E> getParent() {
        return parent;
    }

    public void incrementExplorationCount() {
        this.explorations++;
    }

    public void addChild(MCNode<E> child) {
        child.parent = this;
        children.add(child);
    }

    public void update(double result) {
//        this.explorations++;
        this.wins += result;
    }

    double UCB1(int totalExplorations, double c) {
        if (this.explorations == 0) return Double.MAX_VALUE;

        double avgWins = (double) this.wins / this.explorations;
        return avgWins + c * Math.sqrt(Math.log(totalExplorations) / this.explorations);
    }

    double getAverageScore() {
        return (this.explorations == 0) ? 0 : this.wins / this.explorations;
    }

    public int getExplorations() {
        return this.explorations;
    }

    public CheckersMove getUntriedMove() {
        return this.untriedMoves.remove(0);
    }

   public boolean isLeaf() {
        return isTerminalNode() || !isExpanded();
    }

    public boolean isTerminalNode() {
        return this.state.gameOver();
    }

    public boolean isExpanded() {
        return this.untriedMoves.isEmpty();
    }

    public double getWins() {
        return this.wins;
    }

    public List<MCNode<E>> getChildren() {
        return this.children;
    }

    public int getCurrentPlayer() {
        return this.currentPlayer;
    }

    public void setCurrentPlayer() {
        this.currentPlayer = (this.currentPlayer == CheckersData.RED) ? CheckersData.BLACK : CheckersData.RED;
    }

}

