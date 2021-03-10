work in progress

# Implementation Document

## Project structure

This project is a Minesweeper solving algorithm implemented as a bot attached to an existing Minesweeper program. The core of the project is the classes [`MyBot`](https://github.com/maariaw/minesweeper-helper/blob/f0f7b01a5812e2503abb7f29443dcfcd8c62a87d/src/main/java/minesweeper/bot/MyBot.java), [`CSP`](https://github.com/maariaw/minesweeper-helper/blob/f0f7b01a5812e2503abb7f29443dcfcd8c62a87d/src/main/java/minesweeper/bot/CSP.java) and [`MinesweeperConstraint`](https://github.com/maariaw/minesweeper-helper/blob/f0f7b01a5812e2503abb7f29443dcfcd8c62a87d/src/main/java/minesweeper/bot/MinesweeperConstraint.java) in the `minesweeper.bot` package.

### MyBot

`MyBot` implements the interface [`Bot`](https://github.com/maariaw/minesweeper-helper/blob/517601bda9a2ece8c250fb7fc497d750f32ece6f/src/main/java/minesweeper/bot/Bot.java) by which the algorithm communicates with the game. The game calls the bot with two different methods to provide two different bot services to the user. The one that an actual player might use is the "Help (bot)" function, which provides colored highlights on the board to indicate if squares are safe to open or not. For this the game calls the method [`getPossibleMoves`](https://github.com/maariaw/minesweeper-helper/blob/4730eab2dabf77cdc34df8d2dedcd41ca662dded/src/main/java/minesweeper/bot/MyBot.java#L150), which returns a list of highlight-type `Move` objects. The other bot service is the "Bot Game", which makes the bot play an entire game by itself, from the opening move to a conclusion. For this the game runs a `BotExecutor`, which retrieves consecutive moves on the same `Board` object from the bot using the [`makeMove`](https://github.com/maariaw/minesweeper-helper/blob/4730eab2dabf77cdc34df8d2dedcd41ca662dded/src/main/java/minesweeper/bot/MyBot.java#L43) method. It returns one `Move` object at a time, of either opening type or flagging type. Flagging provides no benefit to the game itself, but it visualizes the data the bot has, and also just looks satisfying.

`MyBot` class has a few other methods that are helpful in setting up the algorithm with all the data it requires. They are listed below, and more detailed descriptions of the primary methods will follow.

* [`createCSP`](https://github.com/maariaw/minesweeper-helper/blob/4730eab2dabf77cdc34df8d2dedcd41ca662dded/src/main/java/minesweeper/bot/MyBot.java#L236): Construes the current situation of the board as a constraint satisfaction problem, represented as an object of class `CSP`. It collects all the unopened squares on the board as a list of variables, and also creates a map with the square-variables as keys and the domains (possible values of 0 for not mine and 1 for is mine, as array) as values. These are used as arguments to the CSP constructor.
* [`getFirstMove`](https://github.com/maariaw/minesweeper-helper/blob/517601bda9a2ece8c250fb7fc497d750f32ece6f/src/main/java/minesweeper/bot/MyBot.java#L216): Calls `createCSP`, initialises a SquareSet that keeps track of opened squares with mine indicators and then returns an opening move on a square two removed from the upper left corner.
* [`getConstrainingSquares`](https://github.com/maariaw/minesweeper-helper/blob/master/src/main/java/minesweeper/bot/MyBot.java#L262): Collects all the open squares on the board that have a number on them (so it omits the open squares that have 0 mines around them). This is useful for finding the constraints of this constraint satisfaction problem. Number squares pose the constraint on their neighbours, that the sum of mines in them must add up the number on the square.
* [`getConstrainedSquares`](https://github.com/maariaw/minesweeper-helper/blob/master/src/main/java/minesweeper/bot/MyBot.java#L279): Collects the unopened squares around a given square. It is used to find the set of neighboring squares constrained by each of the number squares.

#### getPossibleMoves

This is the more straightforward method of the two. It uses `createCSP` to make a new CSP every time it is called. Then it gathers the constraints with `getConstrainingSquares` and `getConstrainedSquares` and adds them with the CSP method [`addConstraint`](https://github.com/maariaw/minesweeper-helper/blob/f0f7b01a5812e2503abb7f29443dcfcd8c62a87d/src/main/java/minesweeper/bot/CSP.java#L63). After all the constraints are added, possible redundancy is handled by running [`updateConstraints`](https://github.com/maariaw/minesweeper-helper/blob/f0f7b01a5812e2503abb7f29443dcfcd8c62a87d/src/main/java/minesweeper/bot/CSP.java#L354) until all trivial constraints have been handled (more about this later). Then it calls the CSP method [`findSafeSolutions`](https://github.com/maariaw/minesweeper-helper/blob/f0f7b01a5812e2503abb7f29443dcfcd8c62a87d/src/main/java/minesweeper/bot/CSP.java#L200) for running the backtracking search algorithm. After getting the summary, which denotes the probability of each square being a mine, it translates the safe squares into green highlight moves, certain mine squares into red highlight moves and others as black highlight moves, and finally returns them as a list.

#### makeMove

Since this method is used for making all moves on the same board, it creates only one CSP with the first move, and then keeps updating that. So first move is handled by `getFirstMove`, while subsequent moves go through several steps. Firstly, the CSP must be updated with changes that have occurred since last move. Opened squares are registered as not mines, and new numbered squares get translated to new constraints for the CSP. Constraints are then updated, which might reveal already known squares (see the part on CSP for details). The next step asks the CSP for a known safe square, and if one is available, an opening move on that is immediately returned. If not, a target for a flagging move is requested next. If no known squares are available, the next step is to call the CSP method [`findSafeSolutions`](https://github.com/maariaw/minesweeper-helper/blob/f0f7b01a5812e2503abb7f29443dcfcd8c62a87d/src/main/java/minesweeper/bot/CSP.java#L200) which runs the backtracking search and returns a mapping of squares to their probability of being a mine. Next, `makeMove` goes through every square, and if it finds a square with 0 chance of being a mine, it returns an opening move on that. In case no such squares are present, the iteration also tallies the total probability of mines in the squares that currently have constraints on them. This is done so the tally can be substracted from the total of mines still left on the board. The amount of squares not yet accounted for by any constraints is then counted and the probability of any of them being a mine can be calculated. If that is lower than any of the informed probabilities, one of these mystery squares will be chosen as target for a risky opening move.

![A diagram of the relations of classes and packages](https://github.com/maariaw/minesweeper-helper/blob/master/documentation/images/class-package-diagram.png)

## Time and space complexities



## Flaws and improvements



## Sources
