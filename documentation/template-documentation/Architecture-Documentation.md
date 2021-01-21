# Architecture Documentation

This document describes the architecture of the entire application
on a package and class level. It explains roughly what each class does
and how other classes interact with it and what the purpose of that
interaction is.

## Application package hierarchy

**minesweeper**
  - App.java
  - StorageSingleton.java
  - TestApp.java

**minesweeper.bot**
  - Bot.java
  - BotExecutor.java
  - BotSelect.java
  - TestBot.java

**minesweeper.model**
  - Board.java
  - GameStats.java
  - Highlight.java
  - Move.java
  - MoveType.java
  - Pair.java
  - Square.java

**minesweeper.generator**
  - MinefieldGenerator.java

**minesweeper.gui**
  - GameView.java
  - StartSelectView.java
  - StatsView.java

## Top-level package

### App.java

The App.java class contains the application execution entry-point (static void main())
and is where the application's GUI is initialized. The class will set up a JavaFX
scene and build the StartSelectView on that scene.

This class is also where the user's bot will be initialized and passed to the GUI.
Bot writers should replace the default bot implementation here with their own.

### TestApp.java

This class enables playing a series of test games with your bot without GUI.
Feel free to modify this class for your bot.

### StorageSingleton.java

This class is used for transferring a state between UI scenes without variable passing.

## bot package

The bot package defines the classes and interfaces used for creation of Minesweeper bots.

### Bot.java

Bot.java defines the interfaces which the application uses to communicate data between a
bot implementation and the game board. The interface defines one method:

```java
    public Move makeMove(Board board);
```

This method dictates that a bot implementation will receive a Board object to be inspected
and must return a Move object that represents an action to be performed upon the board.

**NOTE:** The bot must not modify the board itself!

Any bots used with this Minesweeper implementation must implement the Bot interface,
as the application only interacts with bots using this interface.

### BotSelect.java

BotSelect.java provides a single static method getBot() which is used by the GUI
to initialize the Bot implementation that you want to use, including your own.

**NOTE:** Data Structures and Algorithms projects should modify this method to
return their own Bot implementation. This class ensures that you only need to
modify this part of the program.

### BotExecutor.java

The BotExecutor.java class defines a threaded runner for running bots. The
BotExecutor provides a way for running bots on a separate thread while
communicating moves back to the original thread using a BlockingQueue<Move>
data structure.

The BotExecutor's constructor takes the BlockingQueue object, a Bot
object and a Board object. Note that the Board must not be shared
across threads and instead the threads must access separate Boards.
You can ensure that the boards are equivalent by initializing them
with MinefieldGenerators that have the same seed value and by
making sure that the same Moves are run on both boards in the same
order.

The BotExecutor is used to run bot games in the GameView.java class,
where the bot can generate moves while the GUI remains responsive and
updates the visible board with a delay to make the bot's actions
more clearly recognizable.

### TestBot.java

TestBot.java contains the default Bot implementation. This bot is not programmed
to effectively clear minefields but instead functions as a conceptual example
and demonstrates the different kinds of actions that the bots can take.

TestBot will make entirely random moves and thus it will typically quickly fail.

## model package

### Board.java

The Board.java class is the central data type in the application. It defines the 
game board as a two-dimensional array of Square objects and provides methods that
allow accessing and operating on the Square objects in order to play the game.
The Board also keeps track of whether the game has been won or lost and the total
number of mines on the field.

The Board's constructor takes a MinefieldGenerator, width, length and the number
of mines.

By default the Board is lately generated when the first square is opened using the
MinefieldGenerator that was supplied to the Board in the constructor. The generator
ensures a 3x3 safe area around the square that was opened.

The Board's method that modify the state of the Board (open(), chordedOpen() etc)
are intended to be used by the application itself. Bots should only read the state
of the Board using various accessor methods.

### Square.java

Square.java class is a representation of a single square on the Board.
The Square object contains data such as whether a square has been opened or
flagged, whether it is a mine or not and how many adjacent mines there are.

Since the Bot interacts directly with the Square objects the Square has been
programmed to not reveal important details of its state without having been
opened first. Attempting to read such important data (like if a Square is
a mine or how many adjacent mines a Square has) will result in an AssertionException.

