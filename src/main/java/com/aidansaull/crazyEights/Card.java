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

        @Override
        public String toString()
        {
            return "Card " + rank + suit;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o)
                return true;
            if (o == null)
                return false;
            if (getClass() != o.getClass())
                return false;
            Card card = (Card) o;
            return (card.suit == suit && card.rank == rank);
        }

        Character suit;
        Character rank;

        public Card(Character rank, Character suit)
        {
            this.rank = rank;
            this.suit = suit;
        }
    }