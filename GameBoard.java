package org.cis1200.blackjack;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import javax.swing.*;

/**
 * This class instantiates a blackjack object, which is the model for the game.
 * As the user clicks the buttons, the model is updated. Whenever the model
 * is updated, the game board repaints itself and updates its status JLabel to
 * reflect the current state of the model.
 */
@SuppressWarnings("serial")
public class GameBoard extends JPanel {

    private Blackjack blackjack;
    private JLabel status;
    private int playerMoney;

    public static final int BOARD_WIDTH = 900;
    public static final int BOARD_HEIGHT = 500;

    private JButton hitButton;
    private JButton standButton;
    private JButton doubleDownButton;
    private JButton insuranceButton;
    private JButton splitButton;
    private JButton resetButton;
    private JButton saveButton;
    private JButton loadButton;
    private JButton instructionsButton;
    private boolean cardDealt;
    private boolean split;

    /**
     * Initializes the game board with the player's initial amount of money.
     */
    public GameBoard(JLabel statusInit, int initialMoney) {
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        setFocusable(true);

        playerMoney = initialMoney;
        blackjack = new Blackjack(playerMoney);
        status = statusInit;
        cardDealt = false;
    }

    /**
     * Adds a panel with buttons for Hit, Stand, Reset, Save, Load, and special actions.
     */
    public JPanel createControlPanel() {
        JPanel controlPanel = new JPanel();

        hitButton = new JButton("Hit");
        hitButton.setEnabled(false);
        hitButton.addActionListener(e -> {
            if (blackjack.getBet() > 0 && !blackjack.gameOver()) {
                blackjack.hit();
                updateStatus();
                checkInsurance();
                checkSplit();
                repaint();
            } else {
                status.setText("Place a bet before hitting!");
            }
        });
        controlPanel.add(hitButton);

        standButton = new JButton("Stand");
        standButton.setEnabled(false);
        standButton.addActionListener(e -> {
            if (blackjack.getBet() > 0 && !blackjack.gameOver()) {
                blackjack.stand();
                updateStatus();
                repaint();
            } else {
                status.setText("Place a bet before standing!");
            }
        });
        controlPanel.add(standButton);

        doubleDownButton = new JButton("Double down");
        doubleDownButton.setEnabled(true);
        doubleDownButton.setVisible(false);
        doubleDownButton.addActionListener(e -> {
            blackjack.doubleDown();
            updateStatus();
            repaint();
        });
        controlPanel.add(doubleDownButton);

        insuranceButton = new JButton("Insurance");
        insuranceButton.setEnabled(true);
        insuranceButton.setVisible(false);
        insuranceButton.addActionListener(e -> {
            blackjack.insurance();
            updateStatus();
            repaint();
        });
        controlPanel.add(insuranceButton);

        splitButton = new JButton("Split");
        splitButton.setEnabled(true);
        splitButton.setVisible(false);
        splitButton.addActionListener(e -> {
            blackjack.split();
            split = true;
            updateStatus();
            repaint();
        });
        controlPanel.add(splitButton);

        resetButton = new JButton("Reset");
        resetButton.setEnabled(true);
        resetButton.addActionListener(e -> {
            blackjack.reset();
            reset();
        });
        controlPanel.add(resetButton);

        saveButton = new JButton("Save");
        saveButton.setEnabled(true);
        saveButton.addActionListener(e -> {
            saveGameState("save_file");
        });
        controlPanel.add(saveButton);

        loadButton = new JButton("Load");
        loadButton.setEnabled(true);
        loadButton.addActionListener(e -> {
            loadGameState("save_file");
        });
        controlPanel.add(loadButton);

        instructionsButton = new JButton("Instructions");
        instructionsButton.setEnabled(true);
        instructionsButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                    null, "The goal is to get as close to 21 without going over. " +
                            "Hit to get a new card or stand to stay where you are, if you go " +
                            "over 21 you lose. If you stand \nyou win if your total is higher than the " +
                            "dealer's in which case you double your bet, if it is a tie you get your bet back. " +
                            "Special Rules: \n - Aces can count as 1 or 11 (the game will automatically handle this)" +
                            "\n - Double Down: a special move where you double your bet and are dealt only one card." +
                            "\n - Insurance: if the dealer has a face up ace you can place an insurance bet worth " +
                            "half of your original bet that pays out if the dealer has a blackjack. " +
                            "\n - Split: if your first two cards are the same value you can split and play them as two" +
                            " separate hands \nHave Fun!!!", "Instructions",
                    JOptionPane.PLAIN_MESSAGE
            );
        });
        controlPanel.add(instructionsButton);

        return controlPanel;
    }

    /**
     * Resets the game to its initial state, maintaining the player's money.
     */
    public void reset() {
        if (playerMoney <= 0) {
            addMoreMoney("Add more money to continue playing");
        }
        blackjack = new Blackjack(playerMoney);
        cardDealt = false;
        split = false;
        status.setText("Game reset! Place your bet.");
        toggleActionButtons(false);
        checkDoubleDown();
        checkInsurance();
        checkSplit();
        repaint();
        requestFocusInWindow();
        placeBetPrompt("Enter your bet amount:");
        deal();
    }

    private void addMoreMoney(String message) {
        String initialMoneyInput = JOptionPane.showInputDialog(
                null,
                message,
                "Starting Money",
                JOptionPane.PLAIN_MESSAGE
        );

        int initialMoney = 0;
        try {
            if (initialMoneyInput != null && !initialMoneyInput.isEmpty()) {
                initialMoney = Integer.parseInt(initialMoneyInput);
                if (initialMoney <= 0) {
                    while (initialMoney <= 0) {
                        String redoInput = JOptionPane.showInputDialog(
                                null, "Please enter a valid amount:", "Starting Money",
                                JOptionPane.PLAIN_MESSAGE
                        );
                        initialMoney = Integer.parseInt(redoInput);
                    }
                }
            }
        } catch (NumberFormatException e) {
            addMoreMoney("Enter a valid numerical value");
        }
        playerMoney += initialMoney;
    }

    /**
     * Updates the JLabel to reflect the current state of the game.
     */
    private void updateStatus() {
        if (blackjack.gameOver()) {
            if (!split) {
                int winner = blackjack.checkNonSplitWinner();
                if (blackjack.gameOver()) {
                    if (winner > 0) {
                        status.setText("You win! Current Money: $" + blackjack.getUserMoney());
                        playerMoney = blackjack.getUserMoney();
                    } else if (winner < 0) {
                        status.setText("Dealer wins! Current Money: $" + blackjack.getUserMoney());
                        playerMoney = blackjack.getUserMoney();
                    } else {
                        status.setText("It's a tie! Current Money: $" + blackjack.getUserMoney());
                        playerMoney = blackjack.getUserMoney();
                    }
                    toggleActionButtons(false);
                }
            } else {
                int[] wins = blackjack.checkWinner();
                if (blackjack.gameOver()) {
                    if (wins[0] > 0) {
                        if (wins[1] > 0) {
                            status.setText(
                                    "You win both hands! Current Money: $"
                                            + blackjack.getUserMoney()
                            );
                        } else if (wins[1] < 0) {
                            status.setText(
                                    "You win one hand and break even! Current Money: $"
                                            + blackjack.getUserMoney()
                            );
                        } else {
                            status.setText(
                                    "You win one hand and tie the other! Current Money: $"
                                            + blackjack.getUserMoney()
                            );
                        }
                    } else if (wins[0] < 0) {
                        if (wins[1] > 0) {
                            status.setText(
                                    "You win one hand and break even! Current Money: $"
                                            + blackjack.getUserMoney()
                            );
                        } else if (wins[1] < 0) {
                            status.setText(
                                    "You lose both hands! Current Money: $"
                                            + blackjack.getUserMoney()
                            );
                        } else {
                            status.setText(
                                    "You lose one hand and tie the other! Current Money: $"
                                            + blackjack.getUserMoney()
                            );
                        }
                    } else {
                        if (wins[1] > 0) {
                            status.setText(
                                    "You win one hand and tie the other! Current Money: $"
                                            + blackjack.getUserMoney()
                            );
                        } else if (wins[1] < 0) {
                            status.setText(
                                    "You lose one hand and tie the other! Current Money: $"
                                            + blackjack.getUserMoney()
                            );
                        } else {
                            status.setText(
                                    "You tie both hands! Current Money: $"
                                            + blackjack.getUserMoney()
                            );
                        }
                    }
                    toggleActionButtons(false);
                }
            }
        }
        checkDoubleDown();
        checkInsurance();
        checkSplit();
    }

    /**
     * Toggles the enabled state of the Hit and Stand buttons.
     *
     * @param enabled Whether to enable or disable the buttons
     */
    private void toggleActionButtons(boolean enabled) {
        hitButton.setEnabled(enabled);
        standButton.setEnabled(enabled);
    }

    private void checkDoubleDown() {
        doubleDownButton.setVisible(!split && cardDealt && blackjack.validDoubleDown());
    }

    private void checkInsurance() {
        insuranceButton.setVisible(cardDealt && blackjack.validInsurance());
    }

    private void checkSplit() {
        splitButton.setVisible(cardDealt && blackjack.validSplit());
    }

    /**
     * Draws the game board, including cards with rank and suit.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (!split) {
            g.setColor(Color.BLUE);
            g.drawString("Player's Score: " + blackjack.getScores()[1], 20, 260);
            drawCards(g, (ArrayList<Card>) blackjack.getUserHand(), 20, 280);
        } else {
            g.setColor(Color.BLUE);
            g.drawString("Player's Score: " + blackjack.getScores()[1], 20, 260);
            drawCards(g, (ArrayList<Card>) blackjack.getUserHand(), 20, 280);
            drawCards(g, blackjack.getSplitHand(), 20, 370);
        }

        g.setColor(Color.RED);
        g.drawString("Dealer's Score: " + blackjack.getScores()[0], 20, 110);
        drawCards(g, (ArrayList<Card>) blackjack.getDealerHand(), 20, 130);
        if ((blackjack.userTurn() || blackjack.getSplitTurn()) && cardDealt) {
            g.setColor(Color.WHITE);
            g.fillRect(100, 140, 40, 60);
        }

        g.setColor(Color.BLACK);
        g.drawString("Player's Money: $" + blackjack.getUserMoney(), 20, 20);
        g.drawString("Current Bet: $" + blackjack.getBet(), 20, 40);

        if (blackjack.getInsurance()) {
            g.setColor(Color.BLACK);
            g.drawString("Insurance: $" + blackjack.getUser().getInsurance(), 20, 80);
        }

        if (split) {
            g.setColor(Color.BLACK);
            g.drawString("Current Split Bet: $" + blackjack.getBet(), 20, 60);
            g.setColor(Color.BLUE);
            g.drawString("Split Hand Score: " + blackjack.getSplitHandScore(), 20, 470);
        }
    }

    /**
     * Helper method to draw cards on the game board.
     *
     * @param g    the Graphics object for drawing
     * @param hand the list of cards to draw
     * @param x    the x-coordinate to start drawing
     * @param y    the y-coordinate to start drawing
     */
    private void drawCards(Graphics g, ArrayList<Card> hand, int x, int y) {
        int cardWidth = 60;
        int cardHeight = 80;
        int spacing = 10;

        for (int i = 0; i < hand.size(); i++) {
            g.setColor(Color.WHITE);
            g.fillRect(x + i * (cardWidth + spacing), y, cardWidth, cardHeight);
            g.setColor(Color.BLACK);
            g.drawRect(x + i * (cardWidth + spacing), y, cardWidth, cardHeight);

            Card card = hand.get(i);
            if (card.getSuit().equals("♠") || card.getSuit().equals("♣")) {
                g.setColor(Color.BLACK);
            } else {
                g.setColor(Color.RED);
            }
            g.drawString(
                    card.getRank() + card.getSuit(), x + i * (cardWidth + spacing) + 10, y + 40
            );
        }
    }

    public void placeBetPrompt(String message) {
        String betInput = JOptionPane
                .showInputDialog(null, message, "Place Your Bet", JOptionPane.PLAIN_MESSAGE);

        if (betInput != null) {
            try {
                int newBet = Integer.parseInt(betInput);
                if (blackjack.placeBet(newBet)) {
                    status.setText("Bet placed: $" + newBet + ". Hit or Stand?");
                    toggleActionButtons(true);
                    repaint();
                } else {
                    while (!blackjack.placeBet(newBet)) {
                        String redoInput = JOptionPane.showInputDialog(
                                null, "Please enter a valid bet amount:", "Place Your Bet",
                                JOptionPane.PLAIN_MESSAGE
                        );
                        newBet = Integer.parseInt(redoInput);
                    }
                    toggleActionButtons(true);
                    repaint();
                }
            } catch (NumberFormatException ex) {
                placeBetPrompt("Please enter a valid numerical bet amount");
            }
        } else {
            status.setText("Bet not placed.");
        }
    }

    public void deal() {
        blackjack.deal();
        cardDealt = true;
        status.setText("Game started!");
        updateStatus();
        repaint();
    }

    /**
     * Returns the preferred size of the game board.
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    }

    // File Saving
    public void saveGameState(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            // Save player state
            writer.write(blackjack.getUser().getAmount() + "\n");
            writer.write(blackjack.getUser().getBet() + "\n");
            writer.write(convertHandToString(blackjack.getUser().getHand()) + "\n");
            writer.write(convertHandToString(blackjack.getUser().getSplitHand()) + "\n");
            writer.write(blackjack.getUserBlackjack() + "\n");

            // Save dealer state
            writer.write(convertHandToString(blackjack.getDealer().getHand()) + "\n");
            writer.write(blackjack.getDealerBlackjack() + "\n");

            // Save game flags
            writer.write(blackjack.gameOver() + "\n");
            writer.write(blackjack.userTurn() + "\n");
            writer.write(blackjack.getSplit() + "\n");
            writer.write(blackjack.getSplitTurn() + "\n");
            writer.write(blackjack.getInsurance() + "\n");

        } catch (IOException e) {
            System.out.println("An error occurred while saving the game state.");
            e.printStackTrace();
        }
    }

    private String convertHandToString(ArrayList<Card> hand) {
        StringBuilder handString = new StringBuilder();
        for (Card card : hand) {
            handString.append(card.getRank()).append(card.getSuit()).append(" ");
        }
        return handString.toString().trim();
    }

    public void loadGameState(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;

            // Read player state
            if ((line = reader.readLine()) != null) {
                blackjack.getUser().setAmount(Integer.parseInt(line));
                line = reader.readLine();
                blackjack.getUser().setBet(Integer.parseInt(line));
                line = reader.readLine();
                blackjack.getUser().setHand(convertStringToHand(line));
                line = reader.readLine();
                blackjack.getUser().setSplitHand(convertStringToHand(line));
                line = reader.readLine();
                blackjack.setUserBlackjack(Boolean.parseBoolean(line));
                line = reader.readLine();
                blackjack.getDealer().setHand(convertStringToHand(line));
                line = reader.readLine();
                blackjack.setDealerBlackjack(Boolean.parseBoolean(line));
                line = reader.readLine();
                blackjack.setGameOver(Boolean.parseBoolean(line));
                line = reader.readLine();
                blackjack.setPlayerTurn(Boolean.parseBoolean(line));
                line = reader.readLine();
                boolean s = Boolean.parseBoolean(line);
                blackjack.setSplit(s);
                split = s;
                line = reader.readLine();
                blackjack.setSplitTurn(Boolean.parseBoolean(line));
                line = reader.readLine();
                blackjack.setInsurance(Boolean.parseBoolean(line));
            }

            System.out.println(blackjack.getUser().getAmount());

            updateStatus();
            repaint();

        } catch (IOException e) {
            System.out.println("An error occurred while loading the game state.");
            e.printStackTrace();
        }
    }

    private ArrayList<Card> convertStringToHand(String handString) {
        ArrayList<Card> hand = new ArrayList<>();
        String[] cardStrings = handString.split(" ");
        for (String cardString : cardStrings) {
            if (cardString.isEmpty()) {
                break;
            }
            String rank = cardString.substring(0, 1);
            String suit = cardString.substring(1);
            hand.add(new Card(suit, rank));
        }
        return hand;
    }

}
