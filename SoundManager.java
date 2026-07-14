package com.cloudexify.guessgame;

import javax.sound.midi.*;

public class SoundManager {
    private static Synthesizer synth;
    private static MidiChannel channel;
    private static boolean soundEnabled = true;

    static {
        try {
            synth = MidiSystem.getSynthesizer();
            synth.open();
            // Get channel 0
            channel = synth.getChannels()[0];
            // Set instrument to Lead 1 (Square) program 80 (standard General MIDI synth)
            channel.programChange(80);
        } catch (Exception e) {
            System.out.println("MIDI Synthesizer not available. Sounds disabled: " + e.getMessage());
            channel = null;
        }
    }

    public static void setSoundEnabled(boolean enabled) {
        soundEnabled = enabled;
    }

    public static boolean isSoundEnabled() {
        return soundEnabled;
    }

    private static void playNote(int note, int durationMs, int velocity) {
        if (!soundEnabled || channel == null) return;
        new Thread(() -> {
            try {
                channel.noteOn(note, velocity);
                Thread.sleep(durationMs);
                channel.noteOff(note);
            } catch (Exception e) {
                // Ignore
            }
        }).start();
    }

    public static void playClick() {
        playNote(76, 50, 70); // High short E5 note
    }

    public static void playTooLow() {
        // Descending warning tones
        new Thread(() -> {
            if (!soundEnabled || channel == null) return;
            try {
                channel.noteOn(55, 90); // G3
                Thread.sleep(100);
                channel.noteOff(55);
                channel.noteOn(48, 100); // C3
                Thread.sleep(150);
                channel.noteOff(48);
            } catch (Exception e) {
                // Ignore
            }
        }).start();
    }

    public static void playTooHigh() {
        // Ascending warning tones
        new Thread(() -> {
            if (!soundEnabled || channel == null) return;
            try {
                channel.noteOn(60, 90); // C4
                Thread.sleep(100);
                channel.noteOff(60);
                channel.noteOn(67, 100); // G4
                Thread.sleep(150);
                channel.noteOff(67);
            } catch (Exception e) {
                // Ignore
            }
        }).start();
    }

    public static void playVictory() {
        // Major arpeggio upward fan-fare
        new Thread(() -> {
            if (!soundEnabled || channel == null) return;
            try {
                int[] notes = {60, 64, 67, 72, 76, 79, 84}; // C Major arpeggio notes
                int delay = 80;
                for (int note : notes) {
                    channel.noteOn(note, 95);
                    Thread.sleep(delay);
                }
                Thread.sleep(300);
                for (int note : notes) {
                    channel.noteOff(note);
                }
            } catch (Exception e) {
                // Ignore
            }
        }).start();
    }

    public static void playFailure() {
        // Dissonant buzz
        new Thread(() -> {
            if (!soundEnabled || channel == null) return;
            try {
                channel.noteOn(46, 110); // A#2
                channel.noteOn(47, 110); // B2
                Thread.sleep(120);
                channel.noteOn(41, 120); // F2
                Thread.sleep(400);
                channel.noteOff(46);
                channel.noteOff(47);
                channel.noteOff(41);
            } catch (Exception e) {
                // Ignore
            }
        }).start();
    }
}
