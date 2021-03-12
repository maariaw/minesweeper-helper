# Minesweeper Helper

[![Build Status](https://travis-ci.com/maariaw/minesweeper-helper.svg?branch=master)](https://travis-ci.com/maariaw/minesweeper-helper)
[![codecov](https://codecov.io/gh/maariaw/minesweeper-helper/branch/master/graph/badge.svg?token=C1OX7XEAQU)](https://codecov.io/gh/maariaw/minesweeper-helper)

This is a course project for [Data Structures Lab, spring 2021](https://tiralabra.github.io/2021_p3/en/). During the course I will build a bot that assists the player in minesweeper by showing preferable moves and can also be set to play the game independently. I am adding the bots to this [minesweeper template](https://github.com/TiraLabra/minesweeper), so only selects parts in the `minesweeper.bot` package will be my code.

## Documentation

- [User guide](https://github.com/maariaw/minesweeper-helper/blob/master/documentation/User-Guide.md)
- [Project specification](https://github.com/maariaw/minesweeper-helper/blob/master/documentation/Project-Specification.md)
- [Implementation Document](https://github.com/maariaw/minesweeper-helper/blob/master/documentation/Implementation-Document.md)
- [Testing Document](https://github.com/maariaw/minesweeper-helper/blob/master/documentation/Testing-Document.md)

- Weekly reports
  - [Week one](https://github.com/maariaw/minesweeper-helper/blob/master/documentation/week-1-report.md)
  - [Week two](https://github.com/maariaw/minesweeper-helper/blob/master/documentation/week-2-report.md)
  - [Week three](https://github.com/maariaw/minesweeper-helper/blob/master/documentation/week-3-report.md)
  - [Week four](https://github.com/maariaw/minesweeper-helper/blob/master/documentation/week-4-report.md)
  - [Week five](https://github.com/maariaw/minesweeper-helper/blob/master/documentation/week-5-report.md)
  - [Week six](https://github.com/maariaw/minesweeper-helper/blob/master/documentation/week-6-report.md)

## Release

[Final submission](https://github.com/maariaw/minesweeper-helper/releases/tag/v1.0)

## Command line functions
### Running
```
$ ./gradlew run
```

### Testing
```
$ ./gradlew test
```
and for coverage
```
$ ./gradlew jacocoTestReport
```
Then see `build/reports/jacoco/test/html/index.html`. Only coverage of the `minesweeper.bot` and `minesweeper.structures` packages is reported.

### Checkstyle
```
$ ./gradlew check
```
Then see `build/reports/checkstyle/main.html`.

### JavaDoc
```
$ ./gradlew javadoc
```
Then see `build/docs/javadoc/index.html`.

### Creating an executable jar
```
$ ./gradlew shadowJar
```
It will be located in `build/libs/` as `minesweeper-helper.jar`.
