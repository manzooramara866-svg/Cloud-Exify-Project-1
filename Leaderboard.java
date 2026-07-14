package com.cloudexify.guessgame;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Leaderboard {
    private static final String BEST_SCORE_FILE = "bestscore.txt";
    private static final String LEADERBOARD_FILE = "leaderboard.txt";

    public static class ScoreEntry {
        private final String name;
        private final int attempts;
        private final String difficulty;
        private final String date;

        public ScoreEntry(String name, int attempts, String difficulty, String date) {
            this.name = name;
            this.attempts = attempts;
            this.difficulty = difficulty;
            this.date = date;
        }

        public String getName() { return name; }
        public int getAttempts() { return attempts; }
        public String getDifficulty() { return difficulty; }
        public String getDate() { return date; }

        @Override
        public String toString() {
            return name + "," + attempts + "," + difficulty + "," + date;
        }
    }

    // Load best score from file (exactly as guide specifies)
    public static int loadBestScore() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(BEST_SCORE_FILE));
            String line = reader.readLine();
            reader.close();
            if (line != null && !line.trim().isEmpty()) {
                return Integer.parseInt(line.trim());
            }
        } catch (IOException | NumberFormatException e) {
            // File doesn't exist or is invalid
        }
        return Integer.MAX_VALUE; // No score yet
    }

    // Save best score to file (exactly as guide specifies)
    public static void saveBestScore(int attempts) {
        try {
            FileWriter writer = new FileWriter(BEST_SCORE_FILE);
            writer.write(String.valueOf(attempts));
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving best score!");
        }
    }

    // Load top 5 leaderboard entries
    public static List<ScoreEntry> loadLeaderboard() {
        List<ScoreEntry> scores = new ArrayList<>();
        File file = new File(LEADERBOARD_FILE);
        if (!file.exists()) {
            return scores;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    scores.add(new ScoreEntry(
                        parts[0],
                        Integer.parseInt(parts[1]),
                        parts[2],
                        parts[3]
                    ));
                }
            }
        } catch (IOException | NumberFormatException e) {
            // Silently handle read errors
        }
        
        // Sort: primary attempts (ascending), secondary name
        scores.sort(Comparator.comparingInt(ScoreEntry::getAttempts));
        return scores;
    }

    // Add a new entry to the leaderboard and keep only the top 5
    public static void addScoreToLeaderboard(String name, int attempts, String difficulty) {
        if (name == null || name.trim().isEmpty()) {
            name = "Player";
        }
        name = name.replace(",", " "); // Avoid CSV formatting break
        
        List<ScoreEntry> scores = loadLeaderboard();
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String dateStr = sdf.format(new Date());
        
        scores.add(new ScoreEntry(name.trim(), attempts, difficulty, dateStr));
        
        // Sort and limit to top 5
        scores.sort(Comparator.comparingInt(ScoreEntry::getAttempts));
        if (scores.size() > 5) {
            scores = scores.subList(0, 5);
        }

        // Save back to file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LEADERBOARD_FILE))) {
            for (ScoreEntry entry : scores) {
                writer.write(entry.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving leaderboard score: " + e.getMessage());
        }
    }
}
