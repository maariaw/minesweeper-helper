## Week Five

### What did I do?

#### Code review

It was interesting to see a different kind of approach to the same topic. I found some things that I thought I could suggest improvements for, and tried to be as thorough as possible. It did take a lot of my hours this week. Familiarizing myself with the algorithms took some time, since I only glanced at the single point algorithms before starting on my CPS for my own project.

#### Data structures

I started coding my own data structures. I use so many lists and sets of Square objects, that I wanted to make a dedicated structure for them. Since the bot handles only one board at a time, and boards have one unique square tied to each location on grid, SquareList uses a 2d array is to check if a Square is already in. But for iterating it produces an array of Squares. Removal requires iterating through squares, but the game has a maximum of 2500 squares at a time, so it doesn't seem like a great issue.

#### Refactoring

I went through the code to switch most ArrayList<Square> instances to SquareList implementation. I think I already replaced some HashSets as well.

#### Struggle

Most of what I've done for my own code isn't actually visible in GitHub yet. While I was refactoring the code, I got carried away by ideas to improve the algorithm. Unfortunately I went in carelessly head first changing and tinkering here and there without taking care to make working commits and update testing. In the end I had done a lot of work for my own data structure implementing, but also got the algorithm somehow messed up and it didn't perform as well as before. I added all of it as a local branch, and will try to fix that mess next week.

### What did I learn?

To plan ahead better, to think about organizing my work into pieces that I can commit. Also that I'd relly like to be able to talk to someone about what's going on in my code. But it feels foolish to try and ask for help when I don't have specific questions in mind, just general confusion and despair.

### What remains unclear?

It's a daunting task to get everything ready for the demos. I didn't do anything for the documentation this week, and I have more structures to implement, and also have to somehow fix the mess I've made. It's all doable, just a lot of effort, and it stresses me out.

### Hours worked: 18
