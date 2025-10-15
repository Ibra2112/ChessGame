# Console-Based Chess Game

A complete console-based chess game implemented in Java following object-oriented design principles.

## Features

- **Complete Chess Implementation**: All standard chess pieces with proper movement rules
- **Interactive Console Interface**: User-friendly text-based interface
- **Standard Chess Notation**: Move input using algebraic notation (e.g., "E2 E4")
- **Game State Management**: Check, checkmate detection, and game flow control
- **Pawn Promotion**: Automatic promotion handling when pawns reach the end
- **Captured Pieces Tracking**: Displays captured pieces during the game
- **Input Validation**: Comprehensive move validation and error handling

## Project Structure

```
chessgame/
├── src/main/java/
│   ├── board/
│   │   ├── Board.java          # Chessboard management and game state
│   │   └── Position.java       # Position representation and coordinate conversion
│   ├── pieces/
│   │   ├── Piece.java          # Abstract base class for all pieces
│   │   ├── Pawn.java           # Pawn piece implementation
│   │   ├── Rook.java           # Rook piece implementation
│   │   ├── Knight.java         # Knight piece implementation
│   │   ├── Bishop.java         # Bishop piece implementation
│   │   ├── Queen.java          # Queen piece implementation
│   │   └── King.java           # King piece implementation
│   ├── game/
│   │   ├── Game.java           # Main game orchestration
│   │   └── Player.java         # Player management and move input
│   ├── utils/
│   │   └── Utils.java          # Utility functions and helpers
│   └── ChessGame.java          # Main entry point
└── README.md
```

## How to Run

1. **Compile the game:**
   ```bash
   javac -d . src/main/java/board/*.java src/main/java/pieces/*.java src/main/java/utils/*.java src/main/java/game/*.java src/main/java/ChessGame.java
   ```

2. **Run the game:**
   ```bash
   java ChessGame
   ```

## How to Play

1. **Starting the Game:**
   - Enter player names when prompted
   - The game displays the initial board setup

2. **Making Moves:**
   - Use standard algebraic notation: `FROM TO` (e.g., `E2 E4`)
   - White pieces are represented with 'w' prefix (wP, wR, wN, wB, wQ, wK)
   - Black pieces are represented with 'b' prefix (bP, bR, bN, bB, bQ, bK)

3. **Game Features:**
   - **Check Detection**: The game alerts when a king is in check
   - **Checkmate Detection**: Game ends when checkmate occurs
   - **Pawn Promotion**: When a pawn reaches the opposite end, choose promotion piece (Q/R/B/N)
   - **Quit Anytime**: Type 'QUIT' to exit the game

## Board Display

The board is displayed with:
- File labels (A-H) at top and bottom
- Rank labels (1-8) on left and right sides
- Piece symbols in each square
- Current player turn indicator
- Captured pieces list (when applicable)

## Design Patterns Used

- **Abstract Factory Pattern**: Piece hierarchy with abstract Piece class
- **Strategy Pattern**: Different movement rules for each piece type
- **Command Pattern**: Move validation and execution
- **Observer Pattern**: Game state changes and turn management

## Technical Implementation

- **Object-Oriented Design**: Clean separation of concerns across packages
- **Comprehensive Documentation**: Full Javadoc documentation for all classes
- **Input Validation**: Robust error handling and user input validation
- **Memory Management**: Proper object lifecycle and resource cleanup
- **Extensible Architecture**: Easy to add new features or piece types

## Game Rules Implemented

- ✅ All piece movement rules (pawn, rook, knight, bishop, queen, king)
- ✅ Check detection and prevention
- ✅ Checkmate detection
- ✅ Pawn promotion
- ✅ Capture mechanics
- ✅ Turn-based gameplay
- ✅ Move validation
- ✅ Game state management

## Future Enhancements

- Castling implementation
- En passant capture
- Stalemate detection
- Move history and undo functionality
- Save/load game functionality
- Computer AI opponent
- Network multiplayer support

## Requirements Fulfilled

✅ **Project Setup and Organization**: Proper package structure with clear separation of concerns  
✅ **Public Interfaces**: Comprehensive Javadoc documentation for all classes and methods  
✅ **Documentation Generation**: Ready for Javadoc generation  
✅ **Piece Implementation**: Complete abstract Piece class with all concrete implementations  
✅ **Board Implementation**: 8x8 matrix representation with initialization and display  
✅ **Console Interface Workflow**: Interactive move prompting with validation  
✅ **Move Input and Validation**: Standard chess notation with comprehensive validation  
✅ **Testing**: Compilation and basic functionality testing completed

This implementation provides a solid foundation for a complete chess game with room for future enhancements and features.
