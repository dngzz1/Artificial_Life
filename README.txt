~~~ Artificial Life ~~~

An artificial life simulation. Populations of neural-networks-controlled cells evolve over time due to random mutation and natural selection.


~~ Compiling ~~

This project uses some external library classes. You can find these at https://github.com/CheeseyBob/Utilities 


~~ Installation ~~

Requires Java. The data folder needs to be in the same directory as the jar file.


~~ Instructions ~~
arrow keys = move the view/cursor.
shift + arrow keys = move the view/cursor quickly.
space = pause/unpause the simulation.
. = simulate 1 step (if paused).
1-9 = simulation speed settings.
m = toggle between map view and follow view.
s = toggle generation 0 cell spawning (it is recommended to disable this once a self-sustaining population has been achieved).
f = follow the selected cell.
v = toggle drawing the selected cell's vision.
ctrl + p = print cells to file.
ctrl + l = load cells from file. #not currently working#
d = open all doors.
shift + d = close all doors.
ctrl + shift + d = forcibly close all doors (removing any objects in the way).

Parameters can be set be editing data/init.txt. In particular, the map can be set to a custom image file.

When the map is loaded, the following key is used:
green/yellow = summer/winter plant (food-producer)

black (255, 255, 255) = wall
red (255, 0, 0) = hazard
dark gray (51, 51, 51) = door
green (0, 255, 0) = plant which produces food in summer
yellow (255, 255, 0) = plant which produces food in winter
(102, 255, 102) = plant which produces food upon being hit
(61, 153, 61) = plant which produces tubers, which turn into food upon being pulled
any other RGB value = empty space


~~ Feedback ~~

Email: cheeseybobdev@gmail.com
