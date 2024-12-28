package org.cis1200.blackjack;

import java.awt.*;
import javax.swing.*;

/**
 * This class sets up the top-level frame and widgets for the Blackjack GUI.
 *
 * This game adheres to a Model-View-Controller design framework. This
 * framework is very effective for turn-based games.
 */
public class RunBlackjack implements Runnable {
    public void run() {
        final JFrame frame = new JFrame("Blackjack");
        frame.setLocation(300, 300);

        final JPanel statusPanel = new JPanel();
        frame.add(statusPanel, BorderLayout.SOUTH);
        final JLabel status = new JLabel("Setting up...");
        statusPanel.add(status);

        int initialMoney = setInitialAmount("Enter your initial money amount:");

        final GameBoard gameBoard = new GameBoard(status, initialMoney);
        frame.add(gameBoard, BorderLayout.CENTER);

        JPanel controlPanel = gameBoard.createControlPanel();
        frame.add(controlPanel, BorderLayout.NORTH);

        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        gameBoard.placeBetPrompt("Enter your bet amount:");
        gameBoard.deal();
    }

    private int setInitialAmount(String message) {
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
                return initialMoney;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    null, "Invalid Entry: Default to $1000", "Starting Money",
                    JOptionPane.PLAIN_MESSAGE
            );
            return 1000;
        }
        return initialMoney;
    }
}
