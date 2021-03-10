work in progress

# Implementation Document

## Project structure

This project is a Minesweeper solving algorithm implemented as a bot attached to an existing Minesweeper program. The core of the project is the classes [`MyBot`](https://github.com/maariaw/minesweeper-helper/blob/master/src/main/java/minesweeper/bot/MyBot.java), [`CSP`](https://github.com/maariaw/minesweeper-helper/blob/master/src/main/java/minesweeper/bot/CSP.java) and [`MinesweeperConstraint`](https://github.com/maariaw/minesweeper-helper/blob/master/src/main/java/minesweeper/bot/MinesweeperConstraint.java) in the `minesweeper.bot` package.

### MyBot

`MyBot` implements the interface [`Bot`](src/main/java/minesweeper/bot/Bot.java) by which the algorithm communicates with the game. The game calls the bot with two different methods to provide two different bot services to the user. The one that an actual player might use is the "Help (bot)" function, which provides colored highlights on the board to indicate if squares are safe to open or not. For this the game calls the method [`getPossibleMoves`](https://github.com/maariaw/minesweeper-helper/blob/4730eab2dabf77cdc34df8d2dedcd41ca662dded/src/main/java/minesweeper/bot/MyBot.java#L150), which returns a list of highlight-type `Move` objects. The other bot service is the "Bot Game", which makes the bot play an entire game by itself, from the opening move to a conclusion. For this the game runs a `BotExecutor`, which retrieves consecutive moves on the same `Board` object from the bot using the [`makeMove`](https://github.com/maariaw/minesweeper-helper/blob/4730eab2dabf77cdc34df8d2dedcd41ca662dded/src/main/java/minesweeper/bot/MyBot.java#L43) method. It returns one `Move` object at a time, of either opening type or flagging type. Flagging provides no benefit to the game itself, but it visualizes the data the bot has, and also just looks satisfying.

`MyBot` class has a few other methods that are helpful in setting up the algorithm with all the data it requires. They are listed below, and more detailed descriptions of the primary methods will follow.

* [`createCSP`](https://github.com/maariaw/minesweeper-helper/blob/4730eab2dabf77cdc34df8d2dedcd41ca662dded/src/main/java/minesweeper/bot/MyBot.java#L236): Construes the current situation of the board as a constraint satisfaction problem, represented as an object of class `CSP`. It collects all the unopened squares on the board as a list of variables, and also creates a map with the square-variables as keys and the domains (possible values of 0 for not mine and 1 for is mine, as array) as values. These are used as arguments to the CSP constructor.
* [`getConstrainingSquares`](https://github.com/maariaw/minesweeper-helper/blob/master/src/main/java/minesweeper/bot/MyBot.java#L262): Collects all the open squares on the board that have a number on them (so it omits the open squares that have 0 mines around them). This is useful for finding the constraints of this constraint satisfaction problem. Number squares pose the constraint on their neighbours, that the sum of mines in them must add up the number on the square.
* [`getConstrainedSquares`](https://github.com/maariaw/minesweeper-helper/blob/master/src/main/java/minesweeper/bot/MyBot.java#L279): Collects the unopened squares around a given square. It is used to find the set of neighboring squares constrained by each of the number squares.

#### getPossibleMoves

This is the more straightforward method of the two.

![A diagram of the relations of classes and packages](https://github.com/maariaw/minesweeper-helper/blob/master/documentation/images/class-package-diagram.png)

## Time and space complexities



## Flaws and improvements



## Sources
