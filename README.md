# ♟️ Chess in Java

A fully functional two-player chess game built in Java with a graphical user interface using **Java Swing**. 

---

## Features

### Gameplay
- **Local multiplayer** — two players on the same machine, alternating turns
- **Complete piece movement rules** — all 6 piece types with accurate movement logic
- **Pawn promotion** — interactive UI appears when a pawn reaches the back rank (choose Rook, Knight, Bishop, or Queen)
- **Castling** — both kingside and queenside, with correct validation
- **En passant** — tracked via a two-step flag, reset each turn

### Game Logic
- **Check detection** — highlights when the king is in check, displayed in red on screen
- **Checkmate detection** — scans all escape squares and all blocking possibilities (vertical, horizontal, diagonal, and knight attacks handled separately)
- **Stalemate detection** — triggers a draw when the opponent has only a king with no legal moves
- **Illegal move prevention** — a move is rejected if it leaves your own king in check, using a simulation layer separate from the live board

### Rendering
- **60 FPS game loop** — implemented with `Thread` + `Runnable`, delta-time based
- **Custom Swing rendering** — `paintComponent` with `Graphics2D`, anti-aliased text
- **Visual feedback** — active piece highlighted in white; illegal-but-selected squares highlighted in gray with reduced opacity
- **Status panel** — shows whose turn it is, check warnings, and a win/draw screen

---

## Technologies

| Technology | Purpose |
|------------|---------|
| Java | Core language |
| Java Swing + Graphics2D | GUI rendering, mouse input, custom painting |
| `Thread` / `Runnable` | 60 FPS game loop |
| OOP / Polymorphism | Piece hierarchy (`Piece` → `Pawn`, `Rook`, `King`, etc.) |

---

## Architecture

```
chess-java/
├── src/
│   ├── Main.java
│   ├── module/
│   │   ├── GamePanel.java     # Game loop, input handling, move validation
│   │   ├── Board.java         # Board rendering (8x8, alternating colors)
│   │   └── Mouse.java         # MouseListener / MouseMotionListener
│   └── piece/
│       ├── Piece.java         # Abstract base class
│       ├── Pawn.java          # En passant, two-step, promotion
│       ├── Rook.java
│       ├── Knight.java
│       ├── Bishop.java
│       ├── Queen.java
│       └── King.java          # Castling, illegal move check
└── README.md
```

---

## Getting Started

### Prerequisites
- Java JDK 11 or higher

### Run

```bash
git clone 
cd chess-java
javac -d out src/**/*.java
java -cp out Main
```

Or open in **IntelliJ IDEA** / **Eclipse** and run `Main.java`.

---

## How to Play

1. Launch the application — White moves first
2. Click a piece to pick it up, then click a valid square to place it
3. A white highlight shows a valid destination; gray means the move would leave your king in check
4. The game announces check in red, and ends with a win/draw screen on checkmate or stalemate

---

## Screenshots

## What I Learned

- Implementing a **game loop** with fixed timestep using Java threads and delta-time
- Applying **OOP and polymorphism** to model piece-specific movement rules
- Building a **move simulation system** to validate legality without modifying live game state
- Handling complex chess rules (castling, en passant, checkmate path-blocking) from scratch
- Working with **Java Swing** for custom rendering, mouse events, and UI overlays

---

## Author

Made by **Mara Luana Ciurte** using the tutorial made by Ryisnow.
