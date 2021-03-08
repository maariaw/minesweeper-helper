work in progress

# Testing Document

## Unit tests

Unit testing coverage can be viewed [here](https://codecov.io/gh/maariaw/minesweeper-helper/tree/master/src/main/java/minesweeper). Tests can also be done by running `$ ./gradlew test` and `$ ./gradlew jacocoTestReport` at the root of the project. Report can then be found at `build/reports/jacoco/test/html/index.html`. Only coverage of the `minesweeper.bot` and `minesweeper.structures` packages is reported.

Most extensive unit testing has been done for my own data structure implementations in the `minesweeper.structures` package. Since the classes are simple, testing is also simple. The main classes of the bot (`MyBot`, `CSP` and `MinesweeperConstraint` in `minesweeper.bot` package) have decent unit testing coverage. `CSP` has the primary algorithm, and it's good that its performance in simple cases is tested automatically to catch any obvious bugs. Least amount of automatic testing is done on `MyBot`, because the overall performance is best assessed by manual testing.

## Manual tests

The most important testing during the development was to run the game and see the bot in action. I regularly checked both features of the bot: the independent play and the move suggestion. Testing the independent play is easiest, since one only needs to start a new game and then press the "Bot Game" button. Under the board there's a slider to adjust the speed of the animation. For testing the move suggestions, at least an opening move has to be made, and then the "Help (bot)" button can be pressed. For testing, I opened some of the highlighted green squares and then pressed the button again, until either the game was won or it found no more safe moves. During development I also had the bot printing to console at crucial steps, so it was faster and easier to find out if and where there were problems. Any problems in the code would be apparent if the bot stopped making or suggesting moves or the moves it made or suggested were illogical. Now that the algorithm is sufficiently intelligent, there's just the satisfaction of watching the game unfold or the disappointment if the bot needs to guess and loses.

## Performance testing

Conveniently the Minesweeper template also included a template program for performance testing. I modeleled my performance testing after [Studholme (2000)](http://www.cs.toronto.edu/~cvs/minesweeper/minesweeper.pdf) and [Becerra (2015)](https://dash.harvard.edu/bitstream/handle/1/14398552/BECERRA-SENIORTHESIS-2015.pdf) to be able to make comparisons. The dimensions of the board on beginner and intermediate settings differed between the articles, and I also wanted to include testing for the default dimensions for each difficulty in this Minesweeper. So here are the settings I tested:

* Beginner - 10 mines
  * Studholme: 8 x 8
  * Becerra: 9 x 9
  * Game: 10 x 10
* Intermediate - 40 mines
  * Studholme: 15 x 13
  * Becerra/Game: 16 x 16
* Expert - 99 mines
  * All: 30 x 16

I made the bot play 100 sets of 100 games for each setting and calculated the means and standard deviations of victory rates. The tests can be replicated by running the `TestApp` class in the project, after choosing the desired settings in the code. The test results can be found in the file text.txt at the root of the project.

### Accuracy



to be included:
- Results of empirical testing presented in graphical form
- Tests should ideally be a runnable program. This makes repeating the tests easy
