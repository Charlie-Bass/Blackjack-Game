package org.cis1200.blackjack;

import java.util.ArrayList;

public class User extends Player {

    private int amount;
    private int bet;
    private int insurance;
    private ArrayList<Card> splitHand;
    private boolean splitBust;

    public User(int initialAmount) {
        super("User");
        amount = initialAmount;
        bet = initialAmount / 10;
        insurance = 0;
        splitHand = new ArrayList<>();
        splitBust = false;
    }

    public int getBet() {
        return bet;
    }

    public void setBet(int bet) {
        this.bet = bet;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public boolean bet(int bet) {
        if (bet <= amount && bet >= 1) {
            this.bet = bet;
            return true;
        } else {
            return false;
        }
    }

    public void setInsurance() {
        this.insurance = bet / 2;
    }

    public int getInsurance() {
        return insurance;
    }

    public void split() {
        splitHand.add(getHand().remove(1));
    }

    public void recieveSplitCard(Card card) {
        splitHand.add(card);
    }

    public ArrayList<Card> getSplitHand() {
        return splitHand;
    }

    public void setSplitHand(ArrayList<Card> splitHand) {
    }

    public boolean splitBust() {
        if (calculateHandValue(splitHand) > 21) {
            splitBust = true;
            return true;
        }
        return false;
    }

    public void adjustEarnings(int amount) {
        this.amount += amount;
    }

    public void accountForInsurance(boolean blackjack) {
        if (blackjack) {
            amount += 2*insurance;
        } else {
            amount -= insurance;
        }
    }

    @Override
    public void reset() {
        super.reset();
    }

}
