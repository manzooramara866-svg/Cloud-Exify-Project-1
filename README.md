# CloudExify Number Guessing Game  "Java Edition"

Welcome to the **CloudExify Guessing Game**, a premium, competition-winning Java desktop application. This project is built completely from scratch using standard Java SE libraries (AWT/Swing for GUI, MIDI for Synthesizer sound effects, File I/O for score persistence). It features **no external build managers (Maven/Gradle)** or libraries, making it lightweight and fully portable.

The project features a **Dual-Mode System**:
1. **Graphical Mode (GUI)**: A modern, play-store style dark-themed interface with custom-painted components, keyboard/mouse input, a numerical keypad, responsive thermometer proximity gauges, attempts progress bars, and retro midi sound effects.
2. **Terminal Mode (Console)**: A clean, retro console-based implementation matching the core requirements, utilizing standard `Scanner`, `Random`, error handling, and loops.

---

  Project Directory Structure

The files inside the project folder are structured as a standard IntelliJ IDEA project:

```
CloudExifyGuessingGame/
│
├── .idea/                             # IntelliJ project configuration
│   ├── misc.xml                       # Defines SDK version (Java 26)
│   ├── modules.xml                    # Module registry
│   └── runConfigurations/             # Default run configuration
│       └── Run_Guessing_Game.xml
│
├── src/
│   └── com/
│       └── cloudexify/
│           └── guessgame/
│               ├── Main.java          # Entry point & launcher menu
│               ├── Game.java          # Core game state & logic (compatible with guide)
│               ├── ConsoleGame.java   # Retro CLI implementation (using Scanner)
│               ├── GuiGame.java       # High-fidelity GUI dashboard implementation
│               ├── SoundManager.java  # Custom synthesized MIDI sound effects
│               └── Leaderboard.java   # File I/O manager for high scores & leaderboard
│
├── CloudExifyGuessingGame.iml          # IntelliJ project module
├── README.md                          # Game documentation & guide
└── run.bat                            # Double-click script to compile and run
```

---

 Java OOP & Core Concepts Used

This application implements foundational Object-Oriented Programming (OOP) and Java principles:

1. Classes & Objects:
   - The system is decomposed into distinct classes representing logical entities. Objects like `Game` are instantiated to encapsulate the game state dynamically for each match.
2. Encapsulation:
   - Variables such as `secretNumber`, `attempts`, and `bestScore` in the `Game` class are marked `private` to hide details from external components. Access is permitted strictly through getters and setters.
3. Constructors:
   - Multiple overloaded constructors (e.g. `Game()` and `Game(difficulty, limitAttempts)`) initialize objects with different default or custom game modes.
4. Separation of Concerns (MVC-like):
   - `Game.java` acts as the "Model" containing state and math.
   - `GuiGame.java` and `ConsoleGame.java` act as the Views/Controllers" for their respective display modes.
   - `Leaderboard.java` acts as the "Data Access Layer.
   - `SoundManager.java` acts as the "Audio Output Service".
5. "Scanner & Random":
   - Standard `java.util.Scanner` scans input stream integers in Console Mode, while `java.util.Random` generates numbers between 1 and 50 (Easy) or 200 (Hard).
6. File I/O (Persistence):
   - Reads and writes to `bestscore.txt` (raw attempt count) and `leaderboard.txt` (top 5 player scores with CSV layout) using `BufferedReader`, `BufferedWriter`, `FileReader`, and `FileWriter`.

---

 Setup & Execution

 Option A: Run via Batch Script (Windows Only - Recommended)
1. Double-click the `run.bat` file in the root folder.
2. The script will automatically compile the code and ask you to select a mode:
   - Enter `1` to launch the **Graphical GUI** mode.
   - Enter `2` to run the **Console Mode** directly in your command prompt.

Option B: Open in IntelliJ IDEA
1. Open **IntelliJ IDEA**.
2. Click **Open** or **Import Project**.
3. Select the `CloudExifyGuessingGame` root directory.
4. IntelliJ will automatically detect the `.idea` folder and set up the compiler.
5. In the top-right corner, select the run configuration **Run Guessing Game** and click the green **Run (Play)** button.

 Option C: Compile and Run via Manual Terminal
Open a terminal in the root folder and run:
```cmd
javac -d out src/com/cloudexify/guessgame/*.java
java -cp out com.cloudexify.guessgame.Main
```
*To force Console Mode directly:*
```cmd
java -cp out com.cloudexify.guessgame.Main --console
```

---

 Premium Features & Bonus Challenges Done

1.  Proximity Radar (Hot / Cold Cues):
   - Calculates the distance of your current guess compared to the previous guess, printing/showing "Getting WARMER!" or "Getting COLDER!".
   - In GUI mode, a visual thermometer gauge shifts color (cool Cyan to hot Coral) based on proximity to the secret number.
2. Limited Attempts Mode:
   - Restricts attempts (7 for Easy, 10 for Hard).
   - Shows a custom progress bar in GUI that slowly depletes and blends from green/teal to red as you run out of guesses.
3.  Programmatic Midi Audio:
   - Synthesizes retro sounds using Java's built-in sound card synthesizer.
   - Distinct tones for clicks, too-low guesses, too-high guesses, victory chords, and game-over soundscapes.
4.  Leaderboard Dashboard:
   - If a player beats the top record, they are prompted to save their name.
   - Persistent top 5 leaderboard with date, difficulty, and score badges in a clean table panel.
5.  Input Sanitization:
   - Gracefully handles non-numeric keys, blank inputs, and out-of-bounds guesses without crashing.
