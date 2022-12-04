package com.aidansaull.crazyEights;

public class Card
    {
        public Character getSuit()
        {
            return suit;
        }

        public Character getRank()
        {
            return rank;
        }

        Character suit;
        Character rank;

        public Card(Character rank, Character suit)
        {
            this.rank = rank;
            this.suit = suit;
        }
    }