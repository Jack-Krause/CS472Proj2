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
    int wins;
    List<MCNode<E>> children;

    public MCNode(CheckersData state, CheckersMove move) {
        this.state = state;
        this.move = move;
        this.explorations = 0;
        this.wins = 0;
        this.children = new ArrayList<MCNode<E>>();
        this.parent = null;
        this.currentPlayer = 1;
    }

    public void addChild(MCNode<E> child) {
        child.parent = this;
        children.add(child);
    }

    public void update(int result) {
        this.explorations++;
        this.wins += result;
    }

    double UCB1(int totalExplorations, double c) {
        if (this.explorations == 0) return Double.MAX_VALUE;

        double avgWins = (double) this.wins / this.explorations;
        return avgWins + c * Math.sqrt(Math.log(totalExplorations) / this.explorations);
    }

    public int getExplorations() {
        return this.explorations;
    }

    public int getWins() {
        return this.wins;
    }

    public List<MCNode<E>> getChildren() {
        return this.children;
    }

    public int getCurrentPlayer() {
        return this.currentPlayer;
    }

    public void setCurrentPlayer(int currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

}

