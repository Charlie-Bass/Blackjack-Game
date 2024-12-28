package org.cis1200.blackjack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private List<Card> deck;

    public Deck() {
        deck = new ArrayList<Card>();
        reset();
    }

    public void reset() {
        deck.clear();

        String[] suits = { "♠", "♥", "♦", "♣" };
        String[] ranks = { "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A" };

        for (String suit : suits) {
            for (String rank : ranks) {
                deck.add(new Card(suit, rank));
            }
        }

        Collections.shuffle(deck);
    }

    public Card deal() {
        return deck.remove(0);
    }
}
