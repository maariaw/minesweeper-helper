# Project Specification

This project is a course assignment for the Data Structures Project course. I study in the Bachelor's Programme in Computer Science in Helsinki University. The project documentation language is English and programming language Java.

## Purpose of the Project

The aim of the project is to create a bot for playing minesweeper. The bot can assist the player in choosing the next move, or the player can observe the bot play a game independently.

## Algorithms

The problems posed by a minesweeper game can be structured as constraint satisfaction problems (CSP). The opened squares show indicators of how many mines the square is touching. Thus each of the neighboring squares either has mine or not, and finding combination of mines and not mines that satisfies the constraints set by the open indicators informs the next move, which introduces new constraints. I will attempt to implement the coupled subsets CSP (CSCSP) algorithm described by [Studholme (2000)](http://www.cs.toronto.edu/~cvs/minesweeper/minesweeper.pdf). In his thesis [Becerra (2015)](https://dash.harvard.edu/bitstream/handle/1/14398552/BECERRA-SENIORTHESIS-2015.pdf) compares the performance of the CSCSP (CSCSP) algorithm to another variation called connected components CSP (C3SP). There seems to be no significant differences between the solvers on beginner and intermediate levels measured by rate of winning, but CSCSP performs better on expert difficulty. Both CSP algorithms outperform single point strategies, that only take into account the constraints of one indicator square at a time.

## Input

The bot receives a [*Board*](https://github.com/maariaw/minesweeper-helper/blob/master/src/main/java/minesweeper/model/Board.java) object representing the current state of the game board. After finding a beneficial action, the bot executes it by returning a [*Move*](https://github.com/maariaw/minesweeper-helper/blob/master/src/main/java/minesweeper/model/Move.java) object. More information about the model of the game itself can be found in the template's [architecture documentation](https://github.com/maariaw/minesweeper-helper/blob/master/documentation/template-documentation/Architecture-Documentation.md).

## Time and space complexities

With 7 steps, some of which only apply to certain situations, the algorithm is too complicated for me to even guess at the O-notation beforehand. I will update this part as I attempt to write the code and gain understanding of the inner workings.

## Sources

Becerra, D. J. (2015). *Algorithmic approaches to playing minesweeper* (Unpublished bachelor's thesis). Harvard College, Cambridge, Massachusetts, United States.

Studholme, C. (2000). *Minesweeper as a constraint satisfaction problem* (Unpublished project report). University of Toronto, Toronto, Ontario, Canada.


