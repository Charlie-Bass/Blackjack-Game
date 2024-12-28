package org.cis1200.blackjack;

import java.util.ArrayList;
import java.util.List;

public class Blackjack {
    private Deck deck;
    private boolean playerTurn;
    private boolean gameOver;
    private boolean userBlackjack;
    private boolean dealerBlackjack;
    private boolean insurance;
    private boolean split;
    private boolean splitTurn;
    private boolean splitBlackjack;
    private Dealer dealer;
    private User user;

    public Blackjack(int initialAmount) {
        deck = new Deck();
        playerTurn = true;
        gameOver = false;
        userBlackjack = false;
        dealerBlackjack = false;
        insurance = false;
        dealer = new Dealer();
        user = new User(initialAmount);
        reset();
    }

    public void hit() {
        if (playerTurn) {
            user.receiveCard(deck.deal());
            if (user.bust()) {
                playerTurn = false;
                if (split) {
                    splitTurn = true;
                    return;
                }
                dealerPlay();
            } else if (user.calculateHandValue() == 21) {
                userBlackjack = true;
                playerTurn = false;
                if (split) {
                    splitTurn = true;
                }
                dealerPlay();
            }
        } else if (splitTurn) {
            user.recieveSplitCard(deck.deal());
            if (user.splitBust()) {
                splitTurn = false;
                dealerPlay();
            } else if (user.calculateHandValue(user.getSplitHand()) == 21) {
                splitBlackjack = true;
                splitTurn = false;
                dealerPlay();
            }
        }
    }

    public void dealerPlay() {
        while (dealer.play(deck.deal()) && !dealer.bust());
        if (dealer.calculateHandValue() == 21) {
            dealerBlackjack = true;
        }
        gameOver = true;
    }

    public void stand() {
        playerTurn = false;
        if (split) {
            if (splitTurn) {
                splitTurn = false;
                dealerPlay();
                return;
            }
            splitTurn = true;
            return;
        }
        dealerPlay();
    }

    public boolean validDoubleDown() {
        if (playerTurn && !userBlackjack && user.getBet() * 2 <= user.getAmount()
                && user.getHand().size() == 2) {
            return true;
        }
        return false;
    }

    public void doubleDown() {
        if (validDoubleDown()) {
            user.bet(user.getBet() * 2);
            user.receiveCard(deck.deal());
            playerTurn = false;
        }
        dealerPlay();
    }

    public boolean validInsurance() {
        if (playerTurn && !userBlackjack && dealer.getHand().get(0).getRank().equals("A")
                && user.getBet() * 1.5 <= user.getAmount()) {
            return true;
        }
        return false;
    }

    public void insurance() {
        if (validInsurance()) {
            user.setInsurance();
            insurance = true;
        }
    }

    public boolean getInsurance() {
        return insurance;
    }

    public boolean validSplit() {
        if (playerTurn && user.getHand().size() == 2
                && user.getHand().get(0).getRank().equals(user.getHand().get(1).getRank())
        && user.getAmount() >= user.getBet()*2) {
            return true;
        }
        return false;
    }

    public void split() {
        if (validSplit()) {
            user.split();
            split = true;
        }
    }

    public boolean getSplit() {
        return split;
    }

    public boolean getSplitTurn() {
        return splitTurn;
    }

    public ArrayList<Card> getSplitHand() {
        return user.getSplitHand();
    }

    public boolean placeBet(int amount) {
        return user.bet(amount);
    }

    public int getBet() {
        return user.getBet();
    }

    public boolean getUserBlackjack() {
        return userBlackjack;
    }

    public boolean getDealerBlackjack() {
        this.dealerBlackjack = true;
        return dealerBlackjack;
    }

    public void setUserBlackjack(boolean userBlackjack) {
        this.userBlackjack = userBlackjack;
    }

    public void setDealerBlackjack(boolean dealerBlackjack) {
        this.dealerBlackjack = dealerBlackjack;
    }

