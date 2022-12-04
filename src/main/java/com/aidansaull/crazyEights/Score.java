package com.aidansaull.crazyEights;

import java.util.List;

public class Score
{
    private List<Integer> scores;
    private boolean direction;

    public Score(boolean direction, List<Integer> scores)
    {
        this.direction = direction;
        this.scores = scores;
    }

    public List<Integer> getScores()
    {
        return scores;
    }

    public boolean isDirection()
    {
        return direction;
    }

}
