# akka-mars-rover


<h3>Technical Test</h3>

Mars Rover with Scala and Akka.

    Develop an actor system that represents rovers as actors that can move on a 100x100 grid.
    You are given the initial starting point (x,y) for a rover and the direction (N, E, S, W) it is facing.
    Any number of rovers can be created.
    Each rover receives a series of movement commands as messages.
    Implement commands that move the rover forward (f), backward (b), left (l) and right (r).
    At any time rovers can be asked to report their current grid position and facing direction.
    Tip: there should be unit tests.

Example data

Rover 

#1: Starting position (0, 0) and facing North. Movement list - ffrff. Ending position (2,2) E

#2: (50,50) E - ffl - (50,52) N

#3: (23,73) S - ffrblff - (24,69) S