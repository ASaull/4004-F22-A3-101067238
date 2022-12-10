package com.aidansaull.crazyEights;

public class Card
    {
        public boolean isResetHand()
        {
            return resetHand;
        }

        boolean resetHand;

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

        /*public Card(Character rank, Character suit)
        {
            this.rank = rank;
            this.suit = suit;
            this.resetHand = false;
        }*/

        public Card(Character rank, Character suit, boolean resetHand)
        {
            this.rank = rank;
            this.suit = suit;
            this.resetHand = resetHand;
        }
    }