import java.awt.Graphics2D;

abstract class Cell extends WorldObject implements Stepable {
	// Cell Metadata //
	int generation = 0;
	int children = 0;
	int lifetimeFoodEaten = 0;
	
	// Cell Data //
	int energy = GraphCell.energyUponBirth;
	int lifetime = 0;
	
	abstract void drawSenses(Graphics2D g);
}