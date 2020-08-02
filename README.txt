~~~ Artificial Life ~~~

An artificial life simulation. Populations of neural-networks-controlled cells evolve over time due to random mutation and natural selection.


~~ Installation ~~

Requires Java. The data folder needs to be in the same directory as the jar file.


~~ Instructions ~~

space = pause/unpause the simulation.
. = simulate 1 step (if paused).
1 = standard simulation speed (capped framerate; draw every frame).
2 = accelerated simulation speed (uncapped framerate; draw every frame).
3 = superfast simulation speed (uncapped framerate; draw every 10000 frames).
m = toggle between map view and follow view.
s = toggle generation 0 cell spawning (it is recommended to disable this once a self-sustaining population has been achieved).
f = follow the selected cell.
* = toggle pointer to selected cell. #not currently working#
e = toggle eye-markers for the selected cell. #not currently working#
n = load the neural network of the selected cell into neural network viewer. #not currently working#
p = print cells to file.
l = load cells from file. #not currently working#

The mouse can be used to move around neurons in the neural network viewer.

Parameters can be set be editing data/init.txt. In particular, the map can be set to a custom image file.

When the map is loaded, the following key is used:
green/yellow = summer/winter plant (food-producer)
black = wall
red = hazard
blue = immovable wall
any other RGB value = empty space


~~ Feedback ~~

Email: cheeseybobdev@gmail.com
