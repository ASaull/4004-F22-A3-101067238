package com.aidansaull.crazyEights;

import java.util.List;

public class Score
{
    private List<Integer> scores;
    private Integer currentPlayer;
    private boolean direction;

    public Integer getRemaining()
    {
        return remaining;
    }

    private Integer remaining;

    public Card getTopCard()
    {
        return topCard;
    }

    private Card topCard;

    public Score(boolean direction, List<Integer> scores, Integer currentPlayer, Card topCard, Integer remaining)
    {
        this.direction = direction;
        this.scores = scores;
        this.currentPlayer = currentPlayer;
        this.topCard = topCard;
        this.remaining = remaining;
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
