~~~ Artificial Life ~~~

An artificial life simulation. Populations of neural-networks-controlled cells evolve over time due to random mutation and natural selection.


~~ Installation ~~

Requires Java. The data folder needs to be in the same directory as the jar file.


~~ Instructions ~~

space = pause/unpause the simulation.
. = simulate 1 step (if paused).
a = toggle accelerated mode (uncapped framerate).
d = toggle the display. This significantly increases simulation speed while in accelerated mode.
m = toggle map view. 
s = toggle generation 0 cell spawning (is is recommended to disable this once a self-sustaining population has been achieved).
f = follow the selected cell.
* = toggle pointer to selected cell.
e = toggle eye-markers for the selected cell.
n = load the neural network of the selected cell into neural network viewer (currently not working).
p = print cells to file.
l = load cells from file.                                               

The mouse can be used to move around neurons in the neural network viewer.

Parameters can be set be editing data/init.txt. In particular, the map can be set to a custom image file.

When the map is loaded, the following key is used:
black = wall
red = hazard
green = plant (food-producer)
blue = movable wall
any other RGB value = empty space


~~ Feedback ~~

Email: cheeseybobdev@gmail.com
