## Week Four

### What did I do?

#### Performance improvement

I implemented some branch trimming to the CSP, so that squares that have been identified as safe or mines are never again assigned as something else. Not sure if this actually breaks something on the way, I have noticed my bot making some weird choices towards the end. Also, when the bot is used to play an entire game by itself, it saves the CSP to make use of this feature.

#### Increased automated unit testing

I'm very happy with the current test coverage. The MyBot class could have more comprehensive tests, but the few I have are meaningful (as evidenced by my finding some bugs in the code with them).

#### Documentation

I added or edited all relevant JavaDocs to the code I've written. I also started on the implementation and testing documents, although they are mostly at template level. But I noticed there's a couple of weeks to work on them, so I guess it's fine. I did make a pretty neat class/package diagram with focus on the minesweeper.bot package. I think the diagram would have gotten way too confusing, if I had tried to show the relations to classes in the minesweeper.model package, so I left it at package level and only showed the interactions between classes in the minesweeper.bot package.

#### Preparing to implement my own data structures

I read some old course materials on hashtables, but haven't started coding yet.

### What did I learn?

I see patience as one of my foremost virtues, but turns out even I can be driven to keyboard smashing meltdown. NetBeans code completion is supposed to be a friend and ally, but it suddenly refused to let me type the thing I wanted, erasing it every time without giving any explanation. In the middle of complicated refactoring that was already twisting my brain, that betrayal was the last straw. I also learned that being forced to leave my code in non-working state because of other duties can bring me to tears. It was a week of self-discovery indeed.

### What remains unclear?

Something fishy happens sometimes with the bot. I notice it sometimes opening a square that it should know is a mine. This would require some more rigid testing with pre-set seed to figure out. I have a feeling it might be an endless swamp of tweaking, so I'll get to it after all the core requirements for the project are met, if I have time.

Next I'm supposed to work on my own implementations of data structures. It feels slightly daunting, like I don't know where to start. I suppose what I need to do is write my own list, hashtable and hashmap, and switch every instance of ArrayList, HashSet and HashMap in my bot classes to those. I hope I can find some of my old excercises from the Data Structures and Algorithms course for guidance.

### Hours worked: 10
