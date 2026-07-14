package com.cloudexify.guessgame;

import java.util.Random;

public class Game {
    private int secretNumber;
    private int attempts;
    private int bestScore;
    private int difficulty; // 1 = Easy, 2 = Hard
    private int range;      // 50 for Easy, 200 for Hard
    private int maxAttempts; // -1 for unlimited, or positive limit
    private int lastGuess;  // To track warmer/colder
    private Random random;

    // Constructor (Guide-compatible default constructor)
    public Game() {
        this.random = new Random();
        this.secretNumber = 0;
        this.attempts = 0;
        this.bestScore = Leaderboard.loadBestScore();
        this.difficulty = 1;
        this.range = 50;
        this.maxAttempts = -1;
        this.lastGuess = -1;
    }

    // Advanced constructor for custom parameters (used by GUI/Console)
    public Game(int difficulty, boolean limitAttempts) {
        this.random = new Random();
        this.difficulty = difficulty;
        this.range = (difficulty == 1) ? 50 : 200;
        this.secretNumber = random.nextInt(range) + 1;
        this.attempts = 0;
        this.bestScore = Leaderboard.loadBestScore();
        this.lastGuess = -1;
        
        if (limitAttempts) {
            this.maxAttempts = (difficulty == 1) ? 7 : 10;
        } else {
            this.maxAttempts = -1;
        }
    }

    // Play method stub to match the guide blueprint (individual logic handled in ConsoleGame / GuiGame)
    public void play() {
        // Core game logic is executed through ConsoleGame.start() or GuiGame interfaces.
    }

    // Process a guess and return results:
    // -1 = Too Low
    // 1 = Too High
    // 0 = Correct
    public int checkGuess(int guess) {
        attempts++;
        int result;
        if (guess < secretNumber) {
            result = -1;
        } else if (guess > secretNumber) {
            result = 1;
        } else {
            result = 0;
            // If it's a win, save best score if it's improved
            if (attempts < bestScore) {
                bestScore = attempts;
                Leaderboard.saveBestScore(attempts);
            }
        }
        return result;
    }

    // Proximity hint (Bonus challenge: Warmer / Colder)
    public String getProximityHint(int guess) {
        if (lastGuess == -1) {
            lastGuess = guess;
            return ""; // No previous guess to compare to
        }
        
        int currentDiff = Math.abs(secretNumber - guess);
        int prevDiff = Math.abs(secretNumber - lastGuess);
        lastGuess = guess;

        if (currentDiff < prevDiff) {
            return "Getting WARMER! 🔥";
        } else if (currentDiff > prevDiff) {
            return "Getting COLDER! ❄️";
        } else {
            return "Same distance!";
        }
    }

    // Getters and Setters
    public int getSecretNumber() { return secretNumber; }
    public int getAttempts() { return attempts; }
    public int getBestScore() { return bestScore; }
    public int getDifficulty() { return difficulty; }
    public int getRange() { return range; }
    public int getMaxAttempts() { return maxAttempts; }
    public boolean isGameOver() {
        return maxAttempts != -1 && attempts >= maxAttempts;
    }
}
