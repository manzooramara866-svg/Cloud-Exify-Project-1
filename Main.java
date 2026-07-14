package com.cloudexify.guessgame;

import java.awt.GraphicsEnvironment;

public class Main {
    public static void main(String[] args) {
        // Detect environment capabilities
        boolean isHeadless = GraphicsEnvironment.isHeadless();

        // Parse command line arguments
        if (args.length > 0) {
            String arg = args[0].toLowerCase();
            if (arg.equals("--console") || arg.equals("-c")) {
                ConsoleGame.start();
                return;
            } else if (arg.equals("--gui") || arg.equals("-g")) {
                if (isHeadless) {
                    System.out.println("Error: Cannot start GUI in a headless terminal environment.");
                    System.out.println("Launching Console Mode instead...");
                    ConsoleGame.start();
                } else {
                    launchGui();
                }
                return;
            } else {
                System.out.println("CloudExify Guessing Game - Help Manual");
                System.out.println("=====================================");
                System.out.println("Commands:");
                System.out.println("  -g, --gui      Start the game in high-fidelity GUI dashboard mode (default)");
                System.out.println("  -c, --console  Start the game in retro CLI console mode");
                System.out.println("  --help         Display this help message");
                System.out.println("\nLaunching default mode...");
            }
        }

        // Default behavior: GUI mode if supported, otherwise Console mode
        if (isHeadless) {
            ConsoleGame.start();
        } else {
            launchGui();
        }
    }

    private static void launchGui() {
        System.out.println("Launching CloudExify Guessing Game GUI...");
        // Ensure GUI runs on AWT Event Dispatch Thread for thread safety
        javax.swing.SwingUtilities.invokeLater(() -> {
            try {
                GuiGame frame = new GuiGame();
                frame.setVisible(true);
            } catch (Exception e) {
                System.err.println("Failed to initialize GUI: " + e.getMessage());
                System.out.println("Falling back to Console Mode...");
                ConsoleGame.start();
            }
        });
    }
}
