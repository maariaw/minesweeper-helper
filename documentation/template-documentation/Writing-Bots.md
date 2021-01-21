# Writing Minesweeper bots

This document describes the required Java interfaces and the general functionality
which must be implemented in order to write bots for this Minesweeper implementation.
It also describes what data structures are made available to the bot and how the
bot can interact with those data structures.

Currently the bot will be called in two ways:
- Using the "Help (Bot)" button to ask for possible moves to make
- Using the "Bot Game" button, which will plays a full game using the bot

## Selecting the bot implementation

The currently used Bot implementation can be changed in the src/minesweeper/bot/BotSelect.java
class. This class contains a static getBot() function, where you can change the
Bot implementation to another.

## Bot interface (Bot.java)

At the core of the bot framework for this Minesweeper implementation is the Bot interface,
which any bots must implement.

The interface defines 3 methods 
```java
public Move makeMove(Board board);

public ArrayList<Move> getPossibleMoves(Board board);

public void setGameStats(GameStats gameStats); 
```
``makeMove`` is used by the Minesweeper to relay information to the bot and ask the bot to make
a decision. As a parameter, the current state of the board is given in the form of a
Board object. The bot returns the action it has taken in the form of a Move object.

In the bot implementations, this function must be overridden with your own custom
functionality that represents a single round being played. ***The bot doesn't modify the
state of the board directly but instead returns a Move object, which is processed
by the application to update the board state.**

``getPossibleMoves`` is used by the ``Help (Bot)`` funtionality. It's meant to **ONLY** return a set of Hightlight moves, 
which gives a hint for the player on the next move. I.e. red highlight for mines, green for safe square and so on.

``setGameStats`` is used to pass the ``GameStats`` object to the bot during game construction time. 
Since it is only a interface method you need to save it to some local variable if you want to use it.
``GameStats`` class can be used to access earlier move, so this is method mostly for convience reasons and has no critical functionality.

## Move class

The Move class represents a single move taken in the game. The Move class has two
constructors you can use to represent new moves:
```java
public Move(MoveType type, int x, int y)
```
and
```java
public Move(int x, int y, Highlight highlight)
```

The first constructor is a generic constructor for constructing any type of move.
The type is defined in the MoveType enum and can be HIGHLIGHT, OPEN, CHORD or FLAG.
The x and y variables refer to the grid coordinate.

The other constructor exclusively constructs Highlight-type moves and allow the bot
to highlight squares on the board with green or red colour.

## Board class

The Board class is the data type that is used as the internal representation of the
board state within the application. It is based on a two dimensional array containing
all of the individual squares as Square objects.

The primary interaction with the board happens via the ```public Square getSquareAt(int x, int y)```
method, which allows you to inspect a Square at a given coordinate.

**The Board also provides methods for opening squares, but the bot shouldn't use these itself.**

## Square class

A Square object provides access to the state of an individual Square on the board.
It has methods for querying whether a Square is a mine, how many adjacent mines it has
and allows flagging/unflagging the square.

**NOTE:** A Square must be opened before you can check if it's a mine or how many adjacent
mines it has. Attempting to read the full state of a closed Square will result in an
AssertionError.
