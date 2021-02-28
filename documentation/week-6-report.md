## Week Six

### What did I do?

#### Data structures

I finished creating all of my own data structures and replacing Java structures with them.

#### Algorithm improvements

I backtracked all the way to before all my struggles last week, then worked on the algorithm little by little, checking that what I do doesn't mess stuff up. I think I found and fixed the main culprit for my algorithm being slow and making mistakes. It was as simple as recording all squares that opened between moves. Right now I'm happy with how it works. It doesn't do overtly stupid moves, and speed is sufficient for Expert boards. It looks impressive when it's running, it's even more satisfying now that I added flagging, even though it doesn't actually improve the performance in any way. It's just for show. I do think I could tinker with the algorithm even more, but that's for later if ever.

#### Updated JavaDoc

I added missing Javadocs and updated ones that had been changed. Also added some commenting here and there to explain what's happening.

#### Cleanup

I tried to remove printouts and trashed code. I do need to add them back up if I decide to work on the algorithm some more, to see what's happening. I left a couple of printlines just commented out, because they tell what moves the bot makes when it plays independently, and you can see faster the outcome of the game than by waiting for the animation. Just for convenience for anyone looking at my project.

### What did I learn?

My lesson. Better to work incrementally, commit regularly and check that everything works. Also that I need to remember to do `git status` more often. I happily committed and pushed my project thinking I'm adding my own data structures, but the whole files had not been staged since I have a habit of adding everything with `git add -p`. With the data structures I got more practice in coding with generic types.

### What remains undone?

All my time went to fixing the algorithm and making the data structures. Documentation is where it was two weeks ago, except for JavaDoc. This weekly report is a bit late. I have not done my code review, and neither has my partner. I try to do it early next week, even if I can't get the points, just so they can get some feedback before demos. Also I have not even begun performance testing. That's something I really need to get done before the demo sessions. I can live with only getting the documentation done in time for final submission. I should also probably add unit testing for the data structures and for all the new methods in the algorithm.

### Hours worked: ~24