Squares should not be opened directly by the Bot. This action should rather
be taken through the Board object in the application logic itself.

### Highlight.java

Highlight.java contains an enum of different types of highlights. This is used
for highlighting squares with various colours. A bot can set a square's highlight
by returning a Highlight-type move. This can be used for a help functionality,
where the bot makes move suggestions rather than executing them directly.

### Move.java

Move.java class represents a single move made in Minesweeper. It has a type that
determines what sort of a move it is, an X,Y coordinate and an optional Highlight
for Highlight-type moves.

Moves are created using the two main constructors:
```java
    public Move(MoveType type, int x, int y); // For open, chording and flagging
    
    public Move(int x, int y, Highlight highlight); // For making highlights
```

Moves also have fields for a timestamp and an Euclidian distance. These
are used for bot statistics and should not be modified by the bots
themselves. The timestamp is set in the Move's constructor and
the Euclidian distance is calculated by BotExecutor in comparison
with the previous Move. (Euclidian distance be used as one criterion
to compare bot efficiencies.)

### MoveType.java

MoveType.java contains an enum of different actions that can be taken in
a Minesweeper game. The types include OPEN, FLAG, CHORD and HIGHLIGHT.
This enum is used in Move.java to determine the type of the move.

### GameStats.java

GameStats.java is a class that is used for storing and updating game statistics.
It provides access to the list of moves, but it also keeps track of some
cumulative statistics, such as the cumulative euclidian distance travelled
by the virtual mouse and the cumulative time spent in the game.

### Pair.java

Pair.java is a simple implementation of a pair of two values. It is used
mainly in the Board class for bundling X,Y coordinates together.

## generator package

### MinefieldGenerator.java

MinefieldGenerator.java defines the generator for creating randomized minefields.
It is generally used by passing it as a constructor argument for Boards,
but for testing purposes a MinefieldGenerator can also generate a board separately.

The MinefieldGenerator object will generate minefields in such a way that a 3x3
safe area is generated around the given X,Y coordinate, which in actual play
is the coordinate of the first open square on the board. The minefield will
be generated by randomly picking a square among the valid squares that don't yet
have mines placed on them until all mines have been placed.

The MinefieldGenerator does not guarantee a minefield to be deterministically
solvable and may generate minefields that require a degree of luck and guess work.

## gui package

The GUI package contains classes related to JavaFX and the defining of
graphical aspects of the application.

**Note:** This package contains just little useful information for writing bots.

### GameView.java

GameView.java class defines the main GUI of the game representing the board
and serving the gameplay actions of the application. It's main purpose is
to initialize the game state with given parameters and provide controls
for the user to execute various gameplay-related actions.

The main part of the GameView is the array of buttons, which represent
the individual squares of the board. Clicking them will open or flag
the Squares stored in the Board object.

The GameView also provides buttons related to Bot functionality.
The "Bot Game" button will launch a BotExecutor thread and will begin
playing the Bot's moves from the ```BlockingQueue<Move>``` to the visible
board with a given delay. The "Help (Bot)" button on the other hand
will execute a single bot action directly to the board.

The "Statistics" button will open the StatsView for inspecting the
in-game statistics for the currently running game.

### StatsView.java

StatsView.java defines a GUI for accessing the in-game statistics.
It has a list view containing a log of the moves made in the game and the
time the player or the bot took to make those moves. It also displays
the cumulative time spent in the game and the cumulative euclidian
distance travelled by a virtual mouse.

The StatsView also allows exporting statistics into a file using
the "Export" button. This button will create a save file dialog that
asks for the name and location of the file to be created.

### StartSelectView.java

The StartSelectView.java represents the GUI that is presented to the user when the application is started. This view allows the player to select the difficulty of the board to Beginner, Intermediate, or Expert levels.

One can also set a custom sized board. A possible research task for the Tiralabra project would be to study how the bot's performance depends on the size of the board and number of mines on it.

One can also use pre-set seeds for the game. Seed determines the distribution of mines on the board. This is another useful tool for performance analysis.
