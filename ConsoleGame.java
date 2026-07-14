package com.cloudexify.guessgame;

import java.util.Scanner;

public class ConsoleGame {
    public static void start() {
        Scanner input = new Scanner(System.in);
        boolean playMore = true;

        System.out.println("\n==================================");
        System.out.println("   WELCOME TO CLOUDEXIFY CONSOLE  ");
        System.out.println("==================================");
        System.out.println("Press Enter to start...");
        input.nextLine();

        while (playMore) {
            int bestScore = Leaderboard.loadBestScore();
            System.out.println("\n========================");
            System.out.println(" CLOUDEXIFY GUESS GAME");
            System.out.println("========================");
            if (bestScore == Integer.MAX_VALUE) {
                System.out.println("Best Score: No score yet");
            } else {
                System.out.println("Best Score: " + bestScore + " attempts");
            }

            // Choose difficulty
            int diff = 1;
            while (true) {
                System.out.print("\nDifficulty (1-Easy, 2-Hard): ");
                String diffStr = input.next();
                if (diffStr.equals("1") || diffStr.equals("2")) {
                    diff = Integer.parseInt(diffStr);
                    break;
                } else {
                    System.out.println("Invalid input! Please choose 1 or 2.");
                }
            }

            // Limit attempts option
            boolean limitAttempts = false;
            System.out.print("Limit attempts? (y/n): ");
            String limitStr = input.next();
            if (limitStr.equalsIgnoreCase("y")) {
                limitAttempts = true;
            }

            // Initialize Game
            Game game = new Game(diff, limitAttempts);
            int range = game.getRange();
            int maxAttempts = game.getMaxAttempts();

            System.out.println("\nI have picked a secret number between 1 and " + range + ".");
            if (limitAttempts) {
                System.out.println("Warning: You only have " + maxAttempts + " attempts!");
            }

            boolean guessed = false;
            while (!guessed && !game.isGameOver()) {
                int remaining = limitAttempts ? (maxAttempts - game.getAttempts()) : -1;
                if (limitAttempts) {
                    System.out.print("[" + remaining + " left] Guess (1-" + range + "): ");
                } else {
                    System.out.print("Guess (1-" + range + "): ");
                }

                // Input validation
                int guess;
                if (input.hasNextInt()) {
                    guess = input.nextInt();
                } else {
                    System.out.println("Invalid input! Enter a valid integer.");
                    input.next(); // Clear invalid token
                    continue;
                }

                // Range check
                if (guess < 1 || guess > range) {
                    System.out.println("Out of bounds! Guess must be between 1 and " + range + ".");
                    continue;
                }

                // Proximity hint (Before checking result)
                String proximity = game.getProximityHint(guess);

                // Check guess
                int result = game.checkGuess(guess);
                if (result == -1) {
                    System.out.println("Too LOW! " + proximity);
                    SoundManager.playTooLow();
                } else if (result == 1) {
                    System.out.println("Too HIGH! " + proximity);
                    SoundManager.playTooHigh();
                } else {
                    System.out.println("\n🎉 CORRECT! Attempts: " + game.getAttempts());
                    guessed = true;

                    // Play success sound
                    SoundManager.playVictory();

                    // If score is a new best score
                    if (game.getAttempts() < bestScore) {
                        System.out.println("🏆 NEW BEST SCORE!");
                        System.out.print("Enter your name for the leaderboard: ");
                        input.nextLine(); // Consume newline
                        String name = input.nextLine();
                        if (name.trim().isEmpty()) {
                            name = "ConsolePlayer";
                        }
                        Leaderboard.addScoreToLeaderboard(name, game.getAttempts(), diff == 1 ? "Easy" : "Hard");
                    }
                }
            }

            if (!guessed && game.isGameOver()) {
                System.out.println("\n💀 GAME OVER! You ran out of attempts.");
                System.out.println("The secret number was: " + game.getSecretNumber());
                SoundManager.playFailure();
            }

            // Play again loop
            System.out.print("\nPlay again? (y/n): ");
            String answer = input.next();
            playMore = answer.equalsIgnoreCase("y");
        }

        System.out.println("\nThanks for playing CloudExify Guessing Game!");
    }
}
