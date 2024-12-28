package org.cis1200.blackjack;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private String name;
    private List<Card> hand;
    private boolean bust;

    public Player() {
        this.name = "";
        this.hand = new ArrayList<>();
        this.bust = false;
    }

    public Player(String name) {
        this.name = name;
        this.hand = new ArrayList<>();
        this.bust = false;
    }

    public ArrayList<Card> getHand() {
        return (ArrayList<Card>) hand;
    }

    public void setHand(ArrayList<Card> hand) {
        this.hand = hand;
    }

    public void receiveCard(Card card) {
        hand.add(card);
    }

    public boolean hasAce() {
        for (Card card : hand) {
            if (card.getRank().equals("Ace")) {
                return true;
            }
        }
        return false;
    }

    public int calculateHandValue() {
        int totalValue = 0;
        int aceCount = 0;

        for (Card card : hand) {
            int value = card.getValue();
            if (value == 11) {
                aceCount++;
            } else {
                totalValue += value;
            }
        }

        for (int i = 1; i <= aceCount; i++) {
            if (totalValue + 11 <= 21) {
                totalValue += 11;
            } else {
                totalValue += 1;
            }
        }

        return totalValue;
    }

    public int calculateHandValue(ArrayList<Card> hand) {
        int totalValue = 0;
        int aceCount = 0;

        for (Card card : hand) {
            int value = card.getValue();
            if (value == 11) {
                aceCount++;
            } else {
                totalValue += value;
            }
        }

        for (int i = 1; i <= aceCount; i++) {
            if (totalValue + 11 <= 21) {
                totalValue += 11;
            } else {
                totalValue += 1;
            }
        }

        return totalValue;
    }

    public boolean bust() {
        if (calculateHandValue() > 21) {
            bust = true;
            return true;
        }
        return false;
    }

    public void reset() {
        hand.clear();
        bust = false;
    }
}
