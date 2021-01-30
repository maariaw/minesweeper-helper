## Week Two

### What did I do?

#### I set up Travis CI and Codecov for the project

I configured Jacoco so that only the `bot` package coverage is reported, to get a more accurate view of my own testing progress. I tried to find a way to set it up so I could show the total coverage and the bot coverage both as separate badges on README, but didn't succeed. Checkstyle was already configured in the template.

#### I tried to use a library to make a draft of the solver

I found Java libraries that should help with solving constraint satisfaction problems. I thought that could be a way to test the concept for this first week. However, when I tried to add another implementation dependency to build.gradle, it broke javafx. It was difficult to find any examples of configurations with more than one such dependency, but even when I found one and imitated that, it didn't help. So I abandoned the idea of using a library in favor of building the CSP solver myself.

#### I made a first implementation of CSP solver

I found a video that teaches how to code a CSP solver in Python. I think I managed to imitate it in Java pretty well. It's just the bare bones for this algorithm for now. I created the required classes and call it from the method that should return highlighted possible moves. For now it prints out a suggested solution. It does identify correctly the squares that definitely do have mines or don't have mines. But for the ones that are actually guesswork it just assigns the first value that it could possibly be. I need to make it differentiate between surefire squares and guesses. It also needs a lot of other work before being even close to the Studholme algorithm.

#### I created unit tests

I didn't yet manage to *comprehensively* unit test all the code I wrote. The core also doesn't yet work quite as I hope the finished algorithm to work, so I will have to think about what kinds of unit tests are most useful. The most exciting test is always running the game and seeing if my bot correctly identifies definite mines and safe squares. But I will put more effort into automated testing next week.

### What did I learn?

I learned more about how to codify constraint satisfaction problems and how to code the solver. Especially satisfying was figuring out how to formulate the constraints of a board of Minesweeper in code. I reviewed some course materials about algorithms, which helped me to understand the solver better, because it uses a depth search. I also had to review the basics of generic types, abstract classes and inheriting while coding the CSP helper classes.

I got more practice in reading documentation, when I tried to find help for configurating Travis CI, Codecov and Jacoco. It was also a lesson in patience and coping with the fact that sometimes you'll have nothing concrete to show for hours of work.

I also reviewed what I've previously studied about time and space complexities to make some preliminary estimates for the algorithm.

### What remains unclear?

Much is still unclear, but I'm very glad with my progress. I notice that the setup required for unit testing these CSP classes is challenging. I'm also not sure if I should test the move suggestions my bot makes against actual existence of mines on each square, or just that it follows the intended logic.

### Hours worked: 13
