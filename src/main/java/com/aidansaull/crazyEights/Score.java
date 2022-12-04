package com.aidansaull.crazyEights;

import java.util.List;

public class Score
{
    private List<Integer> scores;
    private Integer currentPlayer;
    private boolean direction;

    public Score(boolean direction, List<Integer> scores, Integer currentPlayer)
    {
        this.direction = direction;
        this.scores = scores;
        this.currentPlayer = currentPlayer;
    }

    public List<Integer> getScores()
    {
        return scores;
    }

    public boolean isDirection()
    {
        return direction;
    }

    public Integer getCurrentPlayer()
    {
        return currentPlayer;
    }

}
