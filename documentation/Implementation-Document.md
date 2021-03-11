work in progress

# Implementation Document

## Project structure

This project is a Minesweeper solving algorithm implemented as a bot attached to an existing Minesweeper program. The core of the project is the classes [`MyBot`](https://github.com/maariaw/minesweeper-helper/blob/f0f7b01a5812e2503abb7f29443dcfcd8c62a87d/src/main/java/minesweeper/bot/MyBot.java), [`CSP`](https://github.com/maariaw/minesweeper-helper/blob/f0f7b01a5812e2503abb7f29443dcfcd8c62a87d/src/main/java/minesweeper/bot/CSP.java) and [`MinesweeperConstraint`](https://github.com/maariaw/minesweeper-helper/blob/f0f7b01a5812e2503abb7f29443dcfcd8c62a87d/src/main/java/minesweeper/bot/MinesweeperConstraint.java) in the `minesweeper.bot` package.

`MyBot` implements the interface [`Bot`](https://github.com/maariaw/minesweeper-helper/blob/517601bda9a2ece8c250fb7fc497d750f32ece6f/src/main/java/minesweeper/bot/Bot.java) by which the algorithm communicates with the game. The [`botSelect`](https://github.com/maariaw/minesweeper-helper/blob/db3667080ddf1eb0cd1f16d01fe4727b95d842bf/src/main/java/minesweeper/bot/BotSelect.java) class chooses which implementation is in use. The game calls the bot with two different methods to provide two different bot services to the user. The one that an actual player might use is the "Help (bot)" function, which provides colored highlights on the board to indicate if squares are safe to open or not. For this the game calls the method [`getPossibleMoves`](https://github.com/maariaw/minesweeper-helper/blob/4730eab2dabf77cdc34df8d2dedcd41ca662dded/src/main/java/minesweeper/bot/MyBot.java#L150), which returns a list of highlight-type [`Move`](https://github.com/maariaw/minesweeper-helper/blob/db3667080ddf1eb0cd1f16d01fe4727b95d842bf/src/main/java/minesweeper/model/Move.java) objects. The other bot service is the "Bot Game", which makes the bot play an entire game by itself, from the opening move to a conclusion. For this the game runs a [`BotExecutor`](https://github.com/maariaw/minesweeper-helper/blob/db3667080ddf1eb0cd1f16d01fe4727b95d842bf/src/main/java/minesweeper/bot/BotExecutor.java), which retrieves consecutive moves on the same [`Board`](https://github.com/maariaw/minesweeper-helper/blob/db3667080ddf1eb0cd1f16d01fe4727b95d842bf/src/main/java/minesweeper/model/Board.java) object from the bot using the [`makeMove`](https://github.com/maariaw/minesweeper-helper/blob/4730eab2dabf77cdc34df8d2dedcd41ca662dded/src/main/java/minesweeper/bot/MyBot.java#L43) method. It returns one `Move` object at a time, of either opening type or flagging type. Flagging provides no benefit to the game itself, but it visualizes the data the bot has, and also just looks satisfying.

`CSP` is named after constraint satisfaction problem, which is the framework for this Minesweeper solver. A CSP solver has variables with domains. Here they are the squares, whose value can be either 0 (no mine) or 1 (mine). There's a set of constraints that inform what values the variables should take. `MinesweeperConstraint` is composed of a number and a set of `Squares` where the sum of mines should equal the number for this constraint to be satisfied. This algorithm was modeled after a coupled subsets constraint satisfaction problem solver. That means the constraints are grouped by their common variables. This is implemented by mapping each `Square` with a list of `MinesweeperConstraints` it is a part of. Solutions can be found with a backtracking search algorithm by assigning each `Square` either 0 or 1 in turn and checking if all the linked constraints can be satisfied. To lighten the load on the heavy algorithm, `CSP` includes methods for assessing constraints for immediate solutions or simplifications, by which safe squares may be found. Only when no such shortcuts are present does backtracking search occur.

![A diagram of the relations of classes and packages](https://github.com/maariaw/minesweeper-helper/blob/master/documentation/images/class-package-diagram.png)

## Time and space complexities



## Flaws and improvements

There are many ways the efficacy of this bot could be improved, if more time could be spent on development. For now, constraints are only checked for triviality, not for overlap. Studholme's (2000) algorithm includes a step where constraints are simplified, if the squares of one constraint are a subset of the squares of another constraint. This would allow more situations to be resolved without backtracking search.

There are also some opportunities for branch cutting during the search. If the total of assigned mines in a suggested assignment exceeds the total of mines on the board, the assigment cannot be a solution, and backtracking should occur. On a smaller scale, the check for constraint satisfaction returns true if all the squares in the constraints haven't yet been assigned. However, if the sum of assigned mines is already greater than the constraint allows, it could return false.

Both Studholme (2000) and Becerra (2015) have studied the strategies of the first move. This implementation of Minesweeper having quite a unique starting pattern, it would be interesting to test the different strategies and find the best.

Not enough thought and testing was put into the guessing function of MyBot for me to be confident in it. For example, in the case of an uninformed square being the least likely to be a mine, the target square should be chosen with some heuristic. Currently the move is taken on quite a haphazard manner.

It would also be cool to develop the UI so that when no safe squares are available, the Help (bot) button would show the estimated probability of each square being a mine on the square.


## Sources

Becerra, D. J. (2015). [*Algorithmic approaches to playing minesweeper*](https://dash.harvard.edu/bitstream/handle/1/14398552/BECERRA-SENIORTHESIS-2015.pdf) (Unpublished bachelor's thesis). Harvard College, Cambridge, Massachusetts, United States.

Studholme, C. (2000). [*Minesweeper as a constraint satisfaction problem*](http://www.cs.toronto.edu/~cvs/minesweeper/minesweeper.pdf) (Unpublished project report). University of Toronto, Toronto, Ontario, Canada.
