package com.aidansaull.crazyEights;


import java.util.ArrayList;
import java.util.List;

public class Player
{
    public List<Card> hand;
    public Integer score;

    public Player()
    {
        hand = new ArrayList<Card>();
        score = 0;
    }
}
