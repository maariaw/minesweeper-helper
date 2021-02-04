## Week Three

### What did I do?

#### I improved the CSP solver and made the bot helper highlights work

I started to tinker with the cps to make it more compatible with the minesweeper problem framework. To make the bot give meaningful move suggestions, I needed the solver to differentiate between certain mines/safe squares and uncertain squares. So again digging back to materials from the course in algorithms, I managed to change the code so that it doesn't just give one solution, but finds all solutions that satisfy the current constraints. Then I could make the bot compare the assignments of one square in different solutions, and if all of them were the same, it would give the appropriate highlight. It felt great to be able to see the bot correctly identifying mines.

#### I made a preliminary draft of the makeMove method

Without any work on the efficiency of the algorithm, it gets very slow on Expert. I started on refactoring to avoid doing the whole CSP setup and constraint calculations every time a move is to be made, but ran out of brain power soon after, so it shall continue next week.

#### I wrote more tests, and updated them when needed

I'm not sure if it's somehow bad programming, that I had to change my unit tests when I changed the code. But I guess it's natural, since the tests need to coordinate with changes in the output formats of methods. I did try to make sure they still test the same thing. I don't have unit tests for MyBot, since the units are still very much forming, and I don't know what will actually be used in the end. I mostly test it by running the program and using the bot to see what it does.

### What did I learn?

I re-learned some principles of recursive algorithms. They are somehow mind-boggling at times, but I just need to stare at it long enough and imagine it unfold.

### What remains unclear?

I'm a bit baffled with how to implement the constraint simplification/reducing and "throwing away" that Studholme describes. How is it indicated to the algorithm that certain variables are already known? One idea I have is to make constraints that simply state that the sum of one square needs to be zero or needs to be one. Another option could be reducing their domain to just one of the options. That would concretely speed up the algorithm by reducing branches it checks, I guess.

And then I'll need to find a way to modify remaining constraints accordingly. Like if I have a constraint saying that squares a, b and c have 2 mines between them. Then from another constraint it becomes obvious that c is a mine. I'll have to modify or replace the constraint so that there's 1 mine between a and b.

Studholme's algorithm also states that constraints are maintained, never drawn again from scratch, as my implementation does right now. I already started working on that on MyBot class, but it'd probably be better to implement that on the CPS class itself somehow. Will have to see.

Also I'm not sure how much time and effort I should allocate to streamlining the algorithm versus starting on my own implementations of the data structures.

### Hours worked: 5