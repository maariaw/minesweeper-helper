
# Documentation for testing

## Writing tests

This document describes ways you can test your Bot implementation, both for
performance and for validating the functionality of the Bot.

## Unit testing

You can write unit tests for your bot using JUnit, which has been pre-configured
to work with the project. You can refer to ```TestBotTest.java``` as an example of how
one may write tests for a Bot implementation.

## Excluding files from JaCoCo coverage

The project is configured to use JaCoCo for generating test coverage reports.
Currently the coverage report includes all existing non-GUI code.

For Data Structures and Algorithms projects, it may be important to limit this
code coverage to just code that is specific to the project, in order to
accurately report how well the project code has been tested.

You can create JaCoCo exclusions in the ```build.gradle``` file:

Example:

```gradle

jacocoTestReport {
    reports {
        xml.enabled true
        html.enabled true
    }
     
    afterEvaluate {
        classDirectories.setFrom(files(classDirectories.files.collect {
            fileTree(dir: it, exclude: [
                'minesweeper/App*', 
                'minesweeper/gui/**', 
                'minesweeper/TestApp*',
                'minesweeper/somepackage/SomeClass*, // Exclude SomeClass from somepackage
                'minesweeper/somepackage/**          // Exclude all contents of somepackage
            ])
        }))
    } 
}
```

## Headless performance testing

You can write your test with the test class and run it with gradle task
``headlessTest``.

This allows you to, for example, create performance tests for your bots in
various gameplay situations.

To run a single game you just need place your bot the classes ``bot`` variable and a new StartSelectView and create a new TestApp class in the main method.  
The constructor creates and runs the with your bot as a player game. After that you can access the board and stats/moves with ``gameStats`` and ``board`` variables.  
  
The class currently has some example code. It runs 100 10x16 games with 50 mines. After that it prints the gamestatus, board and moves(with stats) to text.txt file.
```java
        //Values saved as Pairs, this is needed to access the values of both board and gamestats.
        ArrayList<Pair<GameStats,Board>> stats = new ArrayList<>();
        //Play 100 games and save the stats and board to array
        for(int i = 0; i < 100; i++) {
        TestApp app = new TestApp(new Random().nextLong(),10,16,50);
        stats.add(new Pair<GameStats,Board>(app.gameStats, app.board));
        }
        //Sets the out stream to file test.txt in root of project.
        try {
        System.setOut(new PrintStream(new File("test.txt")));
        } catch (Exception e){}
        //Print the stats, board and game ending status to file.
        stats.stream().forEach(s -> {
            System.out.println("---------------------");
            System.out.println("Game: " + s.getValue().gameWon);
            System.out.println(s.getValue());
            s.getKey().moves.stream().forEach(k -> System.out.println(k + " at (" + k.x + "," + k.y + ")"));
        });
```

The print for this example code looks like this:
``` lolcode
Game: false  
Field   
XXXXXXXXXXXXXXXX
XXXXXXXXXXXX*XXX
XXXXXXXXXXXXXXXX
12XXXXXXXXXXXXXX
012XXXXXXXXXXXXX
002XXXXXXXXXXXXX
113XXXXXXXXXXXXX
XXXXXXXXXXXXXXXX
112223XXXXXXXXXX
000002XXXXXXXXXX

Move: OPEN Distance: 14.42 Time: 0.00 at (1,12)
Move: OPEN Distance: 11.18 Time: 0.00 at (9,0)
Move: HIGHLIGHT Distance: 8.54 Time: 0.00 at (7,11)
Move: HIGHLIGHT Distance: 3.16 Time: 0.00 at (4,3)
Move: OPEN Distance: 3.16 Time: 0.00 at (5,0)
Move: HIGHLIGHT Distance: 0.00 Time: 0.00 at (6,3)
```

### Note

When testing headless, there is no checks on your custom board size.
Thus it is possible to test as large a board as your machine can run.
Not larger.