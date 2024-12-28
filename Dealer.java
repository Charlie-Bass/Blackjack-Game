package org.cis1200.blackjack;

public class Dealer extends Player {
    public Dealer() {
        super("Dealer");
    }

    public boolean play(Card card) {
        if (calculateHandValue() < 17) {
            receiveCard(card);
            return calculateHandValue() < 17;
        }
        return false;
    }
}