    public String[] getScores() {
        String[] result = new String[2];
        if ((playerTurn || splitTurn) && !dealer.getHand().isEmpty()) {
            int value = dealer.calculateHandValue() - dealer.getHand().get(1).getValue();
            result[0] = "" + value;
        } else {
            result[0] = "" + dealer.calculateHandValue();
            if (dealer.calculateHandValue() == 21) {
                result[0] = "Blackjack";
            } else if (dealer.bust()) {
                result[0] = "Bust";
            }
        }
        result[1] = "" + user.calculateHandValue();
        if (user.calculateHandValue() == 21) {
            result[1] = "Blackjack";
        } else if (user.bust()) {
            result[1] = "Bust";
        }
        return result;
    }

    public String getSplitHandScore() {
        if (!split) {
            return "No split hand";
        }

        int splitHandValue = user.calculateHandValue(user.getSplitHand());

        if (splitHandValue == 21) {
            return "Blackjack";
        } else if (user.splitBust()) {
            return "Bust";
        } else {
            return String.valueOf(splitHandValue);
        }
    }

    public void deal() {
        user.receiveCard(deck.deal());
        dealer.receiveCard(deck.deal());
        user.receiveCard(deck.deal());
        dealer.receiveCard(deck.deal());
        if (user.calculateHandValue() == 21) {
            userBlackjack = true;
            playerTurn = false;
            dealerPlay();
        }
    }

    public int getUserMoney() {
        return user.getAmount();
    }

    public List<Card> getUserHand() {
        return user.getHand();
    }

    public List<Card> getDealerHand() {
        return dealer.getHand();
    }

    public Dealer getDealer() {
        return dealer;
    }

    public User getUser() {
        return user;
    }

    public boolean userTurn() {
        return playerTurn;
    }

    public boolean gameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public void setPlayerTurn(boolean playerTurn) {
        this.playerTurn = playerTurn;
    }

    public void setSplitTurn(boolean splitTurn) {
        this.splitTurn = splitTurn;
    }

    public void setSplit(boolean split) {
        this.split = split;
    }

    public void setInsurance(boolean insurance) {
        this.insurance = insurance;
    }

    public int checkNonSplitWinner() {
        if (!gameOver()) {
            throw new RuntimeException("Game is not over");
        }

        if (!user.bust()) {
            if (dealer.bust()) {
                user.adjustEarnings(user.getBet());
                if (insurance) {
                    user.accountForInsurance(false);
                }
                return 1;
            } else if (user.calculateHandValue() > dealer.calculateHandValue()) {
                user.adjustEarnings(user.getBet());
                if (insurance) {
                    user.accountForInsurance(false);
                }
                return 1;
            } else if (user.calculateHandValue() == dealer.calculateHandValue()) {
                if (insurance && userBlackjack) {
                    user.accountForInsurance(true);
                }
                return 0;
            }
        }
        user.adjustEarnings(-1 * user.getBet());
        user.accountForInsurance(insurance && dealerBlackjack);
        return -1;
    }

    public int[] checkWinner() {
        if (!gameOver()) {
            throw new RuntimeException("Game is not over");
        }

        int[] result = new int[2];
        result[0] = checkNonSplitWinner();

        if (!split) {
            return result;
        } else {
            if (!user.splitBust()) {
                if (dealer.bust()) {
                    user.adjustEarnings(user.getBet());
                    result[1] = 1;
                    return result;
                } else if (user.calculateHandValue(user.getSplitHand()) > dealer
                        .calculateHandValue()) {
                    user.adjustEarnings(user.getBet());
                    result[1] = 1;
                    return result;
                } else if (user.calculateHandValue(user.getSplitHand()) == dealer
                        .calculateHandValue()) {
                    return result;
                }
            }
            result[1] = -1;
            user.adjustEarnings(-1 * user.getBet());
            return result;
        }
    }

    public void reset() {
        deck.reset();
        playerTurn = true;
        gameOver = false;
        userBlackjack = false;
        dealerBlackjack = false;
        insurance = false;
        split = false;
        splitTurn = false;
        splitBlackjack = false;
        dealer.reset();
        user.reset();
    }
}
