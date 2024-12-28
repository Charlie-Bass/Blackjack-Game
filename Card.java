package org.cis1200.blackjack;

public class Card {
    private String suit;
    private String rank;
    private int value;

    public Card(String suit, String rank) {
        this.suit = suit;
        this.rank = rank;
        this.value = findValue(rank);
    }

    private int findValue(String rank) {
        if (rank.equals("J") || rank.equals("Q") || rank.equals("K")) {
            return 10;
        }
        if (rank.equals("A")) {
            return 11;
        } else {
            return Integer.parseInt(rank);
        }
    }

    public String getSuit() {
        return suit;
    }

    public String getRank() {
        return rank;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
